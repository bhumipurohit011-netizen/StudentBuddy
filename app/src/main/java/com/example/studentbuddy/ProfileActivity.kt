package com.example.studentbuddy

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

/**
 * ProfileActivity.
 * Demonstrates:
 * - SQLite data retrieval (getStudent)
 * - Displaying registered student profile
 * - UPDATE: updates both SQLite and Firebase Realtime Database
 * - DELETE: deletes from both SQLite and Firebase Realtime Database
 * - User feedback toasts
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    // Firebase Realtime Database instance
    private val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance("https://studentbuddy-af045-default-rtdb.firebaseio.com/")
    }

    private lateinit var tvNoProfile: TextView
    private lateinit var llProfileContainer: LinearLayout
    private lateinit var tvStudentId: TextView
    private lateinit var tvProfileHeaderName: TextView

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etYear: EditText
    private lateinit var etInterests: EditText
    private lateinit var etStudyMode: EditText

    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var ibBack: ImageButton

    private var currentStudentId: Int = -1
    private var originalEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        dbHelper = DatabaseHelper(this)

        // Bind UI Views
        tvNoProfile = findViewById(R.id.tvNoProfile)
        llProfileContainer = findViewById(R.id.llProfileContainer)
        tvStudentId = findViewById(R.id.tvStudentId)
        tvProfileHeaderName = findViewById(R.id.tvProfileHeaderName)

        etName = findViewById(R.id.etProfileName)
        etEmail = findViewById(R.id.etProfileEmail)
        etPhone = findViewById(R.id.etProfilePhone)
        etYear = findViewById(R.id.etProfileYear)
        etInterests = findViewById(R.id.etProfileInterests)
        etStudyMode = findViewById(R.id.etProfileStudyMode)

        btnUpdate = findViewById(R.id.btnProfileUpdate)
        btnDelete = findViewById(R.id.btnProfileDelete)
        ibBack = findViewById(R.id.ibProfileBack)

        ibBack.setOnClickListener {
            finish()
        }

        // Load data from local SQLite
        loadStudentProfile()

        // Handle Update button (SQLite + Realtime Database)
        btnUpdate.setOnClickListener {
            updateStudentProfile()
        }

        // Handle Delete button (SQLite + Realtime Database)
        btnDelete.setOnClickListener {
            deleteStudentProfile()
        }
    }

    private fun loadStudentProfile() {
        val cursor = dbHelper.getStudent()

        if (cursor != null && cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID)
            val nameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)
            val emailIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL)
            val phoneIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PHONE)
            val yearIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_YEAR)
            val interestsIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INTERESTS)
            val studyModeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STUDY_MODE)

            currentStudentId = cursor.getInt(idIndex)
            val name = cursor.getString(nameIndex)
            val email = cursor.getString(emailIndex)
            val phone = cursor.getString(phoneIndex)
            val year = cursor.getString(yearIndex)
            val interests = cursor.getString(interestsIndex)
            val studyMode = cursor.getString(studyModeIndex)

            originalEmail = email
            cursor.close()

            // Populate Views
            tvStudentId.text = "Student ID: #$currentStudentId"
            tvProfileHeaderName.text = name
            etName.setText(name)
            etEmail.setText(email)
            etPhone.setText(phone)
            etYear.setText(year)
            etInterests.setText(interests)
            etStudyMode.setText(studyMode)

            llProfileContainer.visibility = View.VISIBLE
            tvNoProfile.visibility = View.GONE
        } else {
            cursor?.close()
            currentStudentId = -1
            originalEmail = ""
            llProfileContainer.visibility = View.GONE
            tvNoProfile.visibility = View.VISIBLE
        }
    }

    private fun updateStudentProfile() {
        if (currentStudentId == -1) {
            Toast.makeText(this, "No record to update", Toast.LENGTH_SHORT).show()
            return
        }

        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val year = etYear.text.toString().trim()
        val interests = etInterests.text.toString().trim()
        val studyMode = etStudyMode.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Name, Email, and Phone cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Update SQLite
        val rows = dbHelper.updateStudent(
            currentStudentId,
            name,
            email,
            phone,
            year,
            interests,
            studyMode
        )

        if (rows > 0) {
            // 2. Update Firebase Realtime Database
            val studentData = hashMapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "year" to year,
                "interests" to interests,
                "studyMode" to studyMode
            )

            val studentsRef = firebaseDatabase.getReference("students")

            // If email changed, remove the old node and write the new one
            val newEmailKey = email.replace(".", "_")
            if (originalEmail.isNotEmpty() && originalEmail != email) {
                val oldEmailKey = originalEmail.replace(".", "_")
                studentsRef.child(oldEmailKey).removeValue()
            }
            studentsRef.child(newEmailKey).setValue(studentData)
            originalEmail = email

            tvProfileHeaderName.text = name
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteStudentProfile() {
        if (currentStudentId == -1) {
            Toast.makeText(this, "No record to delete", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Delete from SQLite
        val rows = dbHelper.deleteStudent(currentStudentId)
        if (rows > 0) {
            // 2. Delete from Firebase Realtime Database
            if (originalEmail.isNotEmpty()) {
                val emailKey = originalEmail.replace(".", "_")
                firebaseDatabase.getReference("students").child(emailKey).removeValue()
            }

            currentStudentId = -1
            originalEmail = ""
            llProfileContainer.visibility = View.GONE
            tvNoProfile.visibility = View.VISIBLE
            Toast.makeText(this, "Profile deleted successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
        }
    }
}
