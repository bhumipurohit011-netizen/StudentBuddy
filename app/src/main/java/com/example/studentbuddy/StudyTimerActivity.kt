package com.example.studentbuddy

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity

/**
 * StudyTimerActivity.
 * Demonstrates:
 * - ToggleButton component for Study Mode ON/OFF
 * - Alternating 15-minute Study / 5-minute Break countdown cycle
 * - START, PAUSE, RESET timer controls
 * - User feedback toasts on each phase start and finish
 */
class StudyTimerActivity : AppCompatActivity() {

    private lateinit var tbStudyToggle: ToggleButton
    private lateinit var tvStudyStatus: TextView
    private lateinit var tvSessionLabel: TextView
    private lateinit var tvTimerDisplay: TextView
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnReset: Button
    private lateinit var ibBack: ImageButton

    // Timer durations
    private val studyTimeMs: Long = 15 * 60 * 1000L   // 15 minutes
    private val breakTimeMs: Long = 5 * 60 * 1000L    //  5 minutes

    private var timeLeftMs: Long = studyTimeMs
    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning: Boolean = false
    private var isStudyPhase: Boolean = true           // true = Study, false = Break

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_timer)

        tbStudyToggle = findViewById(R.id.tbStudyToggle)
        tvStudyStatus = findViewById(R.id.tvStudyStatus)
        tvSessionLabel = findViewById(R.id.tvSessionLabel)
        tvTimerDisplay = findViewById(R.id.tvTimerDisplay)
        btnStart = findViewById(R.id.btnTimerStart)
        btnPause = findViewById(R.id.btnTimerPause)
        btnReset = findViewById(R.id.btnTimerReset)
        ibBack = findViewById(R.id.ibStudyBack)

        ibBack.setOnClickListener { finish() }

        // ToggleButton: Study Mode ON/OFF (independent of timer)
        tbStudyToggle.setOnCheckedChangeListener { _, isChecked ->
            tvStudyStatus.text = if (isChecked) "Study Mode ON 📚" else "Study Mode OFF"
        }

        // START button
        btnStart.setOnClickListener {
            if (!isTimerRunning) {
                startTimer()
                Toast.makeText(this, "Study session started!", Toast.LENGTH_SHORT).show()
            }
        }

        // PAUSE button
        btnPause.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
                Toast.makeText(this, "Study session paused", Toast.LENGTH_SHORT).show()
            }
        }

        // RESET button
        btnReset.setOnClickListener {
            resetTimer()
            Toast.makeText(this, "Timer reset", Toast.LENGTH_SHORT).show()
        }

        updateTimerText()
        updateSessionLabel()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftMs = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                isTimerRunning = false
                timeLeftMs = 0
                updateTimerText()

                // Phase finished — switch to the other phase
                if (isStudyPhase) {
                    // Study finished → switch to Break
                    Toast.makeText(
                        this@StudyTimerActivity,
                        "Study session completed! Take a break. ☕",
                        Toast.LENGTH_LONG
                    ).show()
                    isStudyPhase = false
                    timeLeftMs = breakTimeMs
                } else {
                    // Break finished → switch back to Study
                    Toast.makeText(
                        this@StudyTimerActivity,
                        "Break over! Back to study. 📚",
                        Toast.LENGTH_LONG
                    ).show()
                    isStudyPhase = true
                    timeLeftMs = studyTimeMs
                }

                updateSessionLabel()
                // Auto-start the next phase
                startTimer()
            }
        }.start()

        isTimerRunning = true
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        isStudyPhase = true
        timeLeftMs = studyTimeMs
        updateTimerText()
        updateSessionLabel()
    }

    private fun updateTimerText() {
        val minutes = (timeLeftMs / 1000) / 60
        val seconds = (timeLeftMs / 1000) % 60
        tvTimerDisplay.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateSessionLabel() {
        tvSessionLabel.text = if (isStudyPhase) "Study Time 📚" else "Break Time ☕"
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
