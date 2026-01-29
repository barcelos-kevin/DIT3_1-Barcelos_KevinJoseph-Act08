package com.example.locationtrackingapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    // Map and Location Variables
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentLatLng: LatLng? = null
    private var isTrackingActive = false

    // UI Components
    private lateinit var latitudeText: TextView
    private lateinit var longitudeText: TextView
    private lateinit var accuracyText: TextView
    private lateinit var speedText: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var centerMapButton: FloatingActionButton

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val LOCATION_UPDATE_INTERVAL = 5000L // 5 seconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        initializeUIComponents()

        // Initialize location services
        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set up location callback for continuous updates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    currentLatLng = LatLng(location.latitude, location.longitude)
                    updateMapLocation(currentLatLng!!, location)
                }
            }
        }

        // Check and request permissions
        if (!hasLocationPermission()) {
            requestLocationPermission()
        }
    }

    private fun initializeUIComponents() {
        // Get references to UI components
        latitudeText = findViewById(R.id.latitudeText)
        longitudeText = findViewById(R.id.longitudeText)
        accuracyText = findViewById(R.id.accuracyText)
        speedText = findViewById(R.id.speedText)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        centerMapButton = findViewById(R.id.centerMapButton)

        // Set up button click listeners
        startButton.setOnClickListener {
            startTracking()
        }

        stopButton.setOnClickListener {
            stopTracking()
        }

        centerMapButton.setOnClickListener {
            if (currentLatLng != null && ::mMap.isInitialized) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, 15f))
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Set default camera to a general location (e.g., San Francisco)
        val defaultLocation = LatLng(37.7749, -122.4194)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

        // Start location updates if permissions are granted
        if (hasLocationPermission()) {
            startTracking()
        }
    }

    private fun updateMapLocation(latLng: LatLng, location: Location? = null) {
        // Clear previous markers
        mMap.clear()

        // Add new marker at current location
        mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Current Location")
                .snippet("Lat: ${String.format("%.4f", latLng.latitude)}, Lng: ${String.format("%.4f", latLng.longitude)}")
        )

        // Animate camera to new location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        // Update UI with location information
        updateLocationUI(latLng, location)
    }

    private fun updateLocationUI(latLng: LatLng, location: Location? = null) {
        // Update latitude and longitude
        latitudeText.text = "Latitude: ${String.format("%.4f", latLng.latitude)}°"
        longitudeText.text = "Longitude: ${String.format("%.4f", latLng.longitude)}°"

        // Update accuracy if available
        if (location != null) {
            accuracyText.text = "Accuracy: ±${location.accuracy.toInt()} meters"
            speedText.text = "Speed: ${String.format("%.1f", location.speed * 3.6)} km/h"
        } else {
            accuracyText.text = "Accuracy: --"
            speedText.text = "Speed: --"
        }
    }

    private fun startTracking() {
        if (isTrackingActive) {
            Toast.makeText(this, "Tracking already active", Toast.LENGTH_SHORT).show()
            return
        }

        startLocationUpdates()
        isTrackingActive = true
        startButton.isEnabled = false
        stopButton.isEnabled = true
        Toast.makeText(this, R.string.tracking_started, Toast.LENGTH_SHORT).show()
    }

    private fun stopTracking() {
        if (!isTrackingActive) {
            Toast.makeText(this, "Tracking not active", Toast.LENGTH_SHORT).show()
            return
        }

        stopLocationUpdates()
        isTrackingActive = false
        startButton.isEnabled = true
        stopButton.isEnabled = false
        Toast.makeText(this, R.string.tracking_stopped, Toast.LENGTH_SHORT).show()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL
        ).build()

        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null
                )
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error starting location updates: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (e: Exception) {
            Toast.makeText(this, "Error stopping location updates: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, you might want to request BACKGROUND_LOCATION separately
            // but it's not required for this basic app
        }

        ActivityCompat.requestPermissions(
            this,
            permissions.toTypedArray(),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show()
                if (::mMap.isInitialized) {
                    startTracking()
                }
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isTrackingActive && hasLocationPermission() && ::mMap.isInitialized) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isTrackingActive) {
            stopLocationUpdates()
        }
    }
}