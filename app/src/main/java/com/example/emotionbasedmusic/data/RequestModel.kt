package com.example.emotionbasedmusic.data

data class RequestModel(
     var songName: String? = null,  var artistName: String? = null,
     var token: String? = null,  var status: Int? = null, var id: String? = null
)