package com.tsz.live.football.tv.streaming.hd.adsdata

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.chartboost.sdk.Chartboost
import com.chartboost.sdk.ads.Interstitial
import com.chartboost.sdk.callbacks.InterstitialCallback
import com.chartboost.sdk.events.CacheError
import com.chartboost.sdk.events.CacheEvent
import com.chartboost.sdk.events.ClickError
import com.chartboost.sdk.events.ClickEvent
import com.chartboost.sdk.events.DismissEvent
import com.chartboost.sdk.events.ImpressionEvent
import com.chartboost.sdk.events.ShowError
import com.chartboost.sdk.events.ShowEvent
import com.chartboost.sdk.events.StartError
import com.chartboost.sdk.privacy.model.CCPA
import com.chartboost.sdk.privacy.model.COPPA
import com.chartboost.sdk.privacy.model.GDPR
import com.cleversolutions.ads.AdImpression
import com.cleversolutions.ads.AdLoadCallback
import com.cleversolutions.ads.AdPaidCallback
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.Audience
import com.cleversolutions.ads.ConsentFlow
import com.cleversolutions.ads.android.CAS
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.InterstitialAdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.startapp.sdk.ads.banner.Banner
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener
import com.startapp.sdk.adsbase.adlisteners.AdEventListener
import com.tsz.live.football.tv.streaming.hd.BuildConfig
import com.tsz.live.football.tv.streaming.hd.models.AppAd
import com.tsz.live.football.tv.streaming.hd.utils.Logger
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.casAiAdManager
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.googleAdMangerInterstitial
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.AdManagerListener
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions

object NewAdManager {

    private val logger = Logger()
    private var chartboostInterstitial: Interstitial? = null
    private var adManagerListener: AdManagerListener? = null
    private var unityInterstitialId = "Interstitial_Android"
    private val taG = "AdManagerClass"
    private var mInterstitialAd: InterstitialAd? = null
    private var facebookInterstitial: com.facebook.ads.InterstitialAd? = null
    private var adProvider = ""
    private var startAppAd: StartAppAd? = null
    private var bannerAdValue = ""
    private var interstitialAdValue = ""
    private var nativeAdValue = ""
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    private var isCasInterstitialReady = false

    ///function will return provider
    fun setAdManager(adManagerListen: AdManagerListener) {
        adManagerListener = adManagerListen
    }

    fun checkProvider(list: List<AppAd>, location: String): String {
        adProvider = "none"
        for (listItem in list) {
            if (listItem.enable == true) {
                if (!listItem.ad_locations.isNullOrEmpty()) {
                    for (adLocation in listItem.ad_locations!!) {
                        if (adLocation.title.equals(location, true)) {
                            if (listItem.ad_provider.equals(Constants.admob, true)) {
                                adProvider = Constants.admob
                                checkAdValue(adLocation.title, listItem.ad_key, adProvider)
                            } else if (listItem.ad_provider.equals(Constants.facebook, true)) {
                                adProvider = Constants.facebook
                                checkAdValue(adLocation.title, listItem.ad_key, adProvider)

                            } else if (listItem.ad_provider.equals(Constants.unity, true)) {
                                adProvider = Constants.unity
                                checkAdValue(adLocation.title, listItem.ad_key, adProvider)
                            } else if (listItem.ad_provider.equals(Constants.chartBoost, true)) {
                                adProvider = Constants.chartBoost
                                checkAdValue(adLocation.title, listItem.ad_key, adProvider)
                            } else if (listItem.ad_provider.equals(Constants.startApp, true)) {
                                adProvider = Constants.startApp
                                checkAdValue(adLocation.title, listItem.ad_key, adProvider)
                            }  else if (listItem.ad_provider.equals(Constants.adManagerAds, true)) {
                                adProvider = Constants.adManagerAds
                                checkAdValue(adLocation.title, listItem.ad_key, adProvider)
                            }
                            else if (listItem.ad_provider.equals(Constants.cas_Ai, true)) {
                                adProvider = Constants.cas_Ai
                                checkAdValue(adLocation.title, listItem.ad_key, adProvider)
                            }
                        }


                    }

                }


            }

        }

        ////If provider exist then initialize sdk of the particular provider
        return adProvider
    }

