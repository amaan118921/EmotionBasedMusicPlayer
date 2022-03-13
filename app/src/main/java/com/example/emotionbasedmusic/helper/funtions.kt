package com.example.emotionbasedmusic.helper

import android.app.Activity
import android.view.View
import com.example.emotionbasedmusic.data.NotificationBody
import com.example.emotionbasedmusic.data.RequestModel


fun View.makeVisible() {
    this.visibility = View.VISIBLE
}

fun View.makeInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.makeGone() {
    this.visibility = View.GONE
}

fun NotificationBody.toRequestModel(): RequestModel {
    return RequestModel().apply {

    }
}

