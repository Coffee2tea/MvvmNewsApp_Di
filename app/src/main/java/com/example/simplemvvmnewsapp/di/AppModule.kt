package com.example.simplemvvmnewsapp.di

import android.content.Context
import androidx.room.Room
import com.example.simplemvvmnewsapp.data.NewsApi
import com.example.simplemvvmnewsapp.db.ArticleDao
import com.example.simplemvvmnewsapp.db.ArticleDatabase
import com.example.simplemvvmnewsapp.main.DefaultMainRepository
import com.example.simplemvvmnewsapp.main.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import com.example.simplemvvmnewsapp.util.Constants.Companion.BASE_URL
import com.example.simplemvvmnewsapp.util.DispatcherProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideNewsApi():NewsApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsApi::class.java)

    @Singleton
    @Provides
    fun provideMainRepository(articleDao: ArticleDao,api: NewsApi):MainRepository = DefaultMainRepository(
        articleDao,api
    )

    @Singleton
    @Provides
    fun provideDispatcher():DispatcherProvider = object: DispatcherProvider{
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        ArticleDatabase::class.java,
        "article_db"
    ).build()

    @Singleton
    @Provides
    fun provideArticleDao(db: ArticleDatabase) = db.getArticleDao()



}
