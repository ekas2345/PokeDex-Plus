package com.ekas.pokedexplus

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager // Used for the "trophy shelf" look
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath // Needed for querying by Document ID
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var pokemonAdapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_list)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        favoritesRecyclerView = findViewById(R.id.recycler_view_pokemon)

        favoritesRecyclerView.layoutManager = GridLayoutManager(this, 2)

        pokemonAdapter = PokemonAdapter(emptyList()) { pokemon ->
            val intent = Intent(this, PokemonDetailsActivity::class.java).apply {

                putExtra("POKEMON_DOC_ID", pokemon.documentId)
            }
            startActivity(intent)
        }
        favoritesRecyclerView.adapter = pokemonAdapter

        fetchUserFavorites()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        fetchUserFavorites()
    }

    private fun fetchUserFavorites() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Please log in to view your collection.", Toast.LENGTH_LONG).show()
            pokemonAdapter.updateList(emptyList())
            return
        }

        db.collection("favorites")
            .whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { favoritesResult ->
                val pokemonIds = favoritesResult.documents.mapNotNull { it.getString("pokemonId") }

                if (pokemonIds.isEmpty()) {
                    Toast.makeText(this, "Your collection is empty. Go add some favorites!", Toast.LENGTH_SHORT).show()
                    pokemonAdapter.updateList(emptyList())
                    return@addOnSuccessListener
                }

                val queryIds = pokemonIds.take(10).toList()

                db.collection("pokemon")
                    .whereIn(FieldPath.documentId(), queryIds)
                    .get()
                    .addOnSuccessListener { pokemonResult ->
                        val favoritePokemon = pokemonResult.toObjects(Pokemon::class.java)
                        pokemonAdapter.updateList(favoritePokemon)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to load Pokémon details.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load favorites. Check Firebase rules.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (bottomNavigationView == null) return

        bottomNavigationView.selectedItemId = R.id.nav_favorites

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, PokemonListActivity::class.java))
                    overridePendingTransition(0, 0); finish(); true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0); finish(); true
                }
                R.id.nav_favorites -> true // Already here
                else -> false
            }
        }
    }
}