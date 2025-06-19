package com.example.myapplication.data

data class Travel(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val members: Int? = null,
    val days: Int? = null,
    val budget: Int? = null,
    val description: String? = null,
    val images: List<String>? = null
)

data class Attraction(
    val id: String,
    val name: String,
    val city: String,
    val country: String,
    val rating: Double?,
    val imageUrl: String? = null
)

data class PlacesSearchResponse(
    val results: List<PlaceResult>,
    val status: String
)

data class PlaceResult(
    val place_id: String,
    val name: String,
    val rating: Double?,
    val vicinity: String?,
    val photos: List<Photo>?,
    val geometry: Geometry
)

data class Photo(
    val photo_reference: String
)

enum class SearchType { TRAVEL, ATTRACTION }

data class SearchItem(
    val label: String,          // 要顯示的文字
    val type: SearchType        // 類型：旅程 or 景點
)


data class GeocodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    val address_components: List<AddressComponent>,
    val formatted_address: String,
    val geometry: Geometry,
    val place_id: String,
    val types: List<String>
)

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)

data class Geometry(
    val location: LatLng
)

data class LatLng(
    val lat: Double,
    val lng: Double
)
