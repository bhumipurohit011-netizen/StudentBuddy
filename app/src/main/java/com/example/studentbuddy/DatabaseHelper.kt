package com.example.studentbuddy

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DatabaseHelper for StudentBuddy App.
 * Manages SQLite local database operations: Create, Read, Update, Delete (CRUD).
 * Extends SQLiteOpenHelper which is Android's built-in SQLite framework.
 *
 * Version 2: Added 'notes' table for My Notes feature.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Database Details
        const val DATABASE_NAME = "StudentBuddyDB.db"
        const val DATABASE_VERSION = 2   // bumped from 1 to 2 to add notes table

        // ── Students Table ──────────────────────────────────────────────────
        const val TABLE_STUDENTS = "students"

        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_EMAIL = "email"
        const val COL_PHONE = "phone"
        const val COL_YEAR = "year"
        const val COL_INTERESTS = "interests"
        const val COL_STUDY_MODE = "study_mode"

        // ── Notes Table ─────────────────────────────────────────────────────
        const val TABLE_NOTES = "notes"

        const val COL_NOTE_ID = "id"
        const val COL_NOTE_TITLE = "title"
        const val COL_NOTE_CONTENT = "content"
    }

    // Called when the database is created for the first time
    override fun onCreate(db: SQLiteDatabase) {
        // Students table
        val createStudentsTable = (
            "CREATE TABLE $TABLE_STUDENTS ("
            + "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "$COL_NAME TEXT, "
            + "$COL_EMAIL TEXT, "
            + "$COL_PHONE TEXT, "
            + "$COL_YEAR TEXT, "
            + "$COL_INTERESTS TEXT, "
            + "$COL_STUDY_MODE TEXT)"
        )
        db.execSQL(createStudentsTable)

        // Notes table
        val createNotesTable = (
            "CREATE TABLE $TABLE_NOTES ("
            + "$COL_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "$COL_NOTE_TITLE TEXT, "
            + "$COL_NOTE_CONTENT TEXT)"
        )
        db.execSQL(createNotesTable)
    }

    // Called when the database version is bumped
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Safely add notes table without dropping the existing students table
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS $TABLE_NOTES ("
                + "$COL_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COL_NOTE_TITLE TEXT, "
                + "$COL_NOTE_CONTENT TEXT)"
            )
        }
    }

    // ── Student CRUD ─────────────────────────────────────────────────────────

    // 1. INSERT: Insert student details into SQLite
    fun insertStudent(
        name: String,
        email: String,
        phone: String,
        year: String,
        interests: String,
        studyMode: String
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_EMAIL, email)
            put(COL_PHONE, phone)
            put(COL_YEAR, year)
            put(COL_INTERESTS, interests)
            put(COL_STUDY_MODE, studyMode)
        }
        val result = db.insert(TABLE_STUDENTS, null, values)
        db.close()
        return result
    }

    // 2. RETRIEVE: Get the latest registered student record from SQLite
    fun getStudent(): Cursor? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_STUDENTS ORDER BY $COL_ID DESC LIMIT 1"
        return db.rawQuery(query, null)
    }

    // 3. UPDATE: Update existing student details by ID
    fun updateStudent(
        id: Int,
        name: String,
        email: String,
        phone: String,
        year: String,
        interests: String,
        studyMode: String
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_EMAIL, email)
            put(COL_PHONE, phone)
            put(COL_YEAR, year)
            put(COL_INTERESTS, interests)
            put(COL_STUDY_MODE, studyMode)
        }
        val rowsAffected = db.update(TABLE_STUDENTS, values, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected
    }

    // 4. DELETE: Delete a student record by ID
    fun deleteStudent(id: Int): Int {
        val db = this.writableDatabase
        val rowsDeleted = db.delete(TABLE_STUDENTS, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    // 5. COUNT: Get total registered students count from SQLite
    fun getStudentCount(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_STUDENTS", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    // ── Notes CRUD ───────────────────────────────────────────────────────────

    // 6. INSERT NOTE: Save a new note
    fun insertNote(title: String, content: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_NOTE_TITLE, title)
            put(COL_NOTE_CONTENT, content)
        }
        val result = db.insert(TABLE_NOTES, null, values)
        db.close()
        return result
    }

    // 7. GET ALL NOTES: Return all notes newest-first as Triple(id, title, content)
    fun getAllNotes(): List<Triple<Int, String, String>> {
        val notes = mutableListOf<Triple<Int, String, String>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NOTES ORDER BY $COL_NOTE_ID DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_NOTE_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE_CONTENT))
                notes.add(Triple(id, title, content))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return notes
    }

    // 8. DELETE NOTE: Delete a note by its ID
    fun deleteNote(id: Int): Int {
        val db = this.writableDatabase
        val rowsDeleted = db.delete(TABLE_NOTES, "$COL_NOTE_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }
}
