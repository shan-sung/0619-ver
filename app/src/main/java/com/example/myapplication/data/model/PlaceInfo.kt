package com.example.myapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PlaceInfo(
    val source: SourceType = SourceType.CUSTOM,
    val id: String? = null,
    val name: String,
    val address: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val imageUrl: String? = null,
    val openingHours: List<String>? = null,
    val rating: Double? = null,
    val userRatingsTotal: Int? = null
) : Parcelable


@Serializable
enum class SourceType {
    GOOGLE,
    CUSTOM
}
