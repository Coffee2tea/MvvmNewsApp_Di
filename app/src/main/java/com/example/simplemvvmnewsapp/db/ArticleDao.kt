package com.example.simplemvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.simplemvvmnewsapp.data.models.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles():LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

}