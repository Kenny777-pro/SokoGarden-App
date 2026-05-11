package com.example.sokogardenapp_kenny

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
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

        // Initialize Views
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)
        val username = findViewById<TextInputEditText>(R.id.username)
        val email = findViewById<TextInputEditText>(R.id.email)
        val phone = findViewById<TextInputEditText>(R.id.phone)
        val password = findViewById<TextInputEditText>(R.id.password)
        val confirmPassword = findViewById<TextInputEditText>(R.id.confirmPassword)
        val tilConfirm = findViewById<TextInputLayout>(R.id.tilConfirmPassword)
        val strengthBar = findViewById<ProgressBar>(R.id.passwordStrength)
        
        val signupButton = findViewById<MaterialButton>(R.id.signup)
        val cbTerms = findViewById<CheckBox>(R.id.cbTerms)
        val formCard = findViewById<MaterialCardView>(R.id.formCard)
        val avatarContainer = findViewById<FrameLayout>(R.id.avatar_container)
        val headerTitle = findViewById<TextView>(R.id.tvTitle)
        val chipGroup = findViewById<ChipGroup>(R.id.interestChips)

        // --- STAGGERED ENTRANCE ANIMATIONS ---
        val slideUp = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        slideUp.duration = 800
        headerTitle.startAnimation(slideUp)
        avatarContainer.startAnimation(slideUp)
        formCard.startAnimation(slideUp)

        // --- REAL-TIME PASSWORD LOGIC ---
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateStrengthBar(s.toString(), strengthBar)
                checkMatch(s.toString(), confirmPassword.text.toString(), tilConfirm)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        confirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkMatch(password.text.toString(), s.toString(), tilConfirm)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Navigation
        btnBack.setOnClickListener { finish() }
        tvSignIn.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
            finish()
        }

        signupButton.setOnClickListener {
            validateAndSubmit(username, email, phone, password, confirmPassword, cbTerms, chipGroup)
        }
    }

    private fun updateStrengthBar(pass: String, bar: ProgressBar) {
        val strength = when {
            pass.isEmpty() -> 0
            pass.length < 6 -> 25
            pass.any { it.isDigit() } && pass.any { it.isUpperCase() } -> 100
            pass.length >= 8 -> 75
            else -> 50
        }
        bar.progress = strength
        val color = when (strength) {
            25 -> R.color.error
            50 -> R.color.warning
            75 -> R.color.accent_blue
            100 -> R.color.success
            else -> R.color.text_gray
        }
        bar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(this, color))
    }

    private fun checkMatch(p1: String, p2: String, layout: TextInputLayout) {
        if (p2.isNotEmpty()) {
            if (p1 == p2) {
                layout.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.success)))
                layout.error = null
            } else {
                layout.setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.error)))
            }
        }
    }

    private fun validateAndSubmit(
        u: EditText, e: EditText, p: EditText, pw: EditText, cp: EditText, cb: CheckBox, chips: ChipGroup
    ) {
        val uName = u.text.toString().trim()
        val uEmail = e.text.toString().trim()
        val uPhone = p.text.toString().trim()
        val uPass = pw.text.toString()
        val uConfirmPass = cp.text.toString()

        if (uName.isEmpty() || uEmail.isEmpty() || uPhone.isEmpty() || uPass.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (uPass != uConfirmPass) {
            Toast.makeText(this, getString(R.string.passwords_dont_match), Toast.LENGTH_SHORT).show()
            return
        }

        if (!cb.isChecked) {
            Toast.makeText(this, getString(R.string.agree_terms_error), Toast.LENGTH_SHORT).show()
            return
        }

        // Get selected interests
        val interests = mutableListOf<String>()
        for (i in 0 until chips.childCount) {
            val child = chips.getChildAt(i)
            if (child is Chip && child.isChecked) {
                interests.add(child.text.toString())
            }
        }

        val api = "https://kennyfungo.alwaysdata.net/api/signup"
        val data = RequestParams().apply {
            put("username", uName)
            put("password", uPass)
            put("email", uEmail)
            put("phone", uPhone)
            put("interests", interests.joinToString(","))
        }

        Toast.makeText(this, "Connecting to Kent Galore...", Toast.LENGTH_SHORT).show()
        ApiHelper(this).post(api, data)
    }
}
