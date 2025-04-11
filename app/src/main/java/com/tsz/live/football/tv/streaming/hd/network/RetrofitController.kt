package com.tsz.live.football.tv.streaming.hd.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import com.tsz.live.football.tv.streaming.hd.BuildConfig
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.baseUrlChannel
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.baseUrlDemo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitController {
    ////Gson converter....
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val client: OkHttpClient by lazy {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        OkHttpClient.Builder()
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .connectTimeout(90, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    private val retrofitController_Channel: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrlChannel)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    val apiServiceChannel: ApiInterface by lazy {
        retrofitController_Channel
            .build()
            .create(ApiInterface::class.java)
    }

    val apiServiceDemo: ApiInterface by lazy {
        retrofitController_Demo
            .build()
            .create(ApiInterface::class.java)
    }
    private val retrofitController_Demo: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrlDemo)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    val apiServiceFootball: ApiInterface by lazy {
        retrofitControllerFootball
            .build()
            .create(ApiInterface::class.java)
    }


    private val retrofitControllerFootball: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.footballBaseApi)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }


}