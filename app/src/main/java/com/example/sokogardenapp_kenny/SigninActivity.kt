package com.example.sokogardenapp_kenny

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
//        signin verification implementation
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val signinButton =findViewById<Button>(R.id.signin)

        signinButton.setOnClickListener {
//            get user details
            val data = RequestParams()
            data.put("email",email.text.toString())
            data.put("password",password.text.toString())

//            establish connection
            val api_url = "https://kennyfungo.alwaysdata.net/api/signin"

//            making the signin request
            val helper = ApiHelper(applicationContext)
            helper.post_login(api_url,data)
        }
    }
}