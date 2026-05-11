package com.example.sokogardenapp_kenny

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.loopj.android.http.RequestParams

class SigninActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Views
        val email = findViewById<TextInputEditText>(R.id.email)
        val password = findViewById<TextInputEditText>(R.id.password)
        val signinButton = findViewById<MaterialButton>(R.id.signin)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val cbRememberMe = findViewById<CheckBox>(R.id.cbRememberMe)
        val loginProgress = findViewById<ProgressBar>(R.id.loginProgress)
        val btnBiometric = findViewById<ImageButton>(R.id.btnBiometric)
        
        val loginCard = findViewById<MaterialCardView>(R.id.loginCard)
        val headerContainer = findViewById<View>(R.id.header_container)

        // --- PREMIUM ENTRANCE ANIMATIONS ---
        val slideUp = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        slideUp.duration = 800
        headerContainer.startAnimation(slideUp)
        loginCard.startAnimation(slideUp)

        // Remember Me Logic
        val prefs = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val savedEmail = prefs.getString("remembered_email", "")
        if (!savedEmail.isNullOrEmpty()) {
            email.setText(savedEmail)
            cbRememberMe.isChecked = true
        }

        // Navigation
        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        btnBiometric.setOnClickListener {
            Toast.makeText(this, getString(R.string.biometric_login), Toast.LENGTH_SHORT).show()
        }

        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_SHORT).show()
        }

        signinButton.setOnClickListener {
            val uEmail = email.text.toString().trim()
            val uPass = password.text.toString() // Don't trim passwords

            if (uEmail.isEmpty() || uPass.isEmpty()) {
                Toast.makeText(this, "Please enter your credentials", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Persistence
            prefs.edit {
                if (cbRememberMe.isChecked) putString("remembered_email", uEmail)
                else remove("remembered_email")
            }

            val data = RequestParams().apply {
                put("email", uEmail)
                put("password", uPass)
            }

            loginProgress.isVisible = true
            signinButton.isEnabled = false
            
            val api_url = "https://kennyfungo.alwaysdata.net/api/signin"
            ApiHelper(applicationContext).post_login(api_url, data)
        }
    }
}
