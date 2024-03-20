package com.example.yassinesaddikimeteoapp.android.ui.theme

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.yassinesaddikimeteoapp.Greeting
import com.example.yassinesaddikimeteoapp.android.MyApplicationTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient;
    private lateinit var locationCallback: LocationCallback
    private var locationRequired: Boolean = false

    override fun onResume(){
        super.onResume()
        if (locationRequired) startLocationUpdate();
    }

    override fun onPause() {
        super.onPause()
        locationCallback?.let {
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
    }
    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        locationCallback?.let{
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 100
            )
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(3000)
                .setMaxUpdateDelayMillis(100)
                .build()
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLocationClient()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentLocation by remember {
                        mutableStateOf()
                    }
                }
            }
        }
    }

    private fun initLocationClient() {
        fusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(this)
    }
}


