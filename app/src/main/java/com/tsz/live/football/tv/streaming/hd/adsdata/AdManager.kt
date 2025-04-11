package com.tsz.live.football.tv.streaming.hd.adsdata

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowMetrics
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.chartboost.sdk.Chartboost
import com.chartboost.sdk.ads.Interstitial
import com.chartboost.sdk.callbacks.InterstitialCallback
import com.chartboost.sdk.events.*
import com.chartboost.sdk.privacy.model.CCPA
import com.chartboost.sdk.privacy.model.COPPA
import com.chartboost.sdk.privacy.model.GDPR
import com.cleversolutions.ads.AdImpression
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.AdViewListener
import com.cleversolutions.ads.Audience
import com.cleversolutions.ads.ConsentFlow
import com.cleversolutions.ads.MediationManager
import com.cleversolutions.ads.android.CAS
import com.cleversolutions.ads.android.CASBannerView
import com.facebook.ads.*
import com.facebook.ads.AdError
import com.facebook.ads.AdSize
import com.google.android.gms.ads.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.startapp.sdk.ads.banner.Banner
import com.startapp.sdk.ads.banner.BannerListener
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener
import com.startapp.sdk.adsbase.adlisteners.AdEventListener
import com.unity3d.ads.*
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import com.google.android.gms.ads.AdView
import com.tsz.live.football.tv.streaming.hd.BuildConfig
import com.tsz.live.football.tv.streaming.hd.R
import com.tsz.live.football.tv.streaming.hd.databinding.LayoutFbNativeAdBinding
import com.tsz.live.football.tv.streaming.hd.models.AppAd
import com.tsz.live.football.tv.streaming.hd.utils.Logger
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.admobInterstitial
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.googleAdMangerInterstitial
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.nativeAdmob
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.AdManagerListener
import kotlinx.coroutines.flow.MutableStateFlow
const val CAS_TAG = "CAS Sample Adsss"
class AdManager(
    private val context: Context,
    private val activity: Activity,
    val adManagerListener: AdManagerListener
) {

    private val logger = Logger()
    private var adView: NativeAdView? = null
    private var nativeAdLayout: NativeAdLayout? = null
    private var chartboostInterstitial: Interstitial? = null
    private var fbNativeAd: NativeAd? = null
    private var facebookbinding: LayoutFbNativeAdBinding? = null
    private var bottomBanner: BannerView? = null
    private var bottomAdUnitId = "Banner_Android"
    private val taG = "AdManagerClass"
    private var bannerAdValue = ""
    private var interstitialAdValue = ""
    private var nativeAdValue = ""
    private var topBannerUnity: BannerView? = null
    private var casAiAdManager: MediationManager? = null

    private var startAppAd: StartAppAd? = null
    private var mInterstitialAd: AdManagerInterstitialAd? = null
    private var facebookinterstitial: InterstitialAd? = null
    private var currentNativeAd: com.google.android.gms.ads.nativead.NativeAd? = null
    private var adProvider = ""
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    private val interstitialAdFlow =
        MutableStateFlow<Pair<StartAppAd?, com.startapp.sdk.adsbase.Ad.AdState>?>(null)

    ///function will return provider
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
                            } else if (listItem.ad_provider.equals(Constants.adManagerAds, true)) {
                                adProvider = Constants.adManagerAds
                                checkAdValue(adLocation.title, listItem.ad_key, adProvider)
                            } else if (listItem.ad_provider.equals(Constants.cas_Ai, true)) {
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

    //Function to load adx interstitial....
    private fun loadAdmobInterstitialAdx() {
        val adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(
            context,
            googleAdMangerInterstitial,
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("AdxLoaded", "error" + adError?.message)
                    mAdManagerInterstitialAd = null
                    adManagerListener.onAdLoad("failed")
                }

                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    Log.d("AdxLoaded", "loaded")
                    mAdManagerInterstitialAd = interstitialAd
                    adManagerListener.onAdLoad("success")
                }
            })
    }

    private fun showAdmobInterstitialAdx() {
        mAdManagerInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.

            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                mAdManagerInterstitialAd = null
                adManagerListener.onAdFinish()
            }

            override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                // Called when ad fails to show.
                mAdManagerInterstitialAd = null
                adManagerListener.onAdFinish()
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
            adManagerListener.onAdFinish()
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }

    fun getSize(): com.google.android.gms.ads.AdSize {
        val displayMetrics = context.resources.displayMetrics
        val adWidthPixels =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
                windowMetrics.bounds.width()
            } else {
                displayMetrics.widthPixels
            }
        val density = displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()
        return com.google.android.gms.ads.AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            context,
            adWidth
        )
    }

    fun loadAdmobBannerAdx(adViewLayout: LinearLayout?) {
        val adSize = getSize()
        val adView = AdManagerAdView(context)
        adView.adUnitId = Constants.googleAdMangerBanner
        adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER)
