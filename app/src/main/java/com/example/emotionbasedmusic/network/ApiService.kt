package com.example.emotionbasedmusic.network

import com.example.emotionbasedmusic.data.Music
import com.example.emotionbasedmusic.data.NotificationBody
import com.example.emotionbasedmusic.helper.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST


private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
private val retrofit =
    Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi)).baseUrl(
        Constants.BASE_URL
    ).build()

private val client = OkHttpClient.Builder().build()

private val retro = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(
    Constants.FCM_BASE_URL).client(client)
    .build()

interface ApiService {

    @GET("happy_songs_data")
    suspend fun getHappySongs(): List<Music>

    @GET("sad_songs_data")
    suspend fun getSadSongs(): List<Music>

    @GET("neutral_songs_data")
    suspend fun getNeutralSongs(): List<Music>

    @GET("angry_songs_data")
    suspend fun getAngrySongs(): List<Music>

    @Headers("Authorization:key=${Constants.fcmServerKey}",
        "Content-Type: application/json")
    @POST("fcm/send")
    suspend fun send(@Body msg: NotificationBody): Response<NotificationBody>

}


object API {
    val retrofitService: ApiService by lazy { retrofit.create(ApiService::class.java) }
}

object MESSAGE {
    val retrofitService: ApiService by lazy { retro.create(ApiService::class.java) }
}