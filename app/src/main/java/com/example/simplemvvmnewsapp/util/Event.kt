package com.example.simplemvvmnewsapp.util

open class Event<out T>(private val data: T) {

    var hasBeenHandled = false
    private set

    fun getDataWhenNotHandled():T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            data
        }
    }

    fun peekContent() = data
}