package com.example.viestar.ui.study

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Word(
    val name: String,
) : Parcelable
