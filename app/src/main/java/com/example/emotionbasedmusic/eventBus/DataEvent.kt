package com.example.emotionbasedmusic.eventBus

class DataEvent(
    str: String, private val dataString: String
): MessageEvent(str) {
    fun getDataString() = dataString
}