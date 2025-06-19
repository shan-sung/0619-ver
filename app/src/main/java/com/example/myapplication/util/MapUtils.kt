package com.example.myapplication.util

import android.net.Uri
import androidx.core.net.toUri

fun encodeMapQuery(query: String): Uri =
    "geo:0,0?q=${Uri.encode(query)}".toUri()
