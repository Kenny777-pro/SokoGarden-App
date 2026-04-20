package com.example.sokogardenapp_kenny

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.loopj.android.http.RequestParams

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        Retrieve views from the payment_activity template
        val productName =findViewById<TextView>(R.id.product_name)
        val productCost =findViewById<TextView>(R.id.product_cost)
        val productPhoto =findViewById<ImageView>(R.id.product_photo)

//        Extracting values from the intent sent
        val name =intent.getStringExtra("product_name")
        val cost =intent.getIntExtra("product_cost",0)
        val image =intent.getStringExtra("product_photo")

//        connecting the actual value to our payment_activity
        productName.text = name
        productCost.text = "KES $cost"

//        using glide to show image

        val imageUrl = "https://kennyfungo.alwaysdata.net/static/images/${image}"

        //Load image using Glide, Load Faster with Glide
        Glide.with(applicationContext)
            .load(imageUrl )
            .placeholder(R.drawable.ic_launcher_background) // Make sure you have a placeholder image
            .into(productPhoto)


//        Making the actual request
//        fetch user details
        val phone = findViewById<EditText>(R.id.phone)


        val purchaseButton =findViewById<Button>(R.id.pay)
        purchaseButton.setOnClickListener {
            val api_url = "https://kennyfungo.alwaysdata.net/api/mpesa_payment"

            val data = RequestParams()
            data.put("amount", cost)
            data.put("phone", phone)

//            access api helper to use post function
            val helper = ApiHelper(applicationContext)
            helper.post(api_url, data)
        }

    }
}