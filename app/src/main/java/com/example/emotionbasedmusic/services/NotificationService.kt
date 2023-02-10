package com.example.emotionbasedmusic.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.emotionbasedmusic.MainActivity
import com.example.emotionbasedmusic.R
import com.example.emotionbasedmusic.Utils.createNotificationChannel
import com.example.emotionbasedmusic.Utils.getIntent
import com.example.emotionbasedmusic.container.AppContainer
import com.example.emotionbasedmusic.helper.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject


class NotificationService : FirebaseMessagingService() {

    lateinit var notificationManager: NotificationManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var database = FirebaseDatabase.getInstance()
    private var auth = FirebaseAuth.getInstance()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (token != "") {
            sharedPreferences = getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()
            editor.putString(Constants.DEVICE_TOKEN, token).apply()
            val ref = database.reference.child("tokens").child(auth.currentUser?.uid!!)
            scope.launch { ref.setValue(token) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notification =
            createNotification(this, message.data.get("body"))
        val range = (1000..9999)
        notificationManager.notify(range.random(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun createNotification(context: Context, songName: String?): Notification {
        setUpNotificationChannel(context)
        val contentBody = "Your requested song $songName is added to Emosic"
        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(context, Constants.FCM_CHANNEL_ID)
            .setSmallIcon(R.drawable.figma_launcher_dark)
            .setContentTitle("Song added")
            .setContentText(contentBody)
            .setContentIntent(pendingIntent)
            .setGroup(Constants.APP_NAME)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .build()
    }

    private fun setUpNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            Constants.FCM_CHANNEL_ID,
            Constants.FCM_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.setSound(null, null)
        notificationManager.createNotificationChannel(channel)
    }
}
