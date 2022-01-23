package com.example.emotionbasedmusic.Utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.emotionbasedmusic.MainActivity
import com.example.emotionbasedmusic.R
import com.example.emotionbasedmusic.data.Music
import com.example.emotionbasedmusic.helper.Constants
import com.example.emotionbasedmusic.helper.Helper

@RequiresApi(Build.VERSION_CODES.M)
fun createNotification(context: Context, song: Music): Helper {
    setUpNotificationChannel(context)
    val notificationLayout = RemoteViews(Constants.PACKAGE_NAME, R.layout.notification)
    notificationLayout.setTextViewText(R.id.notification_song_name, song.songName)
    notificationLayout.setTextViewText(R.id.notification_artist_name, song.artistName)
    val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.music_notes)
        .setContentIntent(getIntent(context))
        .setCustomContentView(notificationLayout)
        .build()

    return Helper(notification, notificationLayout)
}

fun getIntent(context: Context): PendingIntent? {
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra(Constants.IS_FROM_NOTIFICATION, true)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    return PendingIntent.getActivity(context, Constants.INTENT_REQUEST_CODE, intent, 0)
}

fun setUpNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNotificationChannel(context)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun createNotificationChannel(context: Context) {
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
    channel.enableLights(true)
    channel.lightColor = Color.RED
    channel.setSound(null, null)
    notificationManager.createNotificationChannel(channel)
}

