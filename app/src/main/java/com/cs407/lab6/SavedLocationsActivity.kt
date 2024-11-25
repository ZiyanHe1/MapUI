package com.cs407.lab6

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListView

class SavedLocationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_locations)

        val savedLocationsListView: ListView = findViewById(R.id.savedLocationsListView)

        // Example saved locations
        val savedLocations = listOf(
            "Bascom Hall, Madison",
            "Chazen Museum, Madison",
            "State Street, Madison"
        )

        // Set up the adapter
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            savedLocations
        )
        savedLocationsListView.adapter = adapter
    }
}