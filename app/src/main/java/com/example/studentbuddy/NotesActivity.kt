package com.example.studentbuddy

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * NotesActivity.
 * My Notes screen - allows students to write, save, view and delete personal notes.
 * Notes are stored in local SQLite database via DatabaseHelper.
 * Uses programmatic view inflation for the notes list (no RecyclerView - beginner-friendly).
 */
class NotesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etNoteTitle: EditText
    private lateinit var etNoteContent: EditText
    private lateinit var btnSaveNote: Button
    private lateinit var llNotesList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        dbHelper = DatabaseHelper(this)

        // Bind views
        val ibNotesBack: ImageButton = findViewById(R.id.ibNotesBack)
        etNoteTitle = findViewById(R.id.etNoteTitle)
        etNoteContent = findViewById(R.id.etNoteContent)
        btnSaveNote = findViewById(R.id.btnSaveNote)
        llNotesList = findViewById(R.id.llNotesList)

        // Back button
        ibNotesBack.setOnClickListener { finish() }

        // Save button
        btnSaveNote.setOnClickListener { saveNote() }

        // Load existing notes on open
        loadNotes()
    }

    /**
     * Validates inputs, saves to SQLite, clears fields and refreshes list.
     */
    private fun saveNote() {
        val title = etNoteTitle.text.toString().trim()
        val content = etNoteContent.text.toString().trim()

        // Validate title
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a note title", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate content
        if (content.isEmpty()) {
            Toast.makeText(this, "Please write some note content", Toast.LENGTH_SHORT).show()
            return
        }

        // Save to SQLite
        val result = dbHelper.insertNote(title, content)

        if (result != -1L) {
            Toast.makeText(this, "Note saved successfully!", Toast.LENGTH_SHORT).show()
            etNoteTitle.text.clear()
            etNoteContent.text.clear()
            loadNotes()   // Refresh the notes list
        } else {
            Toast.makeText(this, "Failed to save note. Try again.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Reads all notes from SQLite and inflates a card for each one.
     */
    private fun loadNotes() {
        llNotesList.removeAllViews()

        val notes = dbHelper.getAllNotes()

        if (notes.isEmpty()) {
            // Show a simple "no notes" message
            val emptyView = TextView(this)
            emptyView.text = "No notes yet. Add your first note above!"
            emptyView.setTextColor(resources.getColor(R.color.text_muted, theme))
            emptyView.textSize = 14f
            emptyView.setPadding(8, 16, 8, 16)
            llNotesList.addView(emptyView)
            return
        }

        val inflater = LayoutInflater.from(this)

        for ((id, title, content) in notes) {
            // Inflate the note card layout
            val cardView = inflater.inflate(R.layout.item_note_card, llNotesList, false)

            val tvNoteTitle: TextView = cardView.findViewById(R.id.tvNoteTitle)
            val tvNoteContent: TextView = cardView.findViewById(R.id.tvNoteContent)
            val btnDelete: Button = cardView.findViewById(R.id.btnDeleteNote)

            tvNoteTitle.text = title
            tvNoteContent.text = content

            // Delete button
            btnDelete.setOnClickListener {
                dbHelper.deleteNote(id)
                Toast.makeText(this, "Note deleted successfully!", Toast.LENGTH_SHORT).show()
                loadNotes()   // Refresh after deletion
            }

            llNotesList.addView(cardView)
        }
    }
}
