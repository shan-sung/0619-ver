package com.example.myapplication.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationUtils {
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, onLocationResult: (Location?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 5000
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                Log.d("LocationUtils", "定位成功：$location")
                onLocationResult(location)
            }
            .addOnFailureListener {
                Log.e("LocationUtils", "定位失敗", it)
                onLocationResult(null)
            }
    }
}
