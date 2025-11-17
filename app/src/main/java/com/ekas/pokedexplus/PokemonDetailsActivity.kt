package com.ekas.pokedexplus

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class PokemonDetailsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var currentPokemonDocumentId: String? = null

    // UI elements
    private lateinit var detailName: TextView
    private lateinit var detailImage: ImageView
    private lateinit var detailAbilities: TextView
    private lateinit var detailStats: TextView
    private lateinit var addToFavoritesButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_details)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        detailName = findViewById(R.id.text_view_detail_name)
        detailImage = findViewById(R.id.image_view_detail_pokemon)
        detailAbilities = findViewById(R.id.text_view_abilities)
        detailStats = findViewById(R.id.text_view_stats)
        addToFavoritesButton = findViewById(R.id.button_add_to_favorites)
        // -------------------------

        currentPokemonDocumentId = intent.getStringExtra("POKEMON_DOC_ID")

        if (currentPokemonDocumentId != null) {
            fetchPokemonDetails(currentPokemonDocumentId!!)
            checkFavoriteStatus(currentPokemonDocumentId!!)
        } else {
            Toast.makeText(this, "Error: Pokémon ID is missing.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun fetchPokemonDetails(docId: String) {
        db.collection("pokemon").document(docId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val pokemon = document.toObject(Pokemon::class.java)

                    detailName.text = pokemon?.name
                    detailAbilities.text = pokemon?.abilities?.joinToString(", ")
                    detailStats.text = formatStats(pokemon?.stats)

                    Glide.with(this)
                        .load(pokemon?.imageUrl)
                        .into(detailImage)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load Pokémon details from database.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun formatStats(stats: Map<String, Int>?): String {
        return stats?.entries?.joinToString("\n") { (key, value) ->
            "${key.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}: $value"
        } ?: "Stats not available."
    }

    private fun checkFavoriteStatus(pokemonId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("favorites")
            .whereEqualTo("uid", userId)
            .whereEqualTo("pokemonId", pokemonId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    setFavoriteButtonState(isFavorite = true, favoriteDocRef = documents.documents[0].reference)
                } else {
                    setFavoriteButtonState(isFavorite = false, favoriteDocRef = null)
                }
            }
            .addOnFailureListener {
                setFavoriteButtonState(isFavorite = false, favoriteDocRef = null)
            }
    }

    private fun setFavoriteButtonState(isFavorite: Boolean, favoriteDocRef: DocumentReference?) {
        if (isFavorite && favoriteDocRef != null) {
            // State: REMOVE FROM FAVORITES (DELETE)
            addToFavoritesButton.text = "REMOVE FROM FAVORITES"
            addToFavoritesButton.setOnClickListener {
                removeFavorite(favoriteDocRef)
            }
        } else {
            addToFavoritesButton.text = "ADD TO FAVORITES"
            addToFavoritesButton.setOnClickListener {
                addToFavorites(currentPokemonDocumentId)
            }
        }
    }

    private fun addToFavorites(docId: String?) {
        val userId = auth.currentUser?.uid
        if (userId == null || docId == null) return

        val favorite = Favorite(
            uid = userId,
            pokemonId = docId,
            timestamp = Date()
        )

        db.collection("favorites")
            .add(favorite)
            .addOnSuccessListener { docRef ->
                Toast.makeText(this, "Added to your collection!", Toast.LENGTH_SHORT).show()
                setFavoriteButtonState(isFavorite = true, favoriteDocRef = docRef)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error adding favorite. Check Firebase rules.", Toast.LENGTH_LONG).show()
            }
    }

    private fun removeFavorite(favoriteDocRef: DocumentReference) {
        favoriteDocRef
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Removed from collection!", Toast.LENGTH_SHORT).show()
                setFavoriteButtonState(isFavorite = false, favoriteDocRef = null)
            }
            .addOnFailureListener { e ->
                Log.e("FavoriteDelete", "Deletion failed: ${e.message}")
                Toast.makeText(this, "Error removing favorite: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}