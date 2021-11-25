package com.example.simplemvvmnewsapp.exoplayer

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaSessionCompat
import com.example.simplemvvmnewsapp.R
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.Player
import android.support.v4.media.session.MediaControllerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.simplemvvmnewsapp.util.Constants.Companion.CHANNEL_ID
import com.example.simplemvvmnewsapp.util.Constants.Companion.NOTIFICATION_ID


class MusicNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallback:()-> Unit
){

    private val mediaController = MediaControllerCompat(context,sessionToken)
    private val notificationManager: PlayerNotificationManager

    init {
        notificationManager = PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            CHANNEL_ID).apply {
            setChannelNameResourceId(R.string.notification_channel_name)
            setChannelDescriptionResourceId(R.string.notification_channel_description)
            setMediaDescriptionAdapter(MediaDescriptionAdapter(mediaController))
            setNotificationListener(notificationListener)
        }.build().apply {
            setSmallIcon(R.drawable.ic_music)
            setMediaSessionToken(sessionToken)
        }
    }

    fun showNotification(player: Player){
        notificationManager.setPlayer(player)
    }

    private inner class MediaDescriptionAdapter(
        private val mediaController: MediaControllerCompat
    ): PlayerNotificationManager.MediaDescriptionAdapter{
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return mediaController.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return mediaController.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return mediaController.metadata.description.subtitle.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            Glide.with(context).asBitmap()
                .load(mediaController.metadata.description.iconUri)
                .into(object : CustomTarget<Bitmap>(){
                    override fun equals(other: Any?): Boolean {
                        return super.equals(other)
                    }
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        callback.onBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit
                })
            return null
        }

    }

}