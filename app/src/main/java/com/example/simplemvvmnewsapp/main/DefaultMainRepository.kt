package com.example.simplemvvmnewsapp.main

import androidx.lifecycle.LiveData
import com.example.simplemvvmnewsapp.data.NewsApi
import com.example.simplemvvmnewsapp.data.models.Article
import com.example.simplemvvmnewsapp.data.models.NewsResponse
import com.example.simplemvvmnewsapp.db.ArticleDao
import com.example.simplemvvmnewsapp.util.Resource
import retrofit2.Response
import javax.inject.Inject

class DefaultMainRepository @Inject constructor(
    val articleDao: ArticleDao,
    private val api: NewsApi
): MainRepository{

    override suspend fun getNews(countryCode:String,language:String,page:Int) = api.getBreakingNews(countryCode,language,page)

    override suspend fun getBreakingNewsInLanguage(language:String) = api.getBreakingNewsInLanguage(language)

    override suspend fun searchNews(searchQuery:String) = api.searchForNews(searchQuery)

    override fun getAllSavedArticles(): LiveData<List<Article>>{

            return articleDao.getAllArticles()
    }

    override suspend fun upsertArticle(article: Article) {
        articleDao.upsert(article)
    }

    override suspend fun deleteArticle(article: Article) {
        articleDao.deleteArticle(article)
    }


}