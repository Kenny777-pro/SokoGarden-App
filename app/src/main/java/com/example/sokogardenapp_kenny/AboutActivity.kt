package com.example.sokogardenapp_kenny

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.util.Locale

class AboutActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Remove top padding to let CollapsingToolbar handle the status bar area
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Views
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val aboutTextView = findViewById<TextView>(R.id.about_text)
        val speakButton = findViewById<MaterialButton>(R.id.speak_button)
        val aboutCard = findViewById<MaterialCardView>(R.id.aboutCard)
        val statsLayout = findViewById<View>(R.id.statsLayout)
        val valuesContainer = findViewById<View>(R.id.valuesContainer)

        // Setup Toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // --- STAGGERED ENTRANCE ANIMATIONS ---
        val slideUp = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        slideUp.duration = 800
        
        // Hide initially for animation
        aboutCard.visibility = View.INVISIBLE
        statsLayout.visibility = View.INVISIBLE
        valuesContainer.visibility = View.INVISIBLE

        // Staggered sequence
        aboutCard.postDelayed({
            aboutCard.visibility = View.VISIBLE
            aboutCard.startAnimation(slideUp)
        }, 200)

        statsLayout.postDelayed({
            statsLayout.visibility = View.VISIBLE
            statsLayout.startAnimation(slideUp)
        }, 400)

        valuesContainer.postDelayed({
            valuesContainer.visibility = View.VISIBLE
            valuesContainer.startAnimation(slideUp)
        }, 600)

        // Initialize TTS
        tts = TextToSpeech(this, this)

        speakButton.setOnClickListener {
            handleSpeech(aboutTextView.text.toString(), speakButton)
        }
    }

    private fun handleSpeech(text: String, button: MaterialButton) {
        if (isTtsInitialized) {
            if (tts?.isSpeaking == true) {
                tts?.stop()
                button.text = getString(R.string.listen_story)
            } else {
                if (text.isNotEmpty()) {
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AboutTTS")
                    button.text = getString(R.string.stop_listening)
                }
            }
        } else {
            Toast.makeText(this, "Speech engine is still initializing...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.setLanguage(Locale.US)
            isTtsInitialized = true
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
