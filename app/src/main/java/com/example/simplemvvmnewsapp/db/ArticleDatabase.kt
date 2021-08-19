package com.example.simplemvvmnewsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.simplemvvmnewsapp.data.models.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converter::class)
abstract class ArticleDatabase: RoomDatabase()  {

    abstract fun getArticleDao(): ArticleDao

}