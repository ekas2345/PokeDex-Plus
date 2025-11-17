package com.ekas.pokedexplus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailInput = findViewById<EditText>(R.id.edit_text_email_reg)
        val passwordInput = findViewById<EditText>(R.id.edit_text_password_reg)
        val confirmPasswordInput = findViewById<EditText>(R.id.edit_text_confirm_password_reg)
        val registerButton = findViewById<Button>(R.id.button_create_account_reg)
        // --------------------------------------------------------

        auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Registration successful.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, PokemonListActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}