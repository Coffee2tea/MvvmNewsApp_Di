package com.example.simplemvvmnewsapp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplemvvmnewsapp.data.models.Article
import com.example.simplemvvmnewsapp.data.models.NewsResponse
import com.example.simplemvvmnewsapp.util.DispatcherProvider
import com.example.simplemvvmnewsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: DispatcherProvider
):ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsResponse: NewsResponse? = null
    var breakingNewsPage = 1

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsResponse: NewsResponse? = null
    var searchNewsPage = 1

init {
    getBreakingNews("ca","en")
}

    fun getBreakingNews(country:String,language:String) = viewModelScope.launch(dispatcher.io){
            breakingNews.postValue(Resource.Loading())

            val response = repository.getNews(country,language)

            breakingNews.postValue(handleBreakingNews(response))
        }

    fun searchForNews(searchQuery:String) = viewModelScope.launch(dispatcher.io){
        searchNews.postValue(Resource.Loading())
        val response = repository.searchNews(searchQuery)

        searchNewsResponse = null
        searchNewsPage = 1

        searchNews.postValue(handleSearchNews(response))
    }

    fun saveArticle(article: Article) = viewModelScope.launch(dispatcher.io) {
        repository.upsertArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch(dispatcher.io){
        repository.deleteArticle(article)
    }

    fun getAllSavedNews() = repository.getAllSavedArticles()

    private fun handleBreakingNews(response: Response<NewsResponse>):Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body() ?.let { responseResult->

                breakingNewsPage++
                if (breakingNewsResponse == null){
                    breakingNewsResponse = responseResult
                }else{

                    val newArticles = responseResult.articles

                    breakingNewsResponse?.articles?.addAll(newArticles)
                }

                return Resource.Success(breakingNewsResponse?:responseResult)

            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNews(response: Response<NewsResponse>):Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body() ?.let { responseResult->

               searchNewsPage++
                if (searchNewsResponse == null){
                    searchNewsResponse = responseResult
                }else{
                    //val oldArticles = searchNewsResponse?.articles
                    val newArticles = responseResult.articles
                    //oldArticles?.addAll(newArticles)
                    searchNewsResponse?.articles?.addAll(newArticles)
                }

                return Resource.Success(searchNewsResponse?:responseResult)

            }
        }
        return Resource.Error(response.message())
    }

}