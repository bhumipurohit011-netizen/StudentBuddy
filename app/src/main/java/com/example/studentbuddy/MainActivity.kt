package com.example.studentbuddy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * MainActivity - Primary Launcher and Login Activity for StudentBuddy.
 * 
 * Flow:
 * 1. Initial 2-second Splash Screen showing app title and tagline.
 * 2. Smoothly transitions to the Login Form.
 * 3. Validates email & password and navigates to Dashboard.
 * 4. Provides direct access to Registration.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var llSplashContainer: LinearLayout
    private lateinit var llLoginContainer: ScrollView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var ibLoginIcon: ImageButton
    private lateinit var tvRegisterLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Splash & Login Containers
        llSplashContainer = findViewById(R.id.llSplashContainer)
        llLoginContainer = findViewById(R.id.llLoginContainer)

        // Initialize Login UI Components
        etEmail = findViewById(R.id.etLoginEmail)
        etPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        ibLoginIcon = findViewById(R.id.ibLoginIcon)
        tvRegisterLink = findViewById(R.id.tvRegisterLink)

        // Splash screen 2-second delay on fresh launch
        if (savedInstanceState == null) {
            llSplashContainer.visibility = View.VISIBLE
            llLoginContainer.visibility = View.GONE

            Handler(Looper.getMainLooper()).postDelayed({
                llSplashContainer.visibility = View.GONE
                llLoginContainer.visibility = View.VISIBLE
            }, 2000)
        } else {
            llSplashContainer.visibility = View.GONE
            llLoginContainer.visibility = View.VISIBLE
        }

        // ImageButton click listener
        ibLoginIcon.setOnClickListener {
            Toast.makeText(this, "StudentBuddy Secure Login", Toast.LENGTH_SHORT).show()
        }

        // Login Button validation and navigation
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validate: empty fields check
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                // Filled fields -> open Dashboard
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Navigate to Registration Screen
        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