    private fun checkAdValue(adLocation: String?, adKey: String?, provider: String) {
        if (adLocation.equals(Constants.adMiddle, true) || adLocation.equals(
                Constants.adBefore,
                true
            )
            || adLocation.equals(Constants.adAfter, true)
            || adLocation.equals(Constants.adTap, true)
        ) {
            interstitialAdValue = adKey.toString()
            if (provider.equals(Constants.chartBoost, true)) {
                if (interstitialAdValue.contains(Constants.mySecretCheckDel)) {
                    val yourArray: List<String> =
                        interstitialAdValue.split(Constants.mySecretCheckDel)
                    Constants.chartBoostAppID = yourArray[0].trim()
                    Constants.chartBoostAppSig = yourArray[1].trim()
                }
            } else if (provider.equals(Constants.admob, true)) {
                Constants.admobInterstitial = interstitialAdValue
            } else if (provider.equals(Constants.facebook, true)) {
                Constants.facebookPlacementIdInterstitial = interstitialAdValue
            } else if (provider.equals(Constants.unity, true)) {
                Constants.unityGameID = interstitialAdValue
            } else if (provider.equals(Constants.startApp, true)) {
                Constants.startAppId = interstitialAdValue
            }
            else if (provider.equals(Constants.adManagerAds, true)) {
                Constants.googleAdMangerInterstitial = interstitialAdValue
            }else if (provider.equals(Constants.cas_Ai, true)) {
                Constants.casAiId = interstitialAdValue
            }
        } else if (adLocation.equals(Constants.nativeAdLocation, true)) {
            nativeAdValue = adKey.toString()

            if (provider.equals(Constants.admob, true)) {
                Constants.nativeAdmob = nativeAdValue

            } else if (provider.equals(Constants.facebook, true)) {
                Constants.nativeFacebook = nativeAdValue
            }
            else if (provider.equals(Constants.adManagerAds, true)) {
                Constants.googleAdMangerNative = nativeAdValue
            }
        } else if (adLocation.equals(Constants.adLocation1, true)
            || adLocation.equals(Constants.adLocation2top, true)
            || adLocation.equals(Constants.adLocation2bottom, true)
            || adLocation.equals(Constants.adLocation2topPermanent, true)
        ) {
            bannerAdValue = adKey.toString()
            if (provider.equals(Constants.admob, true)) {
                Constants.admobBannerId = bannerAdValue
            } else if (provider.equals(Constants.facebook, true)) {
                Constants.fbPlacementIdBanner = bannerAdValue
            } else if (provider.equals(Constants.startApp, true)) {
                Constants.startAppId = bannerAdValue
            } else if (provider.equals(Constants.unity, true)) {
                Constants.unityGameID = bannerAdValue
            }
            else if (provider.equals(Constants.adManagerAds, true)) {
                Constants.googleAdMangerBanner = bannerAdValue
            } else if (provider.equals(Constants.cas_Ai, true)) {
                Constants.casAiId = bannerAdValue
            }
        }
    }


