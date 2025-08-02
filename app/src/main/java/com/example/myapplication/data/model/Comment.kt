package com.example.myapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Comment(
    val id: String,
    val user: String,
    val rating: Int,
    val text: String
) : Parcelable