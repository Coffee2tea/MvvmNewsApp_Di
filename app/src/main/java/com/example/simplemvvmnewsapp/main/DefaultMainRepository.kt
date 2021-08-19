package com.example.simplemvvmnewsapp.main

import com.example.simplemvvmnewsapp.data.NewsApi
import com.example.simplemvvmnewsapp.data.models.NewsResponse
import com.example.simplemvvmnewsapp.db.ArticleDao
import com.example.simplemvvmnewsapp.db.ArticleDatabase
import com.example.simplemvvmnewsapp.util.Resource
import javax.inject.Inject

class DefaultMainRepository @Inject constructor(
    val articleDao: ArticleDao,
    private val api: NewsApi
): MainRepository{
    override suspend fun getNews(): Resource<NewsResponse> {
        return try {
            val response = api.getBreakingNews()
            val result = response.body()
            if (response.isSuccessful && result != null){
                Resource.Success(result)
            }else{
                Resource.Error(response.message())
            }
        }catch (e:Exception){
                Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun searchNews(searchQuery:String): Resource<NewsResponse> {
        return try {
            val response = api.searchForNews(searchQuery)
            val result = response.body()
            if (response.isSuccessful && result != null){
                Resource.Success(result)
            }else{
                Resource.Error(response.message())
            }
        }catch (e:Exception){
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    fun getAllArticles() = articleDao.getAllArticles()
}