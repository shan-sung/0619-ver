package com.example.myapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attraction(
    val id: String,
    val name: String,
    val address: String? = null,
    val rating: Double? = null,
    val userRatingsTotal: Int? = null,
    val tags: List<String>? = null,
    val city: String = "",
    val country: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
    val openingHours: List<String>? = null,   // 🔹新增
    val comments: List<Comment>? = null,       // 🔹新增
    val lat: Double? = null,       // ← 加這行
    val lng: Double? = null
) : Parcelable