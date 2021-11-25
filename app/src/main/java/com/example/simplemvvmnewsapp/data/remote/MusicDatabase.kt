package com.example.simplemvvmnewsapp.data.remote

import com.example.simplemvvmnewsapp.data.entities.Song
import com.example.simplemvvmnewsapp.util.Constants.Companion.SONG_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class MusicDatabase {
    private val firebaseStore = FirebaseFirestore.getInstance()
    private val songCollection = firebaseStore.collection(SONG_COLLECTION)

    suspend fun getAllSongs(): List<Song>{
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        } catch (e: Exception){
            emptyList()
        }
    }
}