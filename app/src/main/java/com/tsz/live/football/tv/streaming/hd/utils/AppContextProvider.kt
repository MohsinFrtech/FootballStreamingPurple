package com.tsz.live.football.tv.streaming.hd.utils

import android.content.Context

class AppContextProvider// Private constructor to prevent external instantiation
private constructor() {

    companion object {
        private lateinit var instance: AppContextProvider
        private lateinit var applicationContext: Context

        fun init(context: Context) {
            instance = AppContextProvider()
            applicationContext = context.applicationContext
        }

        fun getContext(): Context? {
            if (!this::instance.isInitialized) {
                return null
            }
            return applicationContext
        }
    }

}