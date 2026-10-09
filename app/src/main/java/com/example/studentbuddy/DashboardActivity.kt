package com.example.studentbuddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * DashboardActivity.
 * Main hub of StudentBuddy.
 * Built using ConstraintLayout and GridLayout.
 *
 * Demonstrates:
 * - ConstraintLayout root banner & layout
 * - GridLayout with 6 feature cards:
 *   1. 🧮 Calculator
 *   2. ⏱ Study Timer
 *   3. 📚 Resources
 *   4. 📍 Location
 *   5. 👤 Profile
 *   6. ℹ About
 * - Firebase Realtime Database live registration count display ("👥 X Students Registered")
 */
class DashboardActivity : AppCompatActivity() {

    private lateinit var tvRegisteredCount: TextView
    private lateinit var dbHelper: DatabaseHelper

    // Firebase Realtime Database instance pointing to the project database
    private val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance("https://studentbuddy-af045-default-rtdb.firebaseio.com/")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        dbHelper = DatabaseHelper(this)
        tvRegisteredCount = findViewById(R.id.tvRegisteredCount)

        // Find the 6 dashboard cards
        val cardCalculator: View = findViewById(R.id.cardCalculator)
        val cardStudyTimer: View = findViewById(R.id.cardStudyTimer)
        val cardNotes: View = findViewById(R.id.cardNotes)
        val cardLocation: View = findViewById(R.id.cardLocation)
        val cardProfile: View = findViewById(R.id.cardProfile)
        val cardAbout: View = findViewById(R.id.cardAbout)

        // 1. Calculator
        cardCalculator.setOnClickListener {
            startActivity(Intent(this, CalculatorActivity::class.java))
        }

        // 2. Study Timer
        cardStudyTimer.setOnClickListener {
            startActivity(Intent(this, StudyTimerActivity::class.java))
        }

        // 3. My Notes
        cardNotes.setOnClickListener {
            startActivity(Intent(this, NotesActivity::class.java))
        }

        // 4. My Location
        cardLocation.setOnClickListener {
            startActivity(Intent(this, LocationActivity::class.java))
        }

        // 5. Profile
        cardProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // 6. About
        cardAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        // Run Firebase connectivity test once on launch (logs only, no UI change)
        testFirebaseConnection()

        loadRegistrationCount()
    }

    override fun onResume() {
        super.onResume()
        loadRegistrationCount()
    }

    /**
     * Minimal Firebase Realtime Database connectivity test.
     * Writes a test record under "students" and reads it back.
     * Results are logged only — no UI changes.
     */
    private fun testFirebaseConnection() {
        val studentsRef = firebaseDatabase.getReference("students")
        val testRecord = mapOf(
            "name" to "Firebase Test",
            "status" to "Connected"
        )

        // Write a test child entry with a generated key
        val newEntryRef = studentsRef.push()
        newEntryRef.setValue(testRecord)
            .addOnSuccessListener {
                Log.d("FirebaseDB", "Firebase connection successful")
                // Read it back to confirm
                newEntryRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("FirebaseDB", "Read back: ${snapshot.value}")
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseDB", "Read failed: ${error.message}")
                    }
                })
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseDB", "Firebase connection failed: ${e.message}")
            }
    }

    /**
     * Retrieve the count of registered students from Firebase Realtime Database.
     * If offline or encountering network issues, seamlessly fall back to local SQLite count.
     */
    private fun loadRegistrationCount() {
        val studentsRef = firebaseDatabase.getReference("students")
        studentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount
                tvRegisteredCount.text = "👥 $count Students Registered"
            }

            override fun onCancelled(error: DatabaseError) {
                val localCount = dbHelper.getStudentCount()
                tvRegisteredCount.text = "👥 $localCount Students Registered"
            }
        })
    }
}
