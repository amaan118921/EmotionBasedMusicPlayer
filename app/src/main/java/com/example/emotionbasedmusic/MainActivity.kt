package com.example.emotionbasedmusic

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.emotionbasedmusic.container.AppContainer
import com.example.emotionbasedmusic.data.Music
import com.example.emotionbasedmusic.eventBus.DataEvent
import com.example.emotionbasedmusic.eventBus.MessageEvent
import com.example.emotionbasedmusic.fragments.MusicFragment
import com.example.emotionbasedmusic.helper.Constants
import com.example.emotionbasedmusic.services.MusicService
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashSet

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    var isFromNotification: Boolean = false
    var isServiceRunning: Boolean = false
    var key: Boolean? = true
    private lateinit var activityManager: ActivityManager
    var isFromFavorite = false
    private var musicList = listOf<Music>()
    var musicIntent: Intent? = null
    private var song: Music? = null
    private val permissionEventsListeners: MutableSet<RequestPermissionEventListener> = HashSet()

    @Inject
    lateinit var appContainer: AppContainer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
        appContainer.repo.initSharedPreferences()
        musicIntent = Intent(this, MusicService::class.java)
        this.isFromNotification =
            intent?.extras?.getBoolean(Constants.IS_FROM_NOTIFICATION) ?: false
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().post(MessageEvent(getString(R.string.activity_destroyed)))
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onMessageEvent(event: MessageEvent) {
        when(event.getString()) {
            "deviceToken" -> {
               val ev = event as DataEvent
               val token = ev.getDataString()
                appContainer.repo.setSharedPreferences(Constants.DEVICE_TOKEN, token)
            }
        }
    }

    override fun onBackPressed() {
        when (navController.currentDestination!!.label) {
            Constants.FRAGMENT_MOOD_RECOGNITION -> {
                finish()
            }
            Constants.SIGN_UP_FRAGMENT -> {
                finish()
            }
            Constants.USERS_DATA_FRAGMENT -> {
                finish()
            }
            Constants.SPLASH_FRAGMENT -> {
                finish()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (getListOfServices()) {
            moveToMusic()
        }
    }


    fun stopForegroundService() {
        if(getListOfServices()) { musicIntent?.let { stopService(it) }}
    }

    fun setSong(song: Music?) {
        this.song = song
    }

    fun setSongList(musicList: List<Music>) {
        this.musicList = musicList
    }

    private fun getListOfServices(): Boolean {
        for (service: ActivityManager.RunningServiceInfo in activityManager.getRunningServices(
            Integer.MAX_VALUE
        )) {
            if (service.service.shortClassName == Constants.MUSIC_SERVICE) {
                return true
            }
        }
        return false
    }

    fun moveToMusic() {
        key = false
        if (navController.currentDestination!!.label == Constants.FRAGMENT_MUSIC) { navController.popBackStack() }
        navController.navigate(R.id.musicFragment)
    }

    interface RequestPermissionEventListener {
        fun onRequestPermissionsResults(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        )
    }

    fun registerPermissionListener(listener: RequestPermissionEventListener) {
        permissionEventsListeners.add(listener)
    }

    fun unregisterPermissionListener(listener: RequestPermissionEventListener) {
        permissionEventsListeners.remove(listener)
    }

    fun getActivityPermissionListener(): MutableSet<RequestPermissionEventListener> {
        return Collections.unmodifiableSet(permissionEventsListeners)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (listener in permissionEventsListeners) {
            listener.onRequestPermissionsResults(requestCode, permissions, grantResults)
        }
    }

}