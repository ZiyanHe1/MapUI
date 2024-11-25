package com.cs407.lab6

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

data class LocationNote(
    val location: LatLng,
    val note: String
)

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private val savedLocations = mutableListOf<LocationNote>() // List to store saved locations and notes
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Handle "Saved Locations" button click
        val savedLocationsButton = findViewById<Button>(R.id.savedLocationsButton)
        savedLocationsButton.setOnClickListener {
            // Navigate to SavedLocationsActivity and pass the saved locations
            val intent = Intent(this, SavedLocationsActivity::class.java)
            intent.putParcelableArrayListExtra("saved_locations", ArrayList(savedLocations.map { it.location }))
            intent.putStringArrayListExtra("saved_notes", ArrayList(savedLocations.map { it.note }))
            startActivity(intent)
        }

        // Handle "Add Notes" button click
        val addNotesButton = findViewById<Button>(R.id.addNotesButton)
        addNotesButton.setOnClickListener {
            val intent = Intent(this, AddNotesActivity::class.java)
            startActivityForResult(intent, 100)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a click listener to save locations
        mMap.setOnMapClickListener { latLng ->
            saveLocation(latLng)
        }

        checkLocationPermissionAndDrawPolyline()
    }

    private fun saveLocation(latLng: LatLng) {
        // Add a default note for the clicked location
        val defaultNote = "No note added yet."
        savedLocations.add(LocationNote(latLng, defaultNote))

        // Add a marker at the clicked location
        mMap.addMarker(MarkerOptions().position(latLng).title("Saved Location"))

        // Show a toast confirming the save
        Toast.makeText(this, "Location saved: (${latLng.latitude}, ${latLng.longitude})", Toast.LENGTH_SHORT).show()
    }

    private fun checkLocationPermissionAndDrawPolyline() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getUserLocationAndDrawPolyline()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocationAndDrawPolyline() {
        mFusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    setLocationMarker(currentLatLng, "Current Location")
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                } else {
                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setLocationMarker(location: LatLng, title: String) {
        mMap.addMarker(
            MarkerOptions()
                .position(location)
                .title(title)
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocationAndDrawPolyline()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val note = data.getStringExtra("note") ?: ""
            val locationIndex = savedLocations.size - 1
            if (locationIndex >= 0) {
                savedLocations[locationIndex] = savedLocations[locationIndex].copy(note = note)
                Toast.makeText(this, "Note added to last saved location", Toast.LENGTH_SHORT).show()
            }
        }
    }
}