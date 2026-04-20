package com.example.sokogardenapp_kenny

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
//        Sign up intent
        val signupActivityCall = findViewById<Button>(R.id.signup)
        signupActivityCall.setOnClickListener {
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(intent)

        }

//        Sign in intent
        val signinActivity = findViewById<Button>(R.id.signin)
        signinActivity.setOnClickListener {
            val intent = Intent(applicationContext, SigninActivity::class.java)
            startActivity(intent)

//            About intent
            val aboutActivity = findViewById<Button>(R.id.about)
            aboutActivity.setOnClickListener {
                val intent = Intent(applicationContext, AboutActivity::class.java)
                startActivity(intent)


            }
//        logout documentation
            val logoutButton = findViewById<Button>(R.id.logout)
//        Extracting user details from shared preference
            val prefs = getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val username = prefs.getString("username", null)

            if (username != null) {
                signupActivityCall.visibility = View.GONE
                signinActivity.visibility = View.GONE
                logoutButton.visibility = View.VISIBLE

                Toast.makeText(this, "Welcome back $username", Toast.LENGTH_SHORT).show()
            } else {
                signinActivity.visibility = View.VISIBLE
                signupActivityCall.visibility = View.VISIBLE
                logoutButton.visibility = View.GONE
            }

            logoutButton.setOnClickListener {
                val editor = prefs.edit()
                editor.clear()
                editor.apply()
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
//            reset ui
                signinActivity.visibility = View.VISIBLE
                signupActivityCall.visibility = View.VISIBLE
                logoutButton.visibility = View.GONE
            }
//        Getting progress bar and recyclerview
            val progressBar = findViewById<ProgressBar>(R.id.progress)
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)

//        url for request
            val api_url = "https://kennyfungo.alwaysdata.net/api/get_device_details"

            val helper = ApiHelper(applicationContext)
            helper.loadProducts(api_url, recyclerView, progressBar)
        }
    }
}