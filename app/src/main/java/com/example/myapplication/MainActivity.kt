package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.myapplication.navigation.RootNavGraph
import com.example.myapplication.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>
    var hasLocationPermission by mutableStateOf(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 註冊權限請求回呼
        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            hasLocationPermission = isGranted
            if (isGranted) {
                Log.d("Permission", "✅ 使用者已授權定位")
            } else {
                Log.e("Permission", "❌ 使用者拒絕定位")
            }
        }

        // 啟動請求
        requestLocationPermission()

        setContent {
            AppTheme {
                if (hasLocationPermission) {
                    RootNavGraph()
                } else {
                    Text("請允許定位權限")
                }
            }
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            hasLocationPermission = true
        }
    }
}
