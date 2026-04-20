package com.example.sokogardenapp_kenny

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class AboutActivity : AppCompatActivity() {

//    Declaring the tts variable
    lateinit var tts: TextToSpeech


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val about_text_view = findViewById<TextView>(R.id.about_text)
        val about_button =findViewById<Button>(R.id.speak_button)

//        using tts to turn about_text_view to speach
        tts = TextToSpeech(applicationContext){
            if(it == TextToSpeech.SUCCESS){
                tts.language = Locale.US
            }
        }

        about_button.setOnClickListener {
            val text = about_text_view.text.toString()
            tts.speak(text, TextToSpeech.QUEUE_FLUSH,null,null)

        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }


}