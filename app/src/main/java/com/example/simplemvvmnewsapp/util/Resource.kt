package com.example.simplemvvmnewsapp.util

sealed class Resource<out T>
    (val data: T? = null, val message: String? = null)
{
    class Success<T>(data: T?): Resource<T>(data,null)
    class Error<T>(message: String?): Resource<T>(null, message)
    class Loading<T>: Resource<T>()
    class Empty<T>: Resource<T>()
}