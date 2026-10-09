package com.example.studentbuddy

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast

/**
 * Utility helper to display Custom Toast and standard toasts.
 * Demonstrates Android Practical #12: Custom Toast.
 */
object ToastUtils {

    fun showCustomToast(context: Context, message: String) {
        try {
            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.custom_toast, null)
            val textView: TextView = layout.findViewById(R.id.toast_text)
            textView.text = message

            val toast = Toast(context.applicationContext)
            toast.duration = Toast.LENGTH_SHORT
            @Suppress("DEPRECATION")
            toast.view = layout
            toast.show()
        } catch (e: Exception) {
            // Fallback to standard Toast if custom toast view is restricted on newer API levels
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