//        this.adView = adView
        adViewLayout?.removeAllViews()
        adViewLayout?.addView(adView)
        // Replace ad container with new ad view.
//        binding.adViewContainer.removeAllViews()
//        binding.adViewContainer.addView(adView)

        adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.d(
                    "AdmobAdx",
                    "banner " + adError?.message + " " + Constants.googleAdMangerBanner
                )
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                Log.d("AdmobAdx", "load")

                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }
        // Start loading the ad in the background.
        val adRequest = AdManagerAdRequest.Builder().build()
        adView.loadAd(adRequest)
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
            } else if (provider.equals(Constants.startApp, true)) {
                Constants.startAppId = interstitialAdValue
            } else if (provider.equals(Constants.unity, true)) {
                Constants.unityGameID = interstitialAdValue
            } else if (provider.equals(Constants.adManagerAds, true)) {
                Constants.googleAdMangerInterstitial = interstitialAdValue
            } else if (provider.equals(Constants.cas_Ai, true)) {
                Constants.casAiId = interstitialAdValue
            }

        } else if (adLocation.equals(Constants.nativeAdLocation, true)) {
            nativeAdValue = adKey.toString()

            if (provider.equals(Constants.admob, true)) {
                Constants.nativeAdmob = nativeAdValue

            } else if (provider.equals(Constants.facebook, true)) {
                Constants.nativeFacebook = nativeAdValue
            } else if (provider.equals(Constants.adManagerAds, true)) {
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
            } else if (provider.equals(Constants.unity, true)) {
                Constants.unityGameID = bannerAdValue

            } else if (provider.equals(Constants.startApp, true)) {
                Constants.startAppId = bannerAdValue
            } else if (provider.equals(Constants.adManagerAds, true)) {
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
        startAppBanner: Banner?
    ) {

        if (provider.equals(Constants.admob, true)) {
            adMobSdkInitializationOrAdmobAd(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner
            )
        }
        if (provider.equals(Constants.adManagerAds, true)) {
            adMobSdkInitializationOrAdmobAdWithManager(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner
            )
        } else if (provider.equals(Constants.facebook, true)) {
            facebookSdkInitialization(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner
            )
        } else if (provider.equals(Constants.unity, true)) {
            unitySdkInitialization(adLocation, adView, linearLayout, relativeLayout, startAppBanner)
        } else if (provider.equals(Constants.chartBoost, true)) {
            chartboostSdkInitialization(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner
            )
        } else if (provider.equals(Constants.startApp, true)) {
            startAppSdkInitialization(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner
            )
        } else if (provider.equals(Constants.cas_Ai, true)) {
            //Cas Ai provider Initialization.....
            casAiSdkInitialization(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner
            )
        }
    }


    private fun casAiSdkInitialization(
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?
    ) {
        if (Constants.isCasAiInit) {
            loadAdAtParticularLocation(
                adLocation,
                Constants.cas_Ai, adView, linearLayout, relativeLayout, banner
            )
        } else {
            //InitializeCasAiSdk....
//            CAS.settings.debugMode = BuildConfig.DEBUG
            CAS.settings.taggedAudience = Audience.NOT_CHILDREN
            casAiAdManager = CAS.buildManager()
                .withManagerId(Constants.casAiId)
//                .withTestAdMode(BuildConfig.DEBUG)
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
                            Constants.cas_Ai, adView, linearLayout, relativeLayout, banner
                        )
                    } else {
                        Constants.isCasAiInit = false
                        Log.d(CAS_TAG, "Ad manager initialization failed: " + it.error)
                    }
                }
                .build(context)
        }

    }


    private fun unitySdkInitialization(
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?
    ) {

        if (Constants.isUnitySdkInit) {
            loadAdAtParticularLocation(
                adLocation,
                Constants.unity, adView, linearLayout, relativeLayout, banner
            )
        } else {
            UnityAds.initialize(
                context,
                Constants.unityGameID,
                Constants.unityTestMode,
                object : IUnityAdsInitializationListener {
                    override fun onInitializationComplete() {
                        if (UnityAds.isInitialized) {

                            Constants.isUnitySdkInit = true
                            loadAdAtParticularLocation(
                                adLocation,
                                Constants.unity, adView, linearLayout, relativeLayout, banner
                            )

                        }

                    }

                    override fun onInitializationFailed(
                        p0: UnityAds.UnityAdsInitializationError?,
                        p1: String?
                    ) {

                        Constants.isUnitySdkInit = false

                    }
                })

        }
    }


    private fun setUpUnityBannerBottom(relativeLayout: RelativeLayout?) {

        relativeLayout?.removeAllViews()
        bottomBanner = BannerView(activity, bottomAdUnitId, UnityBannerSize(320, 50))
        val bannerListener: BannerView.IListener = object : BannerView.IListener {
            override fun onBannerLoaded(bannerAdView: BannerView) {
                // Called when the banner is loaded.
                logger.printLog(taG, "unityLoaded")

                if (bottomBanner != null) {
                    showBanner(relativeLayout, bottomBanner!!)
                }

            }

            override fun onBannerShown(bannerAdView: BannerView?) {

            }

            override fun onBannerFailedToLoad(
                bannerAdView: BannerView,
                errorInfo: BannerErrorInfo
            ) {
                logger.printLog(taG, "unityFailed" + "  " + errorInfo.errorMessage)
            }

            override fun onBannerClick(bannerAdView: BannerView) {

            }

            override fun onBannerLeftApplication(bannerAdView: BannerView) {
            }
        }
        bottomBanner?.listener = bannerListener
        bottomBanner?.load()
    }


    private fun setUpUnityBanner(relativeLayout: RelativeLayout?) {
        // Listener for banner events:

        relativeLayout?.removeAllViews()

        topBannerUnity = BannerView(activity, bottomAdUnitId, UnityBannerSize(320, 50))
        val bannerListener: BannerView.IListener = object : BannerView.IListener {
            override fun onBannerLoaded(bannerAdView: BannerView) {
                // Called when the banner is loaded.
                logger.printLog(taG, "unityLoaded")

                if (topBannerUnity != null) {
                    showBanner(relativeLayout, topBannerUnity!!)
                }

            }

            override fun onBannerShown(bannerAdView: BannerView?) {

            }

            override fun onBannerFailedToLoad(
                bannerAdView: BannerView,
                errorInfo: BannerErrorInfo
            ) {
                logger.printLog(taG, "unityFailed" + "  " + errorInfo.errorMessage)
            }

            override fun onBannerClick(bannerAdView: BannerView) {

            }

            override fun onBannerLeftApplication(bannerAdView: BannerView) {
            }
        }
        topBannerUnity?.listener = bannerListener
        topBannerUnity?.load()

    }

    private fun showBanner(s: RelativeLayout?, insideBanner: BannerView) {
//        s?.removeAllViews()
        s?.addView(insideBanner)
    }


    ///Function to initialize admob sdk...
    private fun adMobSdkInitializationOrAdmobAd(
        locationName: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?
    ) {
        if (Constants.isInitAdmobSdk) {
            loadAdAtParticularLocation(
                locationName,
                Constants.admob, adView, linearLayout, relativeLayout, banner
            )
        } else {
            MobileAds.initialize(context) { p0 ->
                Constants.isInitAdmobSdk = true
                loadAdAtParticularLocation(
                    locationName,
                    Constants.admob, adView, linearLayout, relativeLayout, banner
                )
            }
        }


    }

    private fun adMobSdkInitializationOrAdmobAdWithManager(
        locationName: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?
    ) {
        if (Constants.isInitAdmobSdk) {
            loadAdAtParticularLocation(
                locationName,
                Constants.adManagerAds, adView, linearLayout, relativeLayout, banner
            )
        } else {
            MobileAds.initialize(context) { p0 ->
                Constants.isInitAdmobSdk = true
                loadAdAtParticularLocation(
                    locationName,
                    Constants.adManagerAds, adView, linearLayout, relativeLayout, banner
                )
            }
        }


    }


    fun showAds(adProviderShow: String) {
        if (adProviderShow.equals(Constants.admob, true)) {
            showAdmobInterstitial()
        } else if (adProviderShow.equals(Constants.unity, true)) {
            showUnityAd()
        } else if (adProviderShow.equals(Constants.chartBoost, true)) {
            showChartBoost()
        } else if (adProviderShow.equals(Constants.facebook, true)) {
            showFacebookAdInterstitial()
        } else if (adProviderShow.equals(Constants.startApp, true)) {
            showStartAppAd()
        } else if (adProviderShow.equals(Constants.adManagerAds, true)) {

            showAdmobInterstitialAdx()
        }

    }


    private fun showUnityAd() {
        val showListener: IUnityAdsShowListener = object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(
                placementId: String,
                error: UnityAds.UnityAdsShowError,
                message: String
            ) {
                adManagerListener.onAdFinish()

            }

            override fun onUnityAdsShowStart(placementId: String) {

            }

            override fun onUnityAdsShowClick(placementId: String) {

            }

            override fun onUnityAdsShowComplete(
                placementId: String,
                state: UnityAds.UnityAdsShowCompletionState
            ) {
                adManagerListener.onAdFinish()

            }
        }
        UnityAds.show(
            activity,
            "Interstitial_Android",
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
        banner: Banner?
    ) {


        if (locationName.equals(Constants.adLocation1, true) ||
            locationName.equals(Constants.adLocation2top, true) ||
            locationName.equals(Constants.adLocation2bottom, true) ||
            locationName.equals(Constants.adLocation2topPermanent, true)
        ) {

            if (adProviderName.equals(Constants.admob, true)) {
                loadAdmobBanner(adView)
            } else if (adProviderName.equals(Constants.facebook, true)) {
                loadFaceBookBannerAd(context, linearLayout)
            } else if (adProviderName.equals(Constants.adManagerAds, true)) {
                loadAdmobBannerAdx(adView)
            } else if (adProviderName.equals(Constants.unity, true)) {

                if (locationName.equals(Constants.adLocation1, true)) {
                    setUpUnityBanner(relativeLayout)
                }
                if (locationName.equals(Constants.adLocation2topPermanent, true)) {
                    setUpUnityBanner(relativeLayout)
                }

                if (locationName.equals(Constants.adLocation2top, true)) {
                    setUpUnityBanner(relativeLayout)
                }

                if (locationName.equals(Constants.adLocation2bottom, true)) {
                    setUpUnityBannerBottom(relativeLayout)
                }
            } else if (adProviderName.equals(Constants.startApp, true)) {
                setStartAppBanner(banner)
            } else if (adProviderName.equals(Constants.cas_Ai, true)) {
                loadCasAiBannerAd(linearLayout)
            }

        } else if (locationName.equals(Constants.nativeAdLocation)) {
            if (adProviderName.equals(Constants.admob, true)) {
                loadAdmobNativeAdWithoutPopulate()
            } else if (adProviderName.equals(Constants.facebook, true)) {
                loadFacebookNativeAdWithoutPopulate()
            }
        } else {

            if (adProviderName.equals(Constants.admob, true)) {
                loadAdmobInterstitialAd()
            } else if (adProviderName.equals(Constants.unity, true)) {
                loadUnityAdInterstitial()
            } else if (adProviderName.equals(Constants.adManagerAds, true)) {
                loadAdmobInterstitialAdx()
            } else if (adProviderName.equals(Constants.chartBoost, true)) {
                loadChartBoost()
            } else if (adProviderName.equals(Constants.facebook, true)) {
                loadFacebookInterstitialAd()
            } else if (adProviderName.equals(Constants.startApp, true)) {
                loadStartAppAd()
            } else if (adProviderName.equals(Constants.cas_Ai, true)) {
                //Cas Ai Interstitial......
            }

        }

    }

    private fun loadCasAiBannerAd(linearLayout: LinearLayout?) {
        linearLayout?.removeAllViews()
        val adManager = casAiAdManager
        createBanner(adManager,linearLayout)
    }
    private fun createBanner(manager: MediationManager?, linearLayout: LinearLayout?) {
//        val container = findViewById<LinearLayout>(R.id.container)
        val bannerView = CASBannerView(context, manager)

        // Set required Ad size
        bannerView.size = com.cleversolutions.ads.AdSize.getAdaptiveBannerInScreen(context)
        //bannerView.size = AdSize.BANNER
        //bannerView.size = AdSize.LEADERBOARD
        //bannerView.size = AdSize.MEDIUM_RECTANGLE

//        val label = findViewById<TextView>(R.id.bannerStatusText)
        // Set Ad content listener
        bannerView.adListener = object : AdViewListener {
            override fun onAdViewLoaded(view: CASBannerView) {
                linearLayout?.visibility = View.VISIBLE
//                label.text = "Loaded"
                Log.d(CAS_TAG, "Banner Ad loaded and ready to present")
            }

            override fun onAdViewFailed(view: CASBannerView, error: com.cleversolutions.ads.AdError) {
//                label.text = error.message
                linearLayout?.visibility = View.GONE
                Log.e(CAS_TAG, "Banner Ad received error: " + error.message)
            }

            override fun onAdViewPresented(view: CASBannerView, info: AdImpression) {
//                label.text = "Presented: " + info.network
                Log.d(CAS_TAG, "Banner Ad presented from " + info.network)
            }

            override fun onAdViewClicked(view: CASBannerView) {
//                label.text = "Clicked"
                Log.d(CAS_TAG, "Banner Ad received Click action")
            }
        }

        // Add view to container
        linearLayout?.addView(bannerView)

        // Set controls
//        findViewById<Button>(R.id.loadBannerBtn).setOnClickListener {
//            label.text = "Loading"
//            bannerView.loadNextAd()
//        }
//
//        findViewById<Button>(R.id.showBannerBtn).setOnClickListener {
//            bannerView.visibility = View.VISIBLE
//        }
//
//        findViewById<Button>(R.id.hideBannerBtn).setOnClickListener {
//            bannerView.visibility = View.GONE
//        }
    }



    private fun loadStartAppAd() {
        startAppAd = StartAppAd(context)
//        startAppAd?.loadAd(object : AdEventListener {
//
//            override fun onReceiveAd(p0: com.startapp.sdk.adsbase.Ad) {
//                Log.d("StartAppAd","msg")
//                showStartAppAd()
//
//            }
//
//            override fun onFailedToReceiveAd(p0: com.startapp.sdk.adsbase.Ad?) {
//                Log.d("StartAppAd","msg"+p0?.errorMessage)
//            }
//        })
        StartAppAd(context).let { interstitialAd ->
            interstitialAd.loadAd(object : AdEventListener {


                override fun onReceiveAd(p0: com.startapp.sdk.adsbase.Ad) {

//                    interstitialAdFlow.value = Pair(interstitialAd, com.startapp.sdk.adsbase.Ad.AdState.READY)
                    showInterstitial(interstitialAd)
                    Log.d("StartAppAd", "msg")
                }

                override fun onFailedToReceiveAd(p0: com.startapp.sdk.adsbase.Ad?) {
                    Log.d("StartAppAd", "msg" + p0?.errorMessage)
                }
            })
        }
    }

    private fun showInterstitial(ad: StartAppAd?) {
        ad?.let {
//            interstitialAdFlow.value = null

            it.showAd(object : AdDisplayListener {

                override fun adHidden(p0: com.startapp.sdk.adsbase.Ad?) {

                }

                override fun adDisplayed(p0: com.startapp.sdk.adsbase.Ad?) {

                }

                override fun adClicked(p0: com.startapp.sdk.adsbase.Ad?) {

                }

                override fun adNotDisplayed(p0: com.startapp.sdk.adsbase.Ad?) {

                }
            })
        } ?: run {
            Toast.makeText(context, "Interstitial is not ready", Toast.LENGTH_SHORT).show()
        }
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
                adManagerListener.onAdLoad("success")

            }

            override fun onUnityAdsFailedToLoad(
                placementId: String,
                error: UnityAds.UnityAdsLoadError,
                message: String
            ) {

                adManagerListener.onAdLoad("failed")


            }
        }


        UnityAds.load("Interstitial_Android", loadListener)
    }


    private fun chartboostSdkInitialization(
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?
    ) {
        if (Constants.isChartboostSdkInit) {
            loadAdAtParticularLocation(
                adLocation,
                Constants.chartBoost, adView, linearLayout, relativeLayout, banner
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
                        Constants.chartBoost, adView, linearLayout, relativeLayout, banner
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
        banner: Banner?
    ) {
        if (Constants.isInitFacebookSdk) {
            loadAdAtParticularLocation(
                adLocation,
                Constants.facebook, adView, linearLayout, relativeLayout, banner
            )
        } else {
            AudienceNetworkAds
                .buildInitSettings(context)
                .withInitListener {
                    if (it.isSuccess) {
                        Constants.isInitFacebookSdk = true
                        loadAdAtParticularLocation(
                            adLocation,
                            Constants.facebook, adView, linearLayout, relativeLayout, banner
                        )

                    } else {

                        Constants.isInitFacebookSdk = false
                    }

                }
                .initialize()


        }


    }


    ////startapp sdk initialization....
    private fun startAppSdkInitialization(
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?
    ) {

        if (Constants.isStartAppSdkInit) {
            loadAdAtParticularLocation(
                adLocation,
                Constants.startApp, adView, linearLayout, relativeLayout, banner
            )
        } else {
            try {
//                StartAppSDK.init(context, Constants.startAppId, false)
//                StartAppAd.disableSplash()
//                StartAppSDK.setTestAdsEnabled(true)

                ///////////////////////////////////
//                StartAppSDK.setTestAdsEnabled(true)
                StartAppAd.disableSplash()
                // NOTE for the testing purposes you can use demo App ID: 205489527
                StartAppSDK.initParams(context, Constants.startAppId)
                    .setReturnAdsEnabled(false)
                    .setCallback { Constants.isStartAppSdkInit = true }
                    .init()

                loadAdAtParticularLocation(
                    adLocation,
                    Constants.startApp, adView, linearLayout, relativeLayout, banner
                )


            } catch (e: Exception) {

                logger.printLog(taG, "StartAppError" + e.message)
            }
        }


    }


    private fun setStartAppBanner(bannerView: Banner?) {
        val banner = Banner(activity, object : BannerListener {
            override fun onReceiveAd(p0: View?) {
                bannerView?.addView(p0)
                bannerView?.visibility = View.VISIBLE
                bannerView?.showBanner()
            }

            override fun onFailedToReceiveAd(p0: View?) {
                bannerView?.visibility = View.GONE

            }

            override fun onImpression(p0: View?) {

            }

            override fun onClick(p0: View?) {

            }


        })
        banner.loadAd()
    }


    //loadChartBoost
    private fun loadChartBoost() {
        chartboostInterstitial = Interstitial("location", object : InterstitialCallback {
            override fun onAdDismiss(event: DismissEvent) {
                adManagerListener.onAdFinish()
            }

            override fun onAdLoaded(event: CacheEvent, error: CacheError?) {
                adManagerListener.onAdLoad("success")
            }

            override fun onAdRequestedToShow(event: ShowEvent) {}
            override fun onAdShown(event: ShowEvent, error: ShowError?) {


            }

            override fun onAdClicked(event: ClickEvent, error: ClickError?) {
                adManagerListener.onAdFinish()
            }

            override fun onImpressionRecorded(event: ImpressionEvent) {}


        })
        chartboostInterstitial?.cache()
    }

    ///Show chartboost ads
    private fun showChartBoost(
    ) {

        if (chartboostInterstitial?.isCached() == true) { // check is cached is not mandatory
            chartboostInterstitial?.show()
        } else {
            adManagerListener.onAdFinish()
        }

    }

    ///setAdmobBanner....
    private fun loadAdmobBanner(adViewLayout: LinearLayout?) {

        adViewLayout?.removeAllViews()
        val adView = AdView(context)
        adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER)
        adView.adUnitId = Constants.admobBannerId
        adViewLayout?.addView(adView)
        adView.adListener = object : AdListener() {
            override fun onAdClicked() {

            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                Log.d(
                    "BannerLoad", "load" + Constants.admobBannerId + "  " + p0.code
                )
                adViewLayout?.visibility = View.GONE

            }

            override fun onAdLoaded() {
                Log.d("BannerLoad", "load")

                adViewLayout?.visibility = View.VISIBLE

            }

            override fun onAdClosed() {

            }
        }

        val adRequest = AdRequest.Builder()
            .build()
        adView.loadAd(adRequest)
    }

    ///LoadFacebook banner ad.....
    private fun loadFaceBookBannerAd(context: Context?, adContainer: LinearLayout?) {
        val adView =
            com.facebook.ads.AdView(context, Constants.fbPlacementIdBanner, AdSize.BANNER_HEIGHT_50)
        // AdSettings.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        // Add the ad view to your activity layout
        adContainer?.removeAllViews()
        adContainer?.addView(adView)

        val adListener: com.facebook.ads.AdListener = object : com.facebook.ads.AdListener {
            override fun onError(ad: Ad?, adError: AdError) {
                // Ad error callback

            }

            override fun onAdLoaded(ad: Ad?) {
                // Ad loaded callback

                adContainer?.visibility = View.VISIBLE
            }

            override fun onAdClicked(ad: Ad?) {
                // Ad clicked callback
            }

            override fun onLoggingImpression(ad: Ad?) {
                // Ad impression logged callback
            }
        }
        // Request an ad
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build())


    }


    ///Admob load function...
    private fun loadAdmobInterstitialAd() {

        val adRequest = AdManagerAdRequest.Builder().build()

        AdManagerInterstitialAd.load(context, admobInterstitial,
            adRequest, object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    logger.printLog(taG, "admobProvider : ${adError.message}")
                    mInterstitialAd = null
                    adManagerListener.onAdLoad("failed")
                }

                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    logger.printLog(taG, "admobProvider : ${interstitialAd.responseInfo}")
                    mInterstitialAd = interstitialAd
                    adManagerListener.onAdLoad("success")
                }
            })

    }


    ////showAdmobInterstitial
    private fun showAdmobInterstitial() {

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                mInterstitialAd = null
                adManagerListener.onAdFinish()
            }

            override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                // Called when ad fails to show.
                mInterstitialAd = null
                adManagerListener.onAdFinish()
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
            adManagerListener.onAdFinish()
            logger.printLog(taG, "admob interstitial not loaded successfully")
        }


    }

    fun loadFacebookNativeAdWithoutPopulate() {
        fbNativeAd = NativeAd(context, Constants.nativeFacebook)
        val nativeAdListener: NativeAdListener = object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {

                // Native ad finished downloading all assets
            }

            override fun onError(ad: Ad?, adError: AdError) {


                // Native ad failed to load
            }

            override fun onAdLoaded(ad: Ad) {
                // Native ad is loaded and ready to be displayed
                if (fbNativeAd != null) {
                    Constants.currentNativeAdFacebook = fbNativeAd
//                    inflateFbNativeAd(fbNativeAd!!, nativeAdLayout)

                }
            }

            override fun onAdClicked(ad: Ad) {
                // Native ad clicked
            }

            override fun onLoggingImpression(ad: Ad) {
                // Native ad impression
            }
        }

        // Request an ad
        fbNativeAd?.buildLoadAdConfig()
            ?.withAdListener(nativeAdListener)
            ?.build().let {
                fbNativeAd?.loadAd(
                    it
                )
            }
    }


    fun loadFacebookNativeAd(
        fbNativeAd: NativeAd, nativeAdLayout: NativeAdLayout,
        adLayout: ConstraintLayout?
    ) {
        val nativeAdListener: NativeAdListener = object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {

                // Native ad finished downloading all assets
            }

            override fun onError(ad: Ad?, adError: AdError) {

                adLayout?.visibility = View.GONE
                // Native ad failed to load
            }

            override fun onAdLoaded(ad: Ad) {
                adLayout?.visibility = View.INVISIBLE
                // Native ad is loaded and ready to be displayed
                inflateFbNativeAd(fbNativeAd, nativeAdLayout)
            }

            override fun onAdClicked(ad: Ad) {
                // Native ad clicked
            }

            override fun onLoggingImpression(ad: Ad) {
                // Native ad impression
            }
        }

        // Request an ad
        fbNativeAd.buildLoadAdConfig()
            .withAdListener(nativeAdListener)
            .build().let {
                fbNativeAd.loadAd(
                    it
                )
            }
    }


    //    ////To inflate facebook native view
    fun inflateFbNativeAd(
        fbNativeAd: NativeAd, nativeAdLayout2: NativeAdLayout
    ) {
        fbNativeAd.unregisterView()
        nativeAdLayout = nativeAdLayout2
        val inflater = LayoutInflater.from(context)
        val fbAdView =
            inflater.inflate(
                R.layout.layout_fb_native_ad,
                nativeAdLayout,
                false
            ) as LinearLayout?
        nativeAdLayout?.addView(fbAdView)
        facebookbinding = fbAdView?.let { DataBindingUtil.bind(it) }
        // Add the AdOptionsView
        val adOptionsView = AdOptionsView(context, fbNativeAd, nativeAdLayout)
        facebookbinding?.adChoicesContainer?.removeAllViews()
        facebookbinding?.adChoicesContainer?.addView(adOptionsView, 0)
        // Set the Text.
        facebookbinding?.nativeAdTitle?.text = fbNativeAd.advertiserName
        facebookbinding?.nativeAdBody?.text = fbNativeAd.adBodyText
        facebookbinding?.nativeAdSocialContext?.text = fbNativeAd.adSocialContext
        if (fbNativeAd.hasCallToAction()) {
            facebookbinding?.nativeAdCallToAction?.visibility = View.VISIBLE
        } else {
            facebookbinding?.nativeAdCallToAction?.visibility = View.INVISIBLE
        }
        facebookbinding?.nativeAdCallToAction?.text = fbNativeAd.adCallToAction
        facebookbinding?.nativeAdSponsoredLabel?.text = fbNativeAd.sponsoredTranslation

        val clickableViews = ArrayList<View>()
        facebookbinding?.nativeAdTitle?.let { clickableViews.add(it) }
        facebookbinding?.nativeAdCallToAction?.let { clickableViews.add(it) }


        fbNativeAd.registerViewForInteraction(
            fbAdView,
            facebookbinding?.nativeAdMedia,
            facebookbinding?.nativeAdIcon,
            clickableViews
        )
    }

    fun loadAdmobNativeAdWithManager(
        view_holder: RecyclerView.ViewHolder?, nativeAdView: NativeAdView,
        adLayout: ConstraintLayout?
    ) {
        val builder = AdLoader.Builder(context, Constants.googleAdMangerNative)
        builder.forNativeAd { nativeAd ->

            currentNativeAd?.destroy()
            currentNativeAd = nativeAd

        }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.d(
                    "AdmobAdx",
                    "native " + loadAdError.message + " " + Constants.googleAdMangerNative
                )

                logger.printLog(taG, "Admob Native $loadAdError")
                nativeAdView.visibility = View.GONE
                adLayout?.visibility = View.GONE
            }

            override fun onAdLoaded() {
                if (view_holder != null) {
                    adView = view_holder?.itemView?.findViewById(R.id.native_ad_view)
                } else {
                    adView = nativeAdView
                }
                adLayout?.visibility = View.INVISIBLE
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    adView?.let {
                        currentNativeAd?.let { it1 ->
                            populateNativeAdView(
                                it1,
                                it
                            )
                        }
                    }

                }, 100)
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())

    }

    //////
    fun loadAdmobNativeAd(
        view_holder: RecyclerView.ViewHolder?, nativeAdView: NativeAdView,
        adLayout: ConstraintLayout?
    ) {
        val builder = AdLoader.Builder(context, nativeAdmob)
        builder.forNativeAd { nativeAd ->

            currentNativeAd?.destroy()
            currentNativeAd = nativeAd

        }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {

                logger.printLog(taG, "Admob Native $loadAdError")
                nativeAdView.visibility = View.GONE
                adLayout?.visibility = View.GONE
            }

            override fun onAdLoaded() {
                if (view_holder != null) {
                    adView = view_holder?.itemView?.findViewById(R.id.native_ad_view)
                } else {
                    adView = nativeAdView
                }
                adLayout?.visibility = View.INVISIBLE
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    adView?.let {
                        currentNativeAd?.let { it1 ->
                            populateNativeAdView(
                                it1,
                                it
                            )
                        }
                    }

                }, 100)
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())

    }


    fun populateNativeAdView(
        nativeAd: com.google.android.gms.ads.nativead.NativeAd,
        adView: NativeAdView
    ) {
        try {

            adView.visibility = View.VISIBLE
            // Set the media view.
//            adView.mediaView = adView.findViewById(com.google.android.gms.ads.R.id.ad_media)
            // Set other ad assets.
//            adView.headlineView = adView.findViewById(R.id.headline)
//            adView.bodyView = adView.findViewById(com.google.android.ads.nativetemplates.R.id.body)
            adView.callToActionView = adView.findViewById(R.id.cta)
            adView.iconView = adView.findViewById(R.id.icon)
//            adView.priceView = adView.findViewById(com.google.android.gms.ads.R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.rating_bar)
            adView.storeView = adView.findViewById(R.id.secondary)
            adView.headlineView = adView.findViewById(R.id.primary)
//            adView.advertiserView = adView.findViewById(com.google.android.gms.ads.R.id.ad_advertiser)

//           val primaryView =  findViewById(R.id.primary)
            // The headline and media content are guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline
//            nativeAd.mediaContent?.let { adView.mediaView?.setMediaContent(it) }

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
//            if (nativeAd.body == null) {
//                adView.bodyView?.visibility = View.INVISIBLE
//            } else {
//                adView.bodyView?.visibility = View.VISIBLE
//                (adView.bodyView as TextView).text = nativeAd.body
//            }

            if (nativeAd.callToAction == null) {
                adView.callToActionView?.visibility = View.INVISIBLE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }


            if (nativeAd.icon == null) {
                adView.iconView?.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon?.drawable
                )
                adView.iconView?.visibility = View.VISIBLE
            }

//            if (nativeAd.price == null) {
//                adView.priceView?.visibility = View.INVISIBLE
//            } else {
//                adView.priceView?.visibility = View.VISIBLE
//                (adView.priceView as TextView).text = nativeAd.price
//            }

            if (nativeAd.store == null) {
                adView.storeView?.visibility = View.INVISIBLE
            } else {
                adView.storeView?.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }

            if (nativeAd.starRating == null) {
                adView.starRatingView?.visibility = View.INVISIBLE
            } else {

                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView?.visibility = View.VISIBLE
            }

            if (nativeAd.advertiser == null) {
                adView.advertiserView?.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView?.visibility = View.VISIBLE
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd)


            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
//            // have a video asset.
//            val vc = nativeAd.mediaContent?.videoController
//
//            // Updates the UI to say whether or not this ad has a video asset.
//            if (vc?.hasVideoContent() == true) {
//                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
//                // VideoController will call methods on this object when events occur in the video
//                // lifecycle.
//                vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
//
//                }
//            }
        } catch (e: Exception) {

        }

    }


    ///load facebook interstitial....
    private fun loadFacebookInterstitialAd() {
        facebookinterstitial =
            InterstitialAd(context, Constants.facebookPlacementIdInterstitial)
        val adListener = object : InterstitialAdListener {

            override fun onInterstitialDisplayed(p0: Ad?) {

            }

            override fun onAdClicked(p0: Ad?) {
                adManagerListener.onAdFinish()
            }

            override fun onInterstitialDismissed(p0: Ad?) {
                adManagerListener.onAdFinish()
            }

            override fun onError(p0: Ad?, p1: AdError?) {
                adManagerListener.onAdLoad("failed")
            }

            override fun onAdLoaded(p0: Ad?) {
                adManagerListener.onAdLoad("success")
            }

            override fun onLoggingImpression(p0: Ad?) {

            }


        }
        val loadAdConfig = facebookinterstitial!!.buildLoadAdConfig()
            .withAdListener(adListener)
            .build()

        facebookinterstitial!!.loadAd(loadAdConfig)

    }

    private fun loadAdmobNativeAdWithoutPopulate() {
        val builder = AdLoader.Builder(context, nativeAdmob)
        builder.forNativeAd { nativeAd ->

            Constants.currentNativeAd?.destroy()
            Constants.currentNativeAd = nativeAd
        }

        val videoOptions = VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.d("adfailed", "fail" + loadAdError)
            }

            override fun onAdLoaded() {
                Log.d("adfailed", "loaded")

            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())

    }

    /// show facebook interstitial....
    private fun showFacebookAdInterstitial() {
        if (facebookinterstitial != null) {
            if (facebookinterstitial!!.isAdLoaded) {

                try {
                    facebookinterstitial!!.show()
                } catch (e: Throwable) {
                    adManagerListener.onAdFinish()
                    logger.printLog(taG, "Exception" + e.message)
                }


            } else {

                adManagerListener.onAdFinish()
            }
        } else {

            adManagerListener.onAdFinish()
        }


    }


}