package com.ekas.pokedexplus

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class PokemonListActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var pokemonAdapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_list)

        db = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recycler_view_pokemon)
        recyclerView.layoutManager = LinearLayoutManager(this)

        pokemonAdapter = PokemonAdapter(emptyList()) { pokemon ->
            val intent = Intent(this, PokemonDetailsActivity::class.java).apply {
                putExtra("POKEMON_DOC_ID", pokemon.documentId)
            }
            startActivity(intent)
        }
        recyclerView.adapter = pokemonAdapter

        fetchPokemonList()
        setupBottomNavigation()
    }

    private fun fetchPokemonList() {
        db.collection("pokemon")
            .get()
            .addOnSuccessListener { result ->
                val pokemonList = mutableListOf<Pokemon>()
                for (document in result) {
                    val pokemon = document.toObject(Pokemon::class.java)
                    pokemonList.add(pokemon)
                }
                pokemonAdapter.updateList(pokemonList) // Update the list view
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching data: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (bottomNavigationView == null) return

        bottomNavigationView.selectedItemId = R.id.nav_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true

                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    overridePendingTransition(0, 0); finish(); true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0); finish(); true
                }
                else -> false
            }
        }
    }
}