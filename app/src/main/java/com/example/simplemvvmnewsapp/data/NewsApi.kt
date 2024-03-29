package com.example.simplemvvmnewsapp.data

import com.example.simplemvvmnewsapp.data.models.NewsResponse
import com.example.simplemvvmnewsapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET ("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String,
        @Query("language")
        language: String,
        @Query("page")
        page: Int,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET ("v2/top-headlines")
    suspend fun getBreakingNewsInLanguage(
        @Query("language")
        language: String,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET ("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        //@Query("page")
        //pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}