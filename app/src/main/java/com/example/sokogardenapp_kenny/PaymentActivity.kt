package com.example.sokogardenapp_kenny

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.loopj.android.http.RequestParams
import java.util.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var etLocation: TextInputEditText
    private lateinit var btnLocate: MaterialButton
    private lateinit var phoneInput: TextInputEditText
    
    private lateinit var tvSubtotal: TextView
    private lateinit var tvDeliveryFee: TextView
    private lateinit var tvTotal: TextView
    
    private var productCost: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initViews()
        loadProductData()
        setupListeners()
    }

    private fun initViews() {
        etLocation = findViewById(R.id.etLocation)
        btnLocate = findViewById(R.id.btnLocate)
        phoneInput = findViewById(R.id.phone)
        
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee)
        tvTotal = findViewById(R.id.tvTotal)
        
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun loadProductData() {
        val name = intent.getStringExtra("product_name") ?: getString(R.string.premium_electronics)
        productCost = intent.getIntExtra("product_cost", 0)
        val image = intent.getStringExtra("device_photo") ?: ""

        findViewById<TextView>(R.id.product_name).text = name
        
        // Price Breakdown
        val deliveryFee = 500
        val total = productCost + deliveryFee
        
        tvSubtotal.text = "KES ${String.format("%,d", productCost)}"
        tvDeliveryFee.text = "KES ${String.format("%,d", deliveryFee)}"
        tvTotal.text = "KES ${String.format("%,d", total)}"

        val imageUrl = if (image.startsWith("http")) image 
                       else "https://kennyfungo.alwaysdata.net/static/images/$image"

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.istockphoto)
            .into(findViewById(R.id.product_photo))
    }

    private fun setupListeners() {
        btnLocate.setOnClickListener {
            checkLocationPermissions()
        }

        findViewById<Button>(R.id.pay).setOnClickListener {
            processPayment()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getLocation()
        } else {
            Toast.makeText(this, getString(R.string.location_denied), Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            getLocation()
        }
    }

    private fun getLocation() {
        try {
            etLocation.setText(getString(R.string.locating))
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    // Using modern geocoding logic
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0].getAddressLine(0)
                        etLocation.setText(address)
                    } else {
                        etLocation.setText("Lat: ${location.latitude}, Lon: ${location.longitude}")
                    }
                } else {
                    etLocation.setText(getString(R.string.tap_to_locate))
                    Toast.makeText(this, "Unable to find location. Is GPS on?", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            etLocation.setText("Error getting location")
        }
    }

    private fun processPayment() {
        val phone = phoneInput.text.toString().trim()
        val address = etLocation.text.toString().trim()

        if (phone.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_phone_error), Toast.LENGTH_SHORT).show()
            return
        }

        if (address.isEmpty() || address == getString(R.string.tap_to_locate)) {
            Toast.makeText(this, getString(R.string.set_location_error), Toast.LENGTH_SHORT).show()
            return
        }

        val api_url = "https://kennyfungo.alwaysdata.net/api/mpesa_payment"
        val data = RequestParams().apply {
            put("amount", productCost)
            put("phone", "254$phone")
            put("location", address)
        }

        Toast.makeText(this, getString(R.string.payment_initiated), Toast.LENGTH_LONG).show()
        ApiHelper(this).post(api_url, data)
    }
}
