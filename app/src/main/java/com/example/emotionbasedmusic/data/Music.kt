package com.example.emotionbasedmusic.data

import android.os.Parcelable
import java.io.Serializable

data class Music(
    val songName: String = "",
    val artistName: String = "",
    val imgUrl: String = "",
    val songUrl: String = "",
    var playing: Boolean = false
) : Serializable