package com.example.studentbuddy

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

/**
 * AboutActivity.
 * Demonstrates:
 * - About screen presentation
 * - App metadata, purpose, and technology stack overview
 */
class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val ibBack: ImageButton = findViewById(R.id.ibAboutBack)
        ibBack.setOnClickListener {
            finish()
        }
    }
}
