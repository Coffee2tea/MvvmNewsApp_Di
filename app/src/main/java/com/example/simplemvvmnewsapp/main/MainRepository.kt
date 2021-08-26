package com.example.simplemvvmnewsapp.main

import androidx.lifecycle.LiveData
import com.example.simplemvvmnewsapp.data.models.Article
import com.example.simplemvvmnewsapp.data.models.NewsResponse
import com.example.simplemvvmnewsapp.util.Resource
import retrofit2.Response

interface MainRepository {

    suspend fun getNews():Response<NewsResponse>
    suspend fun searchNews(searchQuery:String): Response<NewsResponse>
    suspend fun upsertArticle(article: Article)
    suspend fun deleteArticle(article: Article)
    fun getAllSavedArticles():LiveData<List<Article>>

}