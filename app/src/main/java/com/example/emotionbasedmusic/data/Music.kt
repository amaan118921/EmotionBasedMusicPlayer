package com.example.emotionbasedmusic.data

import android.os.Parcelable
import java.io.Serializable

data class MusicModel(
    var dataArray: List<Music>
):Serializable
data class Music(
    var songName: String = "",
    var artistName: String = "",
    var imgUrl: String = "",
    var songUrl: String = "",
    var playing: Boolean = false,
    var type: String = ""
) : Serializable