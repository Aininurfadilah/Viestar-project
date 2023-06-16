package com.example.viestar

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("words")
    fun searchWordName(
        @Query("ml") ml: String
    ): Call<List<SearchWordResponse>>
}
