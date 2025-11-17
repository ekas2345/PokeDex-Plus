package com.ekas.pokedexplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var emailTextView: TextView
    private lateinit var welcomeTextView: TextView
    private lateinit var tvTotalFavorites: TextView
    private lateinit var tvTotalFound: TextView
    private lateinit var btnChangePassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val logoutButton = findViewById<Button>(R.id.button_logout)
        emailTextView = findViewById(R.id.text_view_user_email)
        welcomeTextView = findViewById(R.id.text_view_welcome_message)
        tvTotalFavorites = findViewById(R.id.text_view_total_favorites)
        tvTotalFound = findViewById(R.id.text_view_total_found)
        btnChangePassword = findViewById(R.id.button_change_password)

        displayUserInfo()
        fetchCollectionStats()
        setupBottomNavigation()

        btnChangePassword.setOnClickListener {
            val email = auth.currentUser?.email
            if (email != null) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password reset link sent to $email.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Failed to send reset email.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "You must be logged in to change your password.", Toast.LENGTH_SHORT).show()
            }
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun displayUserInfo() {
        val user = auth.currentUser
        if (user != null) {
            emailTextView.text = user.email ?: getString(R.string.error_not_logged_in)
            welcomeTextView.text = getString(R.string.welcome_trainer)
        } else {
            emailTextView.text = getString(R.string.error_not_logged_in)
            welcomeTextView.text = getString(R.string.auth_required)
        }
    }

    private fun fetchCollectionStats() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("favorites")
            .whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { favoritesResult ->
                val count = favoritesResult.size()
                tvTotalFavorites.text = "$count Pokémon Collected"
            }
            .addOnFailureListener {
                tvTotalFavorites.text = "0 Pokémon Collected"
            }

        db.collection("pokemon")
            .get()
            .addOnSuccessListener { pokemonResult ->
                val total = pokemonResult.size()
                tvTotalFound.text = "Total Pokedex Entries: $total"
            }
            .addOnFailureListener {
                tvTotalFound.text = "Total Pokedex Entries: Error"
            }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (bottomNavigationView == null) return

        bottomNavigationView.selectedItemId = R.id.nav_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, PokemonListActivity::class.java))
                    overridePendingTransition(0, 0); finish(); true
                }
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    overridePendingTransition(0, 0); finish(); true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }
}