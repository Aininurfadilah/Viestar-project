package com.example.viestar.ui.home.tab.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Film(
    val name: String,
    val description: String,
    val photo: Int
) : Parcelable