    fun loadAdProvider(
        provider: String,
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        startAppBanner: Banner?,
        context: Context,
        activity: Activity

    ) {
        if (provider.equals(Constants.admob, true)) {
            adMobSdkInitializationOrAdmobAd(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner,
                context,
                activity
            )
        }
        else  if (provider.equals(Constants.adManagerAds, true)) {
            adMobSdkInitializationOrAdmobAdWithManager(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner,
                context,
                activity
            )
        }
        else if (provider.equals(Constants.facebook, true)) {
            facebookSdkInitialization(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner,
                context,
                activity
            )
        } else if (provider.equals(Constants.unity, true)) {
            unitySdkInitialization(
                adLocation, adView, linearLayout, relativeLayout, startAppBanner,
                context, activity
            )
        } else if (provider.equals(Constants.chartBoost, true)) {
            chartboostSdkInitialization(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner,
                context,
                activity
            )
        } else if (provider.equals(Constants.startApp, true)) {
            startAppSdkInitialization(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner,
                context,
                activity
            )
        }else if (provider.equals(Constants.cas_Ai, true)) {
            //Cas Ai provider Initialization.....
            casAiSdkInitialization(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner,
                context,
                activity
            )
        }

    }
    private fun casAiSdkInitialization(
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?,
        context: Context,
        activity: Activity
    ) {
        if (Constants.isCasAiInit) {
            loadAdAtParticularLocation(
                adLocation,
                Constants.cas_Ai, adView, linearLayout, relativeLayout, banner,
                context,
                activity
            )
        } else {
            try {
                //InitializeCasAiSdk....
//                CAS.settings.debugMode = BuildConfig.DEBUG
                CAS.settings.taggedAudience = Audience.NOT_CHILDREN
                casAiAdManager = CAS.buildManager()
                    .withManagerId(Constants.casAiId)
//                    .withTestAdMode(BuildConfig.DEBUG)
                    .withAdTypes(AdType.Banner, AdType.Interstitial, AdType.Rewarded)
                    .withConsentFlow(
                        ConsentFlow(isEnabled = true)
                            .withDismissListener {
                                Log.d(CAS_TAG, "Consent flow dismissed")
                            }
                    )
                    .withCompletionListener {
                        if (it.error == null) {
                            Constants.isCasAiInit = true
                            Log.d(CAS_TAG, "Ad manager initialized")
                            loadAdAtParticularLocation(
                                adLocation,
                                Constants.cas_Ai, adView, linearLayout, relativeLayout, banner,
                                context,
                                activity
                            )
                        } else {
                            Constants.isCasAiInit = false
                            Log.d(CAS_TAG, "Ad manager initialization failed: " + it.error)
                        }
                    }
                    .build(context)
            } catch (e: java.lang.Exception) {
                Log.d("Exception", "msg")
            }

        }

    }
    private fun adMobSdkInitializationOrAdmobAdWithManager(
        locationName: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?,
        context: Context,
        activity: Activity
    ) {
        if (Constants.isInitAdmobSdk) {
            loadAdAtParticularLocation(
                locationName,
                Constants.adManagerAds, adView, linearLayout, relativeLayout, banner, context,
                activity
            )
        } else {
            MobileAds.initialize(context) { p0 ->
                Constants.isInitAdmobSdk = true
                loadAdAtParticularLocation(
                    locationName,
                    Constants.adManagerAds, adView, linearLayout, relativeLayout, banner, context,
                    activity
                )
            }
        }


    }

    ////startapp sdk initialization....
    private fun startAppSdkInitialization(
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?,
        context: Context,
        activity: Activity
    ) {

        if (Constants.isStartAppSdkInit) {
            loadAdAtParticularLocation(
                adLocation,
                Constants.startApp, adView, linearLayout, relativeLayout, banner, context, activity
            )
        } else {
            try {
//                Log.d("StartAppId", "value" + Constants.startAppId)
                if (!Constants.startAppId.isNullOrEmpty()) {

                    StartAppSDK.init(context, Constants.startAppId, false)
                    StartAppAd.disableSplash()
//                    StartAppSDK.setTestAdsEnabled(true)
                    Constants.isStartAppSdkInit = true
                    loadAdAtParticularLocation(
                        adLocation,
                        Constants.startApp,
                        adView,
                        linearLayout,
                        relativeLayout,
                        banner,
                        context,
                        activity
                    )

                }


            } catch (e: Exception) {

                logger.printLog(taG, "StartAppError" + e.message)
            }
        }


    }

