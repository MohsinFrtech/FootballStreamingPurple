package com.tsz.live.football.tv.streaming.hd.network

interface ApiResponseListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String)
}