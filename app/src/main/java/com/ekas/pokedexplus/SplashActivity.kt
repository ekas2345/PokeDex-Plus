package com.ekas.pokedexplus // Use your actual package name

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    // Define the delay time in milliseconds (e.g., 2000 ms = 2 seconds)
    private val SPLASH_DELAY: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the layout file to display
        setContentView(R.layout.activity_splash)

        // Use a Handler to delay the execution of code
        Handler(Looper.getMainLooper()).postDelayed({
            // 1. Create an Intent to navigate to the Login Screen (or your next main screen)
            val intent = Intent(this, SplashActivity::class.java)

            // 2. Start the new Activity
            startActivity(intent)

            // 3. Finish the current Splash Activity so the user can't go back to it
            finish()
        }, SPLASH_DELAY)
    }
}