    private fun unitySdkInitialization(
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?,
        context: Context,
        activity: Activity
    ) {

        if (Constants.isUnitySdkInit) {
            loadAdAtParticularLocation(
                adLocation,
                Constants.unity,
                adView,
                linearLayout,
                relativeLayout,
                banner,
                context,
                activity
            )

        } else {
            UnityAds.initialize(
                context,
                Constants.unityGameID,
                Constants.unityTestMode,
                object : IUnityAdsInitializationListener {
                    override fun onInitializationComplete() {
                        if (UnityAds.isInitialized) {
                            Log.d("unityInterstitial", "unity pass")

                            Constants.isUnitySdkInit = true
                            logger.printLog(taG, "unitySdkInitialized")
                            loadAdAtParticularLocation(
                                adLocation,
                                Constants.unity,
                                adView,
                                linearLayout,
                                relativeLayout,
                                banner,
                                context,
                                activity
                            )

                        }

                    }

                    override fun onInitializationFailed(
                        p0: UnityAds.UnityAdsInitializationError?,
                        p1: String?
                    ) {
                        Log.d("unityInterstitial", "unity fail")

                        Constants.isUnitySdkInit = false
                        logger.printLog(taG, "unityFailed" + p0.toString())

                    }
                })

        }
    }


    ///Function to initialize admob sdk...
    private fun adMobSdkInitializationOrAdmobAd(
        locationName: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?,
        context: Context,
        activity: Activity
    ) {
        if (Constants.isInitAdmobSdk) {
            loadAdAtParticularLocation(
                locationName,
                Constants.admob,
                adView,
                linearLayout,
                relativeLayout,
                banner,
                context,
                activity
            )
        } else {
            MobileAds.initialize(context) { p0 ->

                Constants.isInitAdmobSdk = true
                loadAdAtParticularLocation(
                    locationName,
                    Constants.admob,
                    adView,
                    linearLayout,
                    relativeLayout,
                    banner,
                    context,
                    activity
                )

                logger.printLog(taG, "admobProvider${p0.adapterStatusMap}")
            }
        }


    }


    fun showAds(adProviderShow: String, activity: Activity, context: Context) {

        if (adProviderShow.equals(Constants.admob, true)) {
            showAdmobInterstitial(activity, context)
        } else if (adProviderShow.equals(Constants.unity, true)) {
            showUnityAd(activity)
        } else if (adProviderShow.equals(Constants.chartBoost, true)) {
            showChartBoost()
        } else if (adProviderShow.equals(Constants.facebook, true)) {
            showFacebookAdInterstitial(context)
        } else if (adProviderShow.equals(Constants.startApp, true)) {
            showStartAppAd()
        }
        else if (adProviderShow.equals(Constants.adManagerAds, true)) {

            showAdmobInterstitialAdx(activity)
        }else if (adProviderShow.equals(Constants.cas_Ai, true)) {
            showInterstitialCasAi(activity)
        }
        else{
            adManagerListener?.onAdFinish()
        }
    }
    private fun showInterstitialCasAi(activity: Activity) {
        if (isCasInterstitialReady) {
            val contentCallback = object : AdPaidCallback {
                override fun onShown(ad: AdImpression) {
                    Log.d(CAS_TAG, "Interstitial Ad shown from " + ad.network)
                }

                override fun onAdRevenuePaid(ad: AdImpression) {
                    Log.d(CAS_TAG, "Interstitial Ad revenue paid from " + ad.network)
                }

                override fun onShowFailed(message: String) {
                    adManagerListener?.onAdFinish()
                    Log.e(CAS_TAG, "Interstitial Ad show failed: $message")
//                label.text = message
                }

                override fun onClicked() {
                    Log.d(CAS_TAG, "Interstitial Ad received Click")
                }

                override fun onClosed() {
                    adManagerListener?.onAdFinish()
                    Log.d(CAS_TAG, "Interstitial Ad received Close")
//                label.text = "Closed"
                }
            }
            if (casAiAdManager?.isInterstitialReady == true)
                casAiAdManager?.showInterstitial(activity, contentCallback)
            else
                Log.e(CAS_TAG, "Interstitial Ad not ready to show")
        } else {
            createInterstitialCasAi()
            adManagerListener?.onAdFinish()
            //loadagain
        }

    }


