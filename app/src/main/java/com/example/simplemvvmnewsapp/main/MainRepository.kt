package com.example.simplemvvmnewsapp.main

import androidx.lifecycle.LiveData
import com.example.simplemvvmnewsapp.data.models.Article
import com.example.simplemvvmnewsapp.data.models.NewsResponse
import com.example.simplemvvmnewsapp.util.Resource

interface MainRepository {
    suspend fun getNews():Resource<NewsResponse>
    suspend fun searchNews(searchQuery:String): Resource<NewsResponse>
    suspend fun upsertArticle(article: Article)
    suspend fun deleteArticle(article: Article)
    fun getAllSavedArticles():LiveData<List<Article>>
}