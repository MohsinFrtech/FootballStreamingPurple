package com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces

interface ApiResponseListeners {
    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String)
}