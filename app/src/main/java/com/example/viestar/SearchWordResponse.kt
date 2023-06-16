package com.example.viestar

data class SearchWordResponse(
    val word: String,
    val score: Int,
    val tags: List<String>
)
