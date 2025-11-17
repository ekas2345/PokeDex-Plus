package com.ekas.pokedexplus

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val user = FirebaseAuth.getInstance().currentUser

            val nextActivity = if (user != null) {
                PokemonListActivity::class.java
            } else {
                LoginActivity::class.java
            }

            startActivity(Intent(this, nextActivity))
            finish()
        }, SPLASH_DELAY)
    }
}