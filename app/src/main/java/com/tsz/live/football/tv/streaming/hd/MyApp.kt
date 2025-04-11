package com.tsz.live.football.tv.streaming.hd

import android.app.Application
import com.tsz.live.football.tv.streaming.hd.adsdata.AppOpenManager
import com.tsz.live.football.tv.streaming.hd.utils.AppContextProvider

class MyApp : Application() {

//    companion object {
//        const val TAG = "CAS Sample Adsss"
//        const val CAS_ID = "demo"
//
//        lateinit var adManager: MediationManager
//    }

    private var appOpenManager: AppOpenManager? = null
    override fun onCreate() {
        super.onCreate()
        appOpenManager = AppOpenManager(this)
        AppContextProvider.init(this)


        // Set Manual loading mode to disable auto requests
        //CAS.settings.loadingMode = LoadingManagerMode.Manual

        // Initialize SDK


//        val testDeviceIds = Arrays.asList("890CE0083E0D133594E4E763481D1140")
//        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
//        MobileAds.setRequestConfiguration(configuration)

    }


}