    private fun showUnityAd(activity: Activity) {
        val showListener: IUnityAdsShowListener = object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(
                placementId: String,
                error: UnityAds.UnityAdsShowError,
                message: String
            ) {
                loadUnityAdInterstitial()
                adManagerListener?.onAdFinish()
            }

            override fun onUnityAdsShowStart(placementId: String) {

            }

            override fun onUnityAdsShowClick(placementId: String) {

            }

            override fun onUnityAdsShowComplete(
                placementId: String,
                state: UnityAds.UnityAdsShowCompletionState
            ) {
                loadUnityAdInterstitial()
                adManagerListener?.onAdFinish()
            }
        }
        UnityAds.show(
            activity,
            unityInterstitialId,
            UnityAdsShowOptions(),
            showListener
        )

    }

    private fun loadAdAtParticularLocation(
        locationName: String,
        adProviderName: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?,
        context: Context,
        activity: Activity
    ) {
        if (locationName.equals(Constants.adLocation1, true) ||
            locationName.equals(Constants.adLocation2top, true) ||
            locationName.equals(Constants.adLocation2bottom, true) ||
            locationName.equals(Constants.adLocation2topPermanent, true)
        ) {


        } else {
            if (adProviderName.equals(Constants.admob, true)) {
                loadAdmobInterstitialAd(context)
            } else if (adProviderName.equals(Constants.unity, true)) {
                loadUnityAdInterstitial()
            } else if (adProviderName.equals(Constants.chartBoost, true)) {
                loadChartBoost()
            } else if (adProviderName.equals(Constants.facebook, true)) {
                loadFacebookInterstitialAd(context)
            } else if (adProviderName.equals(Constants.startApp, true)) {
                loadStartAppAd(context)
            }
            else if (adProviderName.equals(Constants.adManagerAds, true)) {
                loadAdmobInterstitialAdx(context)
            }else if (adProviderName.equals(Constants.cas_Ai, true)) {
                createInterstitialCasAi()
            }
            else{
                adManagerListener?.onAdFinish()
            }
        }
    }

    private fun createInterstitialCasAi() {

//        val label = findViewById<TextView>(R.id.interStatusText)
        // Set Ad load callback
        casAiAdManager?.onAdLoadEvent?.add(object : AdLoadCallback {
            override fun onAdLoaded(type: AdType) {
                if (type == AdType.Interstitial) {
                    isCasInterstitialReady = true
                    Log.d(CAS_TAG, "Interstitial Ad loaded and ready to show")
                }
            }

            override fun onAdFailedToLoad(type: AdType, error: String?) {
                if (type == AdType.Interstitial) {
                    isCasInterstitialReady = false
                    Log.d(CAS_TAG, "Interstitial Ad received error: $error")
                }
            }
        })

        // Create Ad content callback

        // Any loading mode, except manual,
        // automatically controls the preparation of sdk for impressions.
//        if (CAS.settings.loadingMode == LoadingManagerMode.Manual) {
//            findViewById<Button>(R.id.loadInterBtn).setOnClickListener {
//                label.text = "Loading"
//                manager.loadInterstitial()
//            }
//        } else {
//            if (manager.isInterstitialReady)
//                label.text = "Loaded"
//            else
//                label.text = "Loading"
//            findViewById<Button>(R.id.loadInterBtn).visibility = View.GONE
//        }
//
//        findViewById<Button>(R.id.showInterBtn).setOnClickListener {
//
//        }
    }

    //Function to load adx interstitial....
    private fun loadAdmobInterstitialAdx(context: Context) {
        val adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(context, googleAdMangerInterstitial, adRequest, object : AdManagerInterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("AdxLoaded","error"+adError?.message)
                mAdManagerInterstitialAd = null
                adManagerListener?.onAdLoad("failed")
            }

            override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                Log.d("AdxLoaded","loaded")
                mAdManagerInterstitialAd = interstitialAd
                adManagerListener?.onAdLoad("success")
            }
        })
    }

    private fun showAdmobInterstitialAdx(activity: Activity) {
        mAdManagerInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.

            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                mAdManagerInterstitialAd = null
                adManagerListener?.onAdFinish()
            }

            override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                // Called when ad fails to show.
                mAdManagerInterstitialAd = null
                adManagerListener?.onAdFinish()
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
            }
        }
        if (mAdManagerInterstitialAd != null) {
            mAdManagerInterstitialAd?.show(activity)
        } else {
            adManagerListener?.onAdFinish()
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }


    fun loadStartAppAd(context: Context) {
        startAppAd = StartAppAd(context)
        startAppAd?.loadAd(object : AdEventListener {

            override fun onReceiveAd(p0: com.startapp.sdk.adsbase.Ad) {
                showStartAppAd()
            }

            override fun onFailedToReceiveAd(p0: com.startapp.sdk.adsbase.Ad?) {

            }
        })
    }


    private fun showStartAppAd() {
        startAppAd?.showAd(object : AdDisplayListener {
            override fun adHidden(ad: com.startapp.sdk.adsbase.Ad) {


            }

            override fun adDisplayed(ad: com.startapp.sdk.adsbase.Ad) {


            }

            override fun adClicked(ad: com.startapp.sdk.adsbase.Ad) {
            }

            override fun adNotDisplayed(ad: com.startapp.sdk.adsbase.Ad) {
            }
        })
    }

    private fun loadUnityAdInterstitial() {
        val loadListener: IUnityAdsLoadListener = object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String) {
                adManagerListener?.onAdLoad("success")
            }

            override fun onUnityAdsFailedToLoad(
                placementId: String,
                error: UnityAds.UnityAdsLoadError,
                message: String
            ) {
//                Log.d("unityInterstitial","unity pass"+error.toString())
                logger.printLog(taG, "unityLoaded")
                adManagerListener?.onAdLoad("failed")
            }
        }
        UnityAds.load(unityInterstitialId, loadListener)
    }


    private fun chartboostSdkInitialization(
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?,
        context: Context,
        activity: Activity
    ) {
        if (Constants.isChartboostSdkInit) {
            loadAdAtParticularLocation(
                adLocation,
                Constants.chartBoost,
                adView,
                linearLayout,
                relativeLayout,
                banner,
                context,
                activity
            )

        } else {
            Chartboost.addDataUseConsent(context, GDPR(GDPR.GDPR_CONSENT.BEHAVIORAL))
            Chartboost.addDataUseConsent(context, CCPA(CCPA.CCPA_CONSENT.OPT_IN_SALE))
            Chartboost.addDataUseConsent(context, COPPA(true))

            Chartboost.startWithAppId(
                context, Constants.chartBoostAppID, Constants.chartBoostAppSig
            ) { startError: StartError? ->
                if (startError == null) {
                    Constants.isChartboostSdkInit = true
                    loadAdAtParticularLocation(
                        adLocation,
                        Constants.chartBoost,
                        adView,
                        linearLayout,
                        relativeLayout,
                        banner,
                        context,
                        activity
                    )

                    // handle success
                } else {

                    // handle failure
                }
            }
        }


    }


    private fun facebookSdkInitialization(
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?,
        context: Context,
        activity: Activity
    ) {
        if (Constants.isInitFacebookSdk) {
            loadAdAtParticularLocation(
                adLocation,
                Constants.facebook,
                adView,
                linearLayout,
                relativeLayout,
                banner,
                context,
                activity
            )

        } else {
            AudienceNetworkAds
                .buildInitSettings(context)
                .withInitListener {
                    if (it.isSuccess) {
                        Constants.isInitFacebookSdk = true
                        loadAdAtParticularLocation(
                            adLocation,
                            Constants.facebook,
                            adView,
                            linearLayout,
                            relativeLayout,
                            banner,
                            context,
                            activity
                        )

                    } else {

                        Constants.isInitFacebookSdk = false
                    }

                }
                .initialize()
        }
    }

    //loadChartBoost
    private fun loadChartBoost() {
        chartboostInterstitial = Interstitial("location", object : InterstitialCallback {
            override fun onAdDismiss(event: DismissEvent) {
                loadChartBoost()
                adManagerListener?.onAdFinish()
            }


            override fun onAdLoaded(event: CacheEvent, error: CacheError?) {
                Log.d("chartboostttt","ad"+error?.code)
                adManagerListener?.onAdLoad("success")
            }

            override fun onAdRequestedToShow(event: ShowEvent) {}
            override fun onAdShown(event: ShowEvent, error: ShowError?) {


            }

            override fun onAdClicked(event: ClickEvent, error: ClickError?) {
//                adManagerListener?.onAdFinish()
            }

            override fun onImpressionRecorded(event: ImpressionEvent) {}


        })
        chartboostInterstitial?.cache()
    }

    ///Show chartboost ads
    private fun showChartBoost(
    ) {
        if (chartboostInterstitial !=null) {
            if (chartboostInterstitial?.isCached() == true) { // check is cached is not mandatory
                chartboostInterstitial?.show()
            } else {
                loadChartBoost()
                adManagerListener?.onAdFinish()
            }
        }
        else{
            loadChartBoost()
            adManagerListener?.onAdFinish()
        }
    }

    ///Admob load function...
    private fun loadAdmobInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            Constants.admobInterstitial,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    logger.printLog(taG, "admobProvider : ${adError.message}")
                    mInterstitialAd = null
                    Log.d("AdmobStatus", "not loaded")
//                    adManagerListener?.onAdLoad("failed")
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    logger.printLog(taG, "admobProvider : ${interstitialAd.responseInfo}")
                    Log.d("AdmobStatus", "loaded")
                    mInterstitialAd = interstitialAd
//                    adManagerListener?.onAdLoad("success")
                }
            })

    }


    ////showAdmobInterstitial
    private fun showAdmobInterstitial(activity: Activity, context: Context) {

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {

            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                mInterstitialAd = null
                loadAdmobInterstitialAd(context)
                adManagerListener?.onAdFinish()
            }

            override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                mInterstitialAd = null
                loadAdmobInterstitialAd(context)
                adManagerListener?.onAdFinish()
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.

            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.

            }
        }


        if (mInterstitialAd != null) {
            mInterstitialAd?.show(activity)
        } else {
            loadAdmobInterstitialAd(context)
            adManagerListener?.onAdFinish()
            logger.printLog(taG, "admob interstitial not loaded successfully")
        }


    }

    ///load facebook interstitial....
    private fun loadFacebookInterstitialAd(context: Context) {
        facebookInterstitial =
            com.facebook.ads.InterstitialAd(context, Constants.facebookPlacementIdInterstitial)
        val adListener = object : InterstitialAdListener {

            override fun onInterstitialDisplayed(p0: Ad?) {

            }

            override fun onAdClicked(p0: Ad?) {
                adManagerListener?.onAdFinish()
            }

            override fun onInterstitialDismissed(p0: Ad?) {
                facebookInterstitial = null
                loadFacebookInterstitialAd(context)
                adManagerListener?.onAdFinish()
            }

            override fun onError(p0: Ad?, p1: AdError?) {
                facebookInterstitial = null
                adManagerListener?.onAdLoad("failed")
                logger.printLog("facebookInter", "facebook not" + p1?.errorMessage)
            }

            override fun onAdLoaded(p0: Ad?) {
                adManagerListener?.onAdLoad("success")
                logger.printLog("facebookInter", "facebook loaded" + p0.toString())
            }

            override fun onLoggingImpression(p0: Ad?) {

            }


        }
        val loadAdConfig = facebookInterstitial!!.buildLoadAdConfig()
            .withAdListener(adListener)
            .build()

        facebookInterstitial!!.loadAd(loadAdConfig)

    }


    /// show facebook interstitial....
    private fun showFacebookAdInterstitial(context: Context) {
        if (facebookInterstitial != null) {
            if (facebookInterstitial!!.isAdLoaded) {

                try {
                    facebookInterstitial!!.show()
                } catch (e: Throwable) {

                    logger.printLog(taG, "Exception" + e.message)
                }


            } else {
                loadFacebookInterstitialAd(context)
                adManagerListener?.onAdFinish()
            }
        } else {
            loadFacebookInterstitialAd(context)
            adManagerListener?.onAdFinish()
        }


    }

}