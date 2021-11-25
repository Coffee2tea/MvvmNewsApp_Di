package com.example.simplemvvmnewsapp.main

import android.media.MediaDrm
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemvvmnewsapp.data.entities.Song
import com.example.simplemvvmnewsapp.exoplayer.MusicServiceConnection
import com.example.simplemvvmnewsapp.exoplayer.isPlayEnabled
import com.example.simplemvvmnewsapp.exoplayer.isPrepared
import com.example.simplemvvmnewsapp.util.Constants.Companion.MEDIA_ROOT_ID
import com.example.simplemvvmnewsapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) :ViewModel() {

    private val _mediaItems = MutableLiveData<Resource<List<Song>>>()
    val mediaItems: LiveData<Resource<List<Song>>> = _mediaItems

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.netWorkError
    val curPlayingSong =  musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState

    init {
        _mediaItems.postValue(Resource.Loading())
        musicServiceConnection.subscribe(MEDIA_ROOT_ID, object:MediaBrowserCompat.SubscriptionCallback(){
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                val items = children.map {
                    Song(
                        it.mediaId!!,
                        it.description.title.toString(),
                        it.description.subtitle.toString(),
                        it.description.mediaUri.toString(),
                        it.description.iconUri.toString()
                    )
                }
                _mediaItems.postValue(Resource.Success(items))
            }
        })
    }

    fun skipToNext(){
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPrevious(){
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(pos: Long){
        musicServiceConnection.transportControls.seekTo(pos)
    }

    fun playOrToggleSong(mediaItem:Song, toggle:Boolean = false){
        val isPrepared = playbackState.value?.isPrepared?:false
        if(isPrepared && mediaItem.mediaID ==
                curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)){
            playbackState.value?.let { playbackState->
                when{
                    playbackState.isPrepared -> if (toggle) musicServiceConnection.transportControls.pause()
                    playbackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
              }else{
                musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaID,null)
            }
        }



    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback(){})
    }
}