package com.tsz.live.football.tv.streaming.hd.network

import com.tsz.live.football.tv.streaming.hd.models.FootballMatches
import com.tsz.live.football.tv.streaming.hd.models.UrlData
import com.tsz.live.football.tv.streaming.hd.models.varData
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.baseIp
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.channelApi
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.userApi
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @GET(baseIp)
    fun getIPAsync(): Call<String?>?


    @POST(userApi)
    @Headers("Content-Type: application/json")
    fun getDemoDataAsync(
        @Body body: RequestBody
    ): Call<UrlData?>

    @POST(channelApi)
    @Headers("Content-Type: application/json")
    fun getChannelsAsync(
        @Body body: RequestBody
    ): Call<varData>

    // Football API

    @POST("matches/live")
    @Headers("Content-Type: application/json")
    fun getLiveMatches(
        @Body body: RequestBody
    ): Call<List<FootballMatches>>

    @POST("matches/by_date")
    @Headers("Content-Type: application/json")
    fun getMatchesBydate(
        @Body body: RequestBody
    ): Call<List<FootballMatches>>

    @POST("matches/recent")
    @Headers("Content-Type: application/json")
    fun getRecentMatches(
        @Body body: RequestBody
    ): Call<List<FootballMatches>>

}