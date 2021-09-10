package com.example.simplemvvmnewsapp.main

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.*
import com.example.simplemvvmnewsapp.NewsApplication
import com.example.simplemvvmnewsapp.data.models.Article
import com.example.simplemvvmnewsapp.data.models.NewsResponse
import com.example.simplemvvmnewsapp.util.DispatcherProvider
import com.example.simplemvvmnewsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class MainViewModel @Inject constructor(
    app: Application,
    private val repository: MainRepository,
    private val dispatcher: DispatcherProvider
): AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsResponse: NewsResponse? = null
    var breakingNewsPage = 1

    val chineseBreakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var chineseBreakingNewsResponse: NewsResponse? = null
    var chineseBreakingNewsPage = 1

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsResponse: NewsResponse? = null
    var searchNewsPage = 1

init {
    getCanadaBreakingNews("ca","en")
    getChineseBreakingNews("tw","zh")
}

    fun getCanadaBreakingNews(country:String,language:String) = viewModelScope.launch(dispatcher.io){
            safeCanadianBreakingNewsCall(country,language)
        }

    fun getChineseBreakingNews(country:String,language:String) = viewModelScope.launch(dispatcher.io){

        safeChineseBreakingNewsCall(country,language)
    }

    fun searchForNews(searchQuery:String) = viewModelScope.launch(dispatcher.io){
        safeSearchNewsCall(searchQuery)
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

    private fun handleChineseBreakingNews(response: Response<NewsResponse>):Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body() ?.let { responseResult->

                chineseBreakingNewsPage++

                if (chineseBreakingNewsResponse == null){
                    chineseBreakingNewsResponse = responseResult
                }else{

                    val newArticles = responseResult.articles

                    chineseBreakingNewsResponse?.articles?.addAll(newArticles)
                }

                return Resource.Success(chineseBreakingNewsResponse?:responseResult)

            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeCanadianBreakingNewsCall(country: String, language: String){
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){

                val response = repository.getNews(country,language,breakingNewsPage)

                breakingNewsResponse = null
                breakingNewsPage = 1

                breakingNews.postValue(handleBreakingNews(response))

            } else{
                breakingNews.postValue(Resource.Error("Can't connect to the Internet"))
            }

        }catch (t: Throwable){
            when(t){
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Failure"))
            }

        }
    }

    private suspend fun safeChineseBreakingNewsCall(country: String, language: String){
        chineseBreakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){

                val response = repository.getNews(country,language,chineseBreakingNewsPage)

                chineseBreakingNewsResponse = null
                chineseBreakingNewsPage = 1

                chineseBreakingNews.postValue(handleChineseBreakingNews(response))

            } else{
                chineseBreakingNews.postValue(Resource.Error("Can't connect to the Internet"))
            }

        }catch (t: Throwable){
            when(t){
                is IOException -> chineseBreakingNews.postValue(Resource.Error("Network Failure"))
                else -> chineseBreakingNews.postValue(Resource.Error("Conversion Failure"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = repository.searchNews(searchQuery)


            searchNewsResponse = null
            searchNewsPage = 1

            searchNews.postValue(handleSearchNews(response))
            }else{
                searchNews.postValue(Resource.Error("Can't connect to the Internet"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Failure"))
            }
        }

    }

    private fun hasInternetConnection():Boolean{
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?: return false

            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET)-> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run{
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}

