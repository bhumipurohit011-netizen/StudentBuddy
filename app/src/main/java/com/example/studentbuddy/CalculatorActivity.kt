package com.example.studentbuddy

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * CalculatorActivity.
 * Demonstrates:
 * - GridLayout with 16 buttons (0-9, +, -, ×, ÷, =, C)
 * - Basic arithmetic operations: Addition, Subtraction, Multiplication, Division
 * - Simple, transparent college-level calculation logic
 */
class CalculatorActivity : AppCompatActivity() {

    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView
    private lateinit var ibBack: ImageButton

    private var firstOperand: Double? = null
    private var currentOperator: String? = null
    private var isNewOperation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        tvExpression = findViewById(R.id.tvCalcExpression)
        tvResult = findViewById(R.id.tvCalcResult)
        ibBack = findViewById(R.id.ibCalcBack)

        ibBack.setOnClickListener {
            finish()
        }

        setupDigitButtons()
        setupOperatorButtons()
    }

    private fun setupDigitButtons() {
        val digitButtons = listOf(
            R.id.btn0 to "0",
            R.id.btn1 to "1",
            R.id.btn2 to "2",
            R.id.btn3 to "3",
            R.id.btn4 to "4",
            R.id.btn5 to "5",
            R.id.btn6 to "6",
            R.id.btn7 to "7",
            R.id.btn8 to "8",
            R.id.btn9 to "9"
        )

        for ((btnId, digit) in digitButtons) {
            findViewById<Button>(btnId).setOnClickListener {
                if (isNewOperation || tvResult.text.toString() == "0") {
                    tvResult.text = digit
                    isNewOperation = false
                } else {
                    tvResult.append(digit)
                }
            }
        }
    }

    private fun setupOperatorButtons() {
        // Clear (C)
        findViewById<Button>(R.id.btnC).setOnClickListener {
            firstOperand = null
            currentOperator = null
            isNewOperation = true
            tvExpression.text = ""
            tvResult.text = "0"
        }

        // Operators (+, -, ×, ÷)
        val operators = listOf(
            R.id.btnAdd to "+",
            R.id.btnSub to "-",
            R.id.btnMul to "×",
            R.id.btnDiv to "÷"
        )

        for ((btnId, op) in operators) {
            findViewById<Button>(btnId).setOnClickListener {
                val currentValue = tvResult.text.toString().toDoubleOrNull() ?: 0.0
                firstOperand = currentValue
                currentOperator = op
                tvExpression.text = "$firstOperand $op"
                isNewOperation = true
            }
        }

        // Equals (=)
        findViewById<Button>(R.id.btnEquals).setOnClickListener {
            if (firstOperand != null && currentOperator != null) {
                val secondOperand = tvResult.text.toString().toDoubleOrNull() ?: 0.0
                val result = when (currentOperator) {
                    "+" -> firstOperand!! + secondOperand
                    "-" -> firstOperand!! - secondOperand
                    "×" -> firstOperand!! * secondOperand
                    "÷" -> {
                        if (secondOperand == 0.0) {
                            tvResult.text = "Error"
                            firstOperand = null
                            currentOperator = null
                            isNewOperation = true
                            return@setOnClickListener
                        } else {
                            firstOperand!! / secondOperand
                        }
                    }
                    else -> secondOperand
                }

                tvExpression.text = "$firstOperand $currentOperator $secondOperand ="
                // Format nicely: remove trailing .0 if integer
                tvResult.text = if (result % 1.0 == 0.0) {
                    result.toLong().toString()
                } else {
                    String.format("%.4f", result).trimEnd('0').trimEnd('.')
                }

                firstOperand = null
                currentOperator = null
                isNewOperation = true
            }
        }
    }
}
