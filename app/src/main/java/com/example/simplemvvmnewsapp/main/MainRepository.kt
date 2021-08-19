package com.example.simplemvvmnewsapp.main

import com.example.simplemvvmnewsapp.data.models.NewsResponse
import com.example.simplemvvmnewsapp.util.Resource

interface MainRepository {
    suspend fun getNews():Resource<NewsResponse>
    suspend fun searchNews(searchQuery:String): Resource<NewsResponse>
}