package com.example.myapplication.util


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.myapplication.data.Attraction
import com.example.myapplication.data.SearchItem
import com.example.myapplication.data.SearchType
import com.example.myapplication.data.Travel

fun handleSearchResultClick(
    selected: SearchItem,
    travels: List<Travel>,
    attractions: List<Attraction>,
    navController: NavController,
    context: Context
) {
    when (selected.type) {
        SearchType.TRAVEL -> {
            travels.find { it.title.equals(selected.label, ignoreCase = true) }
                ?.let { travel ->
                    navController.navigate("travel/${travel.id}")
                }
        }

        SearchType.ATTRACTION -> {
            val attraction = attractions.find { it.name.equals(selected.label, ignoreCase = true) }
            attraction?.let {
                val queryEncoded = Uri.encode(it.name)
                val gmmIntentUri = encodeMapQuery(it.name)

                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                    setPackage("com.google.android.apps.maps")
                }

                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)
                } else {
                    val webUri = "https://www.google.com/maps/search/?api=1&query=$queryEncoded".toUri()
                    context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                }
            } ?: run {
                Log.w("handleSearch", "找不到景點：${selected.label}")
            }
        }
    }
}
