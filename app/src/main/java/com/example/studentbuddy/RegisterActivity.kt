package com.example.studentbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var rgYear: RadioGroup
    private lateinit var cbCoding: CheckBox
    private lateinit var cbSports: CheckBox
    private lateinit var cbMusic: CheckBox
    private lateinit var tbStudyMode: ToggleButton
    private lateinit var btnRegister: Button
    private lateinit var ibBack: ImageButton

    private lateinit var dbHelper: DatabaseHelper

    private val firebaseDatabase = FirebaseDatabase.getInstance(
        "https://studentbuddy-af045-default-rtdb.firebaseio.com/"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        dbHelper = DatabaseHelper(this)

        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        rgYear = findViewById(R.id.rgYear)

        cbCoding = findViewById(R.id.cbCoding)
        cbSports = findViewById(R.id.cbSports)
        cbMusic = findViewById(R.id.cbMusic)

        tbStudyMode = findViewById(R.id.tbStudyMode)
        btnRegister = findViewById(R.id.btnRegister)
        ibBack = findViewById(R.id.ibRegisterBack)

        ibBack.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            registerStudent()
        }
    }

    private fun registerStudent() {

        val name = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()

        // Check required fields
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {

            Toast.makeText(
                this,
                "Please fill all required fields",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Check year
        val selectedYearId = rgYear.checkedRadioButtonId

        if (selectedYearId == -1) {

            Toast.makeText(
                this,
                "Please select your year",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val selectedRadioButton =
            findViewById<RadioButton>(selectedYearId)

        val year = selectedRadioButton.text.toString()

        // Get interests
        val interestsList = mutableListOf<String>()

        if (cbCoding.isChecked) {
            interestsList.add("Coding")
        }

        if (cbSports.isChecked) {
            interestsList.add("Sports")
        }

        if (cbMusic.isChecked) {
            interestsList.add("Music")
        }

        val interests =
            if (interestsList.isEmpty()) {
                "None"
            } else {
                interestsList.joinToString(", ")
            }

        // Study mode
        val studyMode =
            if (tbStudyMode.isChecked) "ON" else "OFF"

        // -------------------------------
        // 1. SAVE TO SQLITE
        // -------------------------------

        val sqliteResult = dbHelper.insertStudent(
            name,
            email,
            phone,
            year,
            interests,
            studyMode
        )

        if (sqliteResult == -1L) {

            Toast.makeText(
                this,
                "SQLite registration failed",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // -------------------------------
        // 2. SAVE TO FIREBASE
        // -------------------------------

        val studentData = hashMapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "year" to year,
            "interests" to interests,
            "studyMode" to studyMode
        )

        // Create unique Firebase ID
        val studentReference =
            firebaseDatabase
                .getReference("students")
                .push()

        studentReference.setValue(studentData)

            // Firebase write successful
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Data saved to Firebase!",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(
                    this,
                    ProfileActivity::class.java
                )

                startActivity(intent)
                finish()
            }

            // Firebase write failed
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Firebase Error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}