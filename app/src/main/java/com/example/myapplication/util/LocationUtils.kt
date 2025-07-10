package com.example.myapplication.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.myapplication.BuildConfig
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

suspend fun getCurrentOrFallbackLocation(context: Context): String {
    val fallbackTaiwan = listOf(
        "22.627278,120.301435",
        "23.480075,121.448874"
    ).random()

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val location = try {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.await()
        } else null
    } catch (e: Exception) {
        null
    }

    return location?.let { "${it.latitude},${it.longitude}" } ?: fallbackTaiwan
}

// 將取得的 photo_reference 轉換為可直接載入的圖片 URL
fun buildPhotoUrl(photoRef: String): String {
    return "https://maps.googleapis.com/maps/api/place/photo" +
            "?maxwidth=400&photo_reference=$photoRef&key=${BuildConfig.MAPS_API_KEY}"
}