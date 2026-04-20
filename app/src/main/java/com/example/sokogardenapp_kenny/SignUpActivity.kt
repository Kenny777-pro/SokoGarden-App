package com.example.sokogardenapp_kenny

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.loopj.android.http.RequestParams

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        import all editext
        val username =findViewById<EditText>(R.id.username)
        val email =findViewById<EditText>(R.id.email)
        val phone =findViewById<EditText>(R.id.phone)
        val password =findViewById<EditText>(R.id.password)
        val signupButton =findViewById<Button>(R.id.signup)
        signupButton.setOnClickListener {
            val api = "https://kennyfungo.alwaysdata.net/api/signup"
            val data = RequestParams()
            data.put("username", username.text.toString())
            data.put("password", password.text.toString())
            data.put("email",email.text.toString())
            data.put("phone",phone.text.toString())

//            making signup request to server
            val helper = ApiHelper(applicationContext)
            helper.post(api,data)
        }
    }
}