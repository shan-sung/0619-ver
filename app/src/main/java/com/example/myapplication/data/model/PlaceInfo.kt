package com.example.myapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class PlaceInfo(
    val source: SourceType = SourceType.CUSTOM, // 來源：GOOGLE 或 CUSTOM
    val id: String? = null,                     // Google placeId（若是 Google）
    val name: String,                           // 地點名稱（我家、台北101 等）
    val address: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val imageUrl: String? = null
) : Parcelable

@Serializable
enum class SourceType {
    GOOGLE,
    CUSTOM
}
