package com.example.emotionbasedmusic.data

import com.google.gson.annotations.SerializedName

data class MessageBody(
    @SerializedName("title") val title: String = "", @SerializedName("body") val body: String = ""
)

data class NotificationBody(
    @SerializedName("to") val topic: String = "", @SerializedName("data") val data: MessageBody
)