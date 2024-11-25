package com.cs407.lab6

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddNotesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)

        val notesEditText = findViewById<EditText>(R.id.notesEditText)
        val saveButton = findViewById<Button>(R.id.saveNoteButton)

        saveButton.setOnClickListener {
            val note = notesEditText.text.toString()
            if (note.isNotEmpty()) {
                // Save note logic (can use SharedPreferences, Database, etc.)
                Toast.makeText(this, "Note saved: $note", Toast.LENGTH_SHORT).show()
                notesEditText.text.clear()
            } else {
                Toast.makeText(this, "Please enter a note", Toast.LENGTH_SHORT).show()
            }
        }
    }
}