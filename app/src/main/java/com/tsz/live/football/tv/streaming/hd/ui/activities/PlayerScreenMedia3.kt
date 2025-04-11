package com.tsz.live.football.tv.streaming.hd.ui.activities

import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Debug
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.security.KeyChainException
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.util.Rational
import android.view.Menu
import android.view.MenuItem
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.ClippingConfiguration
import androidx.media3.common.MediaItem.DrmConfiguration
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.dash.DashChunkSource
import androidx.media3.exoplayer.dash.DefaultDashChunkSource
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.DefaultDrmSessionManagerProvider
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.drm.LocalMediaDrmCallback
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.ima.ImaAdsLoader
import androidx.media3.exoplayer.ima.ImaServerSideAdInsertionMediaSource
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ads.AdsLoader
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.TrackSelector
import androidx.media3.exoplayer.upstream.BandwidthMeter
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.navArgs
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.tsz.live.football.tv.streaming.hd.R
import com.tsz.live.football.tv.streaming.hd.adsdata.AdManager
import com.tsz.live.football.tv.streaming.hd.adsdata.NewAdManager
import com.tsz.live.football.tv.streaming.hd.databinding.ActivityExoTestPlayerBinding
import com.tsz.live.football.tv.streaming.hd.databinding.ExoPlaybackControlViewBinding
import com.tsz.live.football.tv.streaming.hd.models.DrmModel
import com.tsz.live.football.tv.streaming.hd.models.FormatDataAudioMedia3
import com.tsz.live.football.tv.streaming.hd.models.FormatDataMedia3
import com.tsz.live.football.tv.streaming.hd.utils.AppContextProvider
import com.tsz.live.football.tv.streaming.hd.utils.HomeWatcher
import com.tsz.live.football.tv.streaming.hd.utils.Logger
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.adBefore
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.adLocation2bottom
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.adLocation2topPermanent
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.calculateDifferenceBetweenDates
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.clearKeyKey
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.dash
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.location2BottomProvider
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.location2TopPermanentProvider
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.locationAfter
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.locationBeforeProvider
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.timeValueAtPlayer
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.userType3
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.xForwardedKey
import com.tsz.live.football.tv.streaming.hd.utils.objects.DebugChecker
import com.tsz.live.football.tv.streaming.hd.utils.objects.Defemation
import com.tsz.live.football.tv.streaming.hd.utils.objects.InternetUtils.isPrivateDnsSetup
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.AdManagerListener
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.OnHomePressedListener
import com.tsz.live.football.tv.streaming.hd.utils.playerUtils.ExpendedActivity
import com.tsz.live.football.tv.streaming.hd.utils.playerUtils.PlayerScreenBottomSheetLangMedia3
import com.tsz.live.football.tv.streaming.hd.utils.playerUtils.PlayerScreenBottomSheetMedia3
import com.tsz.live.football.tv.streaming.hd.viewModel.DataViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.Principal
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.Enumeration
import java.util.UUID
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class PlayerScreenMedia3 : AppCompatActivity(), Player.Listener,
    AdManagerListener,

    SessionManagerListener<CastSession> {


    ///Local class Variables....
    private var binding: ActivityExoTestPlayerBinding? = null
    private val logger = Logger()
    private var path = ""
    private var channel_Type = ""
    private var baseUrl = ""
    private var mCastSession: CastSession? = null
    private var mSessionManagerListener: SessionManagerListener<CastSession>? = null
    private var playerMedia3: ExoPlayer? = null
    private var adStatus = false
    private var viewCount = 0
    private var mLocation: PlaybackLocation? = null
    private var mCastContext: CastContext? = null
    private val tAG = "PlayerScreenClass"
    private var lockCounter = 0
    private var isLockMode: Boolean = false
    private var mediaRouteMenuItem: MenuItem? = null
    private val viewModel by lazy {
        ViewModelProvider(this)[DataViewModel::class.java]
    }
    private var context: Context? = null
    private var mPlaybackState: PlaybackState? = null
    private var count = 1
    private var orientationEventListener: OrientationEventListener? = null
    private var bindingExoPlayback: ExoPlaybackControlViewBinding? = null
    private var adManager: AdManager? = null

    ///Playback location
    enum class PlaybackLocation {
        LOCAL, REMOTE
    }

    private var booleanVpn: Boolean? = false

    //Checking player state...
    enum class PlaybackState {
        PLAYING, IDLE
    }

    val SUPPORTED_TRACK_TYPES = ImmutableList.of(
        C.TRACK_TYPE_VIDEO, C.TRACK_TYPE_AUDIO, C.TRACK_TYPE_TEXT, C.TRACK_TYPE_IMAGE
    )
    private var isShowingTrackSelectionDialog = false
    var compareValue: Long = 0
    private val mHomeWatcher = HomeWatcher(this)
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var isActivityResumed = false
    private var timer: CountDownTimer? = null
    private var isChromcast = false
    private var isAdShowning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exo_test_player)
        context = this
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        isAdShowning = false

        val exoView: ConstraintLayout? = binding?.playerView?.findViewById(R.id.exoControlView)
        bindingExoPlayback = exoView?.let { ExoPlaybackControlViewBinding.bind(it) }
        mLocation = PlaybackLocation.LOCAL
        initializeFirebaseAnalytics()
        if (isGooglePlayServicesAvailable(this)) {
            initializeCastSdk()
        }
        setupActionBar()
        changeOrientation()
        getNavValues()
        checkForAds()
        screenModeController()
        bindingExoPlayback?.moreOption?.setOnClickListener {
            if (playerMedia3 != null) {
                if (!isShowingTrackSelectionDialog
                    && willHaveContent(playerMedia3!!)
                ) {
                    getTracksPresentInsideUrl(playerMedia3)
                }
            }
        }

        bindingExoPlayback?.lang?.setOnClickListener {
            if (playerMedia3 != null) {
                if (!isShowingTrackSelectionDialog && willHaveContent(playerMedia3!!)) {
                    getAudioPresentInsideUrl(playerMedia3)
                }
            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            checkHomeButton()
        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                ////
                if (!locationAfter.equals("none", true)) {
                    /////////
                    if (!locationAfter.equals(Constants.startApp, true)) {
                        if (playerMedia3 != null) {
                            playerMedia3?.stop()
                            playerMedia3!!.release()
                            playerMedia3 = null
                        }
                        isAdShowning = true
                        val local = AppContextProvider.getContext()
                        local?.let {
                            NewAdManager.showAds(
                                locationAfter,
                                this@PlayerScreenMedia3,
                                it
                            )
                        }
                        binding?.lottiePlayer?.visibility = View.VISIBLE
                    } else {
                        Constants.videoFinish = true
                        finish()
                    }

                } else {
                    Constants.videoFinish = true
                    finish()
                }


            }
        })
    }


    private fun initializeFirebaseAnalytics() {
        try {
            firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        } catch (e: java.lang.Exception) {
            Log.d("Exception", "msg")
        }
    }

    private fun getAudioPresentInsideUrl(player: ExoPlayer?) {
        if (player?.currentTracks != null) {
            if (!player.currentTracks.isEmpty) {
                Constants.dataFormatsAudioMedia3.clear()
                val tracks = player.currentTracks
//                val videoTracks = tracks.groups.filter { it.type == C.TRACK_TYPE_VIDEO }
//                Constants.dataFormatsAudio.add(FormatDataAudio(null, null))
                tracks.groups.forEach { trackGroup ->
                    (0 until trackGroup.length).forEach { i ->
                        Log.d("valuesAudio" + " text", "${trackGroup.length}")
                        if (trackGroup.type == C.TRACK_TYPE_TEXT) {
                            val track = trackGroup.getTrackFormat(i)
                            if (track.language != null) {
                                Constants.dataFormatsAudioMedia3.add(
                                    FormatDataAudioMedia3(
                                        track,
                                        trackGroup
                                    )
                                )
                            }
                            Log.d("valuesAudio" + " text", "${track.language}")
                            // ...
                        } else if (trackGroup.type == C.TRACK_TYPE_AUDIO) {
                            val track = trackGroup.getTrackFormat(i)
                            if (track.language != null) {
                                Constants.dataFormatsAudioMedia3.add(
                                    FormatDataAudioMedia3(
                                        track,
                                        trackGroup
                                    )
                                )
                            }
                            Log.d("valuesAudio" + "Audio", track.language.toString() + " " + i)
                        }

                    }
                }


                if (!Constants.dataFormatsAudioMedia3.isNullOrEmpty()) {
                    val fullscreenModal =
                        PlayerScreenBottomSheetLangMedia3(
                            player,
                            Constants.dataFormatsAudioMedia3.size
                        )
                    supportFragmentManager.let {
                        fullscreenModal.show(
                            it,
                            "FullscreenModalBottomSheetDialog"
                        )
                    }
                }

            }
        }
        /////
    }

    //    @OptIn(UnstableApi::class)
    private fun getTracksPresentInsideUrl(player: ExoPlayer?) {
        if (player?.currentTracks != null) {
            if (!player.currentTracks.isEmpty) {
                Constants.dataFormatsMedia3.clear()
                val tracks = player.currentTracks
                val videoTracks = tracks.groups.filter { it.type == C.TRACK_TYPE_VIDEO }
                Constants.dataFormatsMedia3.add(FormatDataMedia3(null, null))
                videoTracks.forEach { trackGroup ->
                    (0 until trackGroup.length).forEach { i ->
                        val track = trackGroup.getTrackFormat(i)
                        val format = track.height
                        val width = track.width
                        Constants.dataFormatsMedia3.add(FormatDataMedia3(track, trackGroup))
                    }
                }


                if (!Constants.dataFormatsMedia3.isNullOrEmpty()) {
                    val fullscreenModal =
                        PlayerScreenBottomSheetMedia3(player, Constants.dataFormatsMedia3.size)
                    supportFragmentManager.let {
                        fullscreenModal.show(
                            it,
                            "FullscreenModalBottomSheetDialog"
                        )
                    }
                }

            }
        }

        /////

    }

    @OptIn(UnstableApi::class)
    private fun setUpPlayer(link: String?) {
        binding?.lottiePlayer?.visibility = View.GONE
        binding?.playerView?.visibility = View.VISIBLE
        val meter: BandwidthMeter = DefaultBandwidthMeter.Builder(this).build()
        val trackSelector: TrackSelector = DefaultTrackSelector(this)
        // 2. Create a default LoadControl
        playerMedia3 = null
        val loadControl: LoadControl = DefaultLoadControl()
        playerMedia3 = context?.let {
            ExoPlayer.Builder(it)
                .setBandwidthMeter(meter)
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl)
                .build()
        }
        binding?.playerView?.player = playerMedia3
        binding?.playerView?.keepScreenOn = true
        //Initialize data source factory
        val defaultDataSourceFactory = DefaultDataSource.Factory(this)
        val mediaItem2 = MediaItem.Builder()
            .setUri(link)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()

        //Initialize hlsMediaSource
        val hlsMediaSource: HlsMediaSource =
            HlsMediaSource.Factory(defaultDataSourceFactory).createMediaSource(mediaItem2)
        val concatenatedSource = ConcatenatingMediaSource(hlsMediaSource)

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding?.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            bindingExoPlayback?.fullScreenIcon?.setImageDrawable(
                context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_full_screen
                    )
                }
            )
            count = 1
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding?.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }

        when (mLocation) {
            PlaybackLocation.LOCAL -> {
                if (mCastSession != null && mCastSession?.remoteMediaClient != null) {
                    mCastSession?.remoteMediaClient?.stop()
                    mCastContext?.sessionManager?.endCurrentSession(true)
                }
                mPlaybackState =
                    PlaybackState.IDLE

                if (playerMedia3 != null) {
                    playerMedia3?.addListener(this)
                    playerMedia3?.setMediaSource(concatenatedSource)
                    playerMedia3?.prepare()
                    binding?.playerView?.requestFocus()
                    playerMedia3?.playWhenReady = true
                }

            }

            PlaybackLocation.REMOTE -> {
                mCastSession?.remoteMediaClient?.play()
                mPlaybackState =
                    PlaybackState.PLAYING
            }

            else -> {
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkHomeButton() {
        try {
            mHomeWatcher.setOnHomePressedListener(object : OnHomePressedListener {
                override fun onHomePressed() {
                    // do something here...
                    ////
                    if (!isInPictureInPictureMode) {
                        if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                            try {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    val params = updatePictureInPictureParams()
                                    if (!isFinishing) {
                                        enterPictureInPictureMode(params)
                                    }
                                }
                            } catch (e: java.lang.Exception) {
                                val bundle = Bundle()
                                bundle.putString("pipFinish", "not Resumed")
                                firebaseAnalytics?.logEvent("pipFinish", bundle)
                                Log.d("Exception", "msg")
                            }
                        } else {
                            val bundle = Bundle()
                            bundle.putString("resume", "not Resumed")
                            firebaseAnalytics?.logEvent("resumeStatus", bundle)
                            Log.d("Exception", "msg")
                        }
                    } else {
                        val bundle = Bundle()
                        bundle.putString("pipMode", "Already")
                        firebaseAnalytics?.logEvent("pipMode", bundle)
                    }
                }

                override fun onHomeLongPressed() {
                    /////
                    if (!isInPictureInPictureMode) {
                        if (isActivityResumed) {
                            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        val params = updatePictureInPictureParams()
                                        if (!isFinishing) {
                                            enterPictureInPictureMode(params)
                                        }
                                    }
                                } catch (e: java.lang.Exception) {
                                    val bundle = Bundle()
                                    bundle.putString("pipFinish", "not Resumed")
                                    firebaseAnalytics?.logEvent("pipFinish", bundle)
                                    Log.d("Exception", "msg")
                                }
                            } else {
                                val bundle = Bundle()
                                bundle.putString("resume", "not Resumed")
                                firebaseAnalytics?.logEvent("resumeStatus", bundle)
                                Log.d("Exception", "msg")
                            }
                        }
                    }
                }

            })
            mHomeWatcher.startWatch()
        } catch (e: java.lang.Exception) {
            val bundle = Bundle()
            bundle.putString("pipException", "error" + e.message)
            firebaseAnalytics?.logEvent("pipException", bundle)
            Log.d("Exception", "msg")
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun updatePictureInPictureParams(): PictureInPictureParams {
        // Calculate the aspect ratio of the PiP screen.
        var aspectRatio: Rational? = null
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            aspectRatio = binding?.minView?.width?.let {
//                binding?.minView?.height?.let { it1 ->
//                    Rational(
//                        it,
//                        it1
//                    )
//                }
//            }
            ////
            aspectRatio = binding?.minView?.width?.let {
                binding?.minView?.height?.let { it1 ->
                    clampAspectRatio(
                        it,
                        it1
                    )
                }
            }

        } else {
//            aspectRatio = binding?.playerView?.width?.let {
//                binding?.playerView?.height?.let { it1 ->
//                    Rational(
//                        it,
//                        it1
//                    )
//                }
//            }

            aspectRatio = binding?.playerView?.width?.let {
                binding?.playerView?.height?.let { it1 ->
                    clampAspectRatio(
                        it,
                        it1
                    )
                }
            }
        }
//        playerScreenInPip = true
        binding?.myToolbar?.visibility = View.GONE
        bindingExoPlayback?.exoControlView?.visibility = View.GONE
        Constants.playerActivityInPip = true
        removeAllAdsViews()
        binding?.topAdLay?.visibility = View.GONE
        binding?.bottomAdLay?.visibility = View.GONE
        // The  view turns into the picture-in-picture mode.
        val visibleRect = Rect()
        binding?.playerView?.getGlobalVisibleRect(visibleRect)
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)
            // Specify the portion of the screen that turns into the picture-in-picture mode.
            // This makes the transition animation smoother.
            .setSourceRectHint(visibleRect)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // The screen automatically turns into the picture-in-picture mode when it is hidden
            // by the "Home" button.
            params.setAutoEnterEnabled(true)
        }
        return params.build().also {
            setPictureInPictureParams(it)
        }
    }


    private fun removeAllAdsViews() {
        binding?.adViewTop?.removeAllViews()
        binding?.fbAdViewTop?.removeAllViews()
        binding?.unityBannerView?.removeAllViews()
        binding?.startAppBannerTop?.removeAllViews()
        binding?.adViewTopPermanent?.removeAllViews()
        binding?.adViewBottom?.removeAllViews()
        binding?.fbAdViewBottom?.removeAllViews()
        binding?.unityBannerViewBottom?.removeAllViews()
        binding?.startAppBannerBottom?.removeAllViews()
        if (binding?.mainTimerLay?.isVisible == true) {
            binding?.countDownTimerAd?.removeAllViews()
        }
    }


    private fun clampAspectRatio(width: Int, height: Int): Rational {
        val aspectRatio = width.toFloat() / height
        return when {
            aspectRatio < 0.418410 -> Rational(41841, 100000) // Minimum allowed aspect ratio
            aspectRatio > 2.390000 -> Rational(239000, 100000) // Maximum allowed aspect ratio
            else -> Rational(width, height)
        }
    }


    private fun willHaveContent(player: Player): Boolean {
        return willHaveContent(player.currentTracks)
    }

    private fun willHaveContent(tracks: Tracks): Boolean {
        for (trackGroup in tracks.groups) {
            if (SUPPORTED_TRACK_TYPES.contains(trackGroup.type)) {
                return true
            }
        }
        return false
    }

    //    @OptIn(UnstableApi::class)
    private fun setUpPlayerP2p(link: String?) {

    }

    private fun isGooglePlayServicesAvailable(context: Context): Boolean {
        try {
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
            return resultCode == ConnectionResult.SUCCESS
        } catch (e: Exception) {
            return false
        }

    }


    private fun getNavValues() {
        try {
            val channelData: PlayerScreenMedia3Args by navArgs()
            baseUrl = channelData.baseURL.toString()
            path = channelData.linkAppend.toString()
            channel_Type = channelData.channleType.toString()

            ////

            if (isProxyEnabled(this)) {
                /// if proxy is enabled.....
            } else {
                if (isProxySet() || isLocalProxyRunning() || detectFrida() ||
                    detectXposed()  || isLibraryTampered()
                ) {
                } else {

                    if (isCertificateInjected(this) || detectUserInstalledCertificates(this)) {
                        // Certificate is injected
                    } else {
                        val channelTime = channelData.channelTime
                        if (channelTime != null) {
                            if (channelTime != compareValue) {
                                checkTimeValueAndStartTimer(channelTime)
                            } else {
                                hideTimerLayout()
                                getChannelTyppeAndPlay()
                            }
                        } else {
                            hideTimerLayout()
                            getChannelTyppeAndPlay()
                        }
                    }
                }
            }

//            val channelTime = channelData.channelTime
//            if (channelTime != null) {
//                if (channelTime != compareValue) {
//                    checkTimeValueAndStartTimer(channelTime)
//                } else {
//                    hideTimerLayout()
//                    getChannelTyppeAndPlay()
//                }
//            } else {
//                hideTimerLayout()
//                getChannelTyppeAndPlay()
//            }

        } catch (e: java.lang.Exception) {
            Log.d("Exception", "msg")
        }

        /////
    }


    private fun detectUserInstalledCertificates(context: Context): Boolean {
        return try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val aliases: Enumeration<String> = keyStore.aliases()
            while (aliases.hasMoreElements()) {
                val alias = aliases.nextElement()
                val certificate: Certificate? = keyStore.getCertificate(alias)
                if (certificate is X509Certificate) {
                    val issuer: Principal = certificate.issuerDN
                    if (issuer.name.contains("CN=user")) {
                        return true
                    }
                }
            }
            false
        } catch (e: KeyChainException) {
            Log.e("detectUser", "KeyChainException", e)
            false
        } catch (e: InterruptedException) {
            Log.e("detectUser", "InterruptedException", e)
            false
        } catch (e: Exception) {
            Log.e("detectUser", "Exception", e)
            false
        }
    }


    private fun checkTimeValueAndStartTimer(selectedChannelTimeInMillis: Long) {
        lifecycleScope.launch(Dispatchers.Main) {
            val timerValue = calculateDifferenceBetweenDates(selectedChannelTimeInMillis)
            val hoursMat = timerValue / (60 * 60 * 1000)
            val minutesMat = (timerValue % (60 * 60 * 1000)) / (60 * 1000)
            Log.d("Difference", "$hoursMat $minutesMat  second(s) remaining")

            /////////////
            if (hoursMat == compareValue && minutesMat <= timeValueAtPlayer) {
                hideTimerLayout()
                timerFinishAndPlayAgain()
            } else {
                if (timeValueAtPlayer > 60) {
                    val convertIntoHours: Long = (timeValueAtPlayer / 60).toLong()
                    val minutesRemaining: Long = (timeValueAtPlayer % 60).toLong()
                    if (hoursMat <= convertIntoHours) {
                        if (hoursMat == convertIntoHours) {
                            if (minutesMat <= minutesRemaining) {
                                hideTimerLayout()
                                timerFinishAndPlayAgain()
                            } else {
                                showTimerLay()
                                timer = object : CountDownTimer(Math.abs(timerValue), 1000) {
                                    // Callback fired on regular interval.
                                    override fun onTick(millisUntilFinished: Long) {
                                        val hours = millisUntilFinished / (60 * 60 * 1000)
                                        val minutes =
                                            (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000)
                                        val seconds = (millisUntilFinished % (60 * 1000)) / 1000
                                        if (hours <= convertIntoHours && minutes <= minutesRemaining) {
                                            hideTimerLayout()
                                            timerFinishAndPlayAgain()
                                        } else {
                                            binding?.timeValue?.text =
                                                "$hours : $minutes : $seconds"
                                        }
                                    }

                                    // Callback fired when the time is up.
                                    override fun onFinish() {
                                        hideTimerLayout()
                                        timerFinishAndPlayAgain()
                                        Log.d("Difference", "Done!!!")
                                    }
                                }.start() // Start the countdown timer
                            }
                        } else {
                            hideTimerLayout()
                            timerFinishAndPlayAgain()
                        }
                    } else {
                        showTimerLay()
                        timer = object : CountDownTimer(Math.abs(timerValue), 1000) {
                            // Callback fired on regular interval.
                            override fun onTick(millisUntilFinished: Long) {
                                val hours = millisUntilFinished / (60 * 60 * 1000)
                                val minutes = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000)
                                val seconds = (millisUntilFinished % (60 * 1000)) / 1000
                                if (hours <= convertIntoHours && minutes <= minutesRemaining) {
                                    hideTimerLayout()
                                    timerFinishAndPlayAgain()
                                } else {
                                    binding?.timeValue?.text = "$hours : $minutes : $seconds"
                                }
                            }

                            // Callback fired when the time is up.
                            override fun onFinish() {
                                hideTimerLayout()
                                timerFinishAndPlayAgain()
                                Log.d("Difference", "Done!!!")
                            }
                        }.start() // Start the countdown timer
                    }
                } else {
                    if (timerValue > 0) {
                        showTimerLay()
                        timer = object : CountDownTimer(Math.abs(timerValue), 1000) {
                            // Callback fired on regular interval.
                            override fun onTick(millisUntilFinished: Long) {
                                val hours = millisUntilFinished / (60 * 60 * 1000)
                                val minutes = (millisUntilFinished % (60 * 60 * 1000)) / (60 * 1000)
                                val seconds = (millisUntilFinished % (60 * 1000)) / 1000
                                if (hours == compareValue && minutes <= timeValueAtPlayer) {
                                    hideTimerLayout()
                                    timerFinishAndPlayAgain()
                                } else {
                                    binding?.timeValue?.text = "$hours : $minutes : $seconds"
                                }
                            }

                            // Callback fired when the time is up.
                            override fun onFinish() {
                                hideTimerLayout()
                                timerFinishAndPlayAgain()
                                Log.d("Difference", "Done!!!")
                            }
                        }.start() // Start the countdown timer
                    } else {
                        hideTimerLayout()
                        timerFinishAndPlayAgain()
                    }
                }

            }

        }
    }


    private fun getSystemTrustStore(): KeyStore? {
        return try {
            // Get the default TrustManagerFactory
            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

            // Initialize it with null (to use the system's default trust store)
            trustManagerFactory.init(null as KeyStore?)

            // Get the TrustManagers
            val trustManagers = trustManagerFactory.trustManagers

            // Find the X509TrustManager (the one that handles X.509 certificates)
            val x509TrustManager =
                trustManagers.firstOrNull { it is X509TrustManager } as? X509TrustManager

            // If we found an X509TrustManager, create a KeyStore and add the trusted CAs to it
            x509TrustManager?.let {
                val keyStore = KeyStore.getInstance("AndroidCAStore")
                keyStore.load(null, null) // Load the KeyStore (no password needed)
                keyStore
            } ?: run {
                Log.e("getSystemTrustStore", "X509TrustManager not found.")
                null
            }
        } catch (e: Exception) {
            Log.e("getSystemTrustStore", "Error getting system trust store", e)
            null
        }
    }

    private fun getUserTrustStore(): KeyStore? {
        return try {
            KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
            }
        } catch (e: Exception) {
            Log.e("CertificateChecker", "Error getting user trust store", e)
            null
        }
    }

    private fun getCertificatesFromTrustStore(keyStore: KeyStore?): List<X509Certificate> {
        return try {
            keyStore?.aliases()?.toList()?.mapNotNull { alias ->
                keyStore.getCertificate(alias) as? X509Certificate
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("CertificateChecker", "Error getting certificates from trust store", e)
            emptyList()
        }
    }


    private fun isCertificateInSystemTrustStore(
        certificate: X509Certificate,
        systemTrustStore: KeyStore?
    ): Boolean {
        return try {
            systemTrustStore?.getCertificateAlias(certificate) != null
        } catch (e: Exception) {
            Log.e("CertificateChecker", "Error checking if certificate is in system trust store", e)
            false
        }
    }

    ////////
    private fun isCertificateChainValid(certificate: X509Certificate): Boolean {
        return try {
            certificate.checkValidity()
            true
        } catch (e: Exception) {
            Log.e("CertificateChecker", "Error checking certificate chain validity", e)
            false
        }
    }

    private fun isCertificateSignatureValid(certificate: X509Certificate): Boolean {
        return try {
            certificate.verify(certificate.publicKey)
            true
        } catch (e: Exception) {
            Log.e("CertificateChecker", "Error checking certificate signature validity", e)
            false
        }
    }

    fun isCertificateInjected(context: Context): Boolean {
        val systemTrustStore = getSystemTrustStore()
        val userTrustStore = getUserTrustStore()

//        val systemCertificates = getCertificatesFromTrustStore(systemTrustStore)
        val userCertificates = getCertificatesFromTrustStore(userTrustStore)

        for (userCertificate in userCertificates) {
            if (!isCertificateInSystemTrustStore(userCertificate, systemTrustStore)) {
                Log.w(
                    "CertificateChecker",
                    "Potential injected certificate found: ${userCertificate.subjectDN}"
                )
                if (!isCertificateChainValid(userCertificate)) {
                    Log.w(
                        "CertificateChecker",
                        "Certificate chain is not valid: ${userCertificate.subjectDN}"
                    )
                }
                if (!isCertificateSignatureValid(userCertificate)) {
                    Log.w(
                        "CertificateChecker",
                        "Certificate signature is not valid: ${userCertificate.subjectDN}"
                    )
                }
                return true
            }
        }

        return false
    }


    fun isLocalProxyRunning(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("netstat -an")
            val reader = process.inputStream.bufferedReader()
            val output = reader.use { it.readText() }
            output.contains("127.0.0.1:8888") || output.contains("localhost")
        } catch (e: Exception) {
            false
        }
    }

    fun detectXposed(): Boolean {
        val paths = arrayOf(
            "/system/framework/XposedBridge.jar",
            "/system/lib/libxposed_art.so",
            "/data/app/de.robv.android.xposed.installer-1.apk"
        )
        return paths.any { File(it).exists() }
    }

    fun isLibraryTampered(): Boolean {
        val lib32 = File("/system/lib/libssl.so")
        val lib64 = File("/system/lib64/libssl.so")
        return !(lib32.exists() || lib64.exists()) // If neither exists, it’s tampered
    }

    private fun showTimerLay() {
        binding?.mainTimerLay?.visibility = View.VISIBLE
        if (binding?.mainTimerLay?.isVisible == true) {
            loadLocation2TopAtTimerScreen()
        }
    }

    private fun loadLocation2TopAtTimerScreen() {
        if (!location2TopPermanentProvider.equals("none", true)) {
            adManager?.loadAdProvider(
                location2TopPermanentProvider, adLocation2topPermanent,
                binding?.countDownTimerAd, null, null, null
            )
        }

    }

    private fun hideTimerLayout() {
        binding?.playerView?.visibility = View.VISIBLE
        timer?.cancel()
        binding?.mainTimerLay?.visibility = View.GONE
    }

    private fun timerFinishAndPlayAgain() {
        getChannelTyppeAndPlay()
    }

    private fun getChannelTyppeAndPlay() {
        if (channel_Type.equals(dash, true)) {
            if (path.isNotEmpty()) {
                binding?.lottiePlayer?.visibility = View.VISIBLE
//                viewModel.fetchData()
//                viewModel.clearKeyString.observe(this) {
//                    if (!it.isNullOrEmpty()) {
//
//                    }
//                }

                setUpPlayerDashWithAgent(path, "it")
            }
        } else if (channel_Type.equals(Constants.userType1, true)) {
            lifecycleScope.launch(Dispatchers.Main) {
                setUpPlayer(path)
            }
        } else if (channel_Type.equals(Constants.userType2, true)) {
            binding?.lottiePlayer?.visibility = View.VISIBLE

            viewModel.getDemoData()
            viewModel.userLinkStatus.observe(this) {
                if (it == true) {
                    path = Constants.userLink
                    lifecycleScope.launch(Dispatchers.Main) {
                        setUpPlayer(path)
                    }
                }
            }
        } else if (channel_Type.equals(Constants.hlsSource, true)) {
            if (path.isNotEmpty()) {
                setUpPlayerWithM3u8(path)
            }
        } else if (channel_Type.equals(Constants.userTypePhp, true)) {
            binding?.lottiePlayer?.visibility = View.VISIBLE
            fetchData(path)
        }

    }


    private val client = OkHttpClient()
    fun fetchData(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url(path)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        withContext(Dispatchers.Main){
                            binding?.lottiePlayer?.visibility = View.VISIBLE
                        }
                    }

                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        withContext(Dispatchers.Main){
                            if (responseBody.contains("m3u8")){
                                setUpPlayerWithM3u8(responseBody)
                            }else{
                                val separatedPart =
                                    responseBody?.split(",")

                                if (separatedPart?.size!! > 1){

                                    val firstPart = separatedPart?.get(0).toString()
                                    val secondPart= separatedPart?.get(1).toString()
                                    binding?.lottiePlayer?.visibility = View.GONE
                                    setUpPlayerPhp(firstPart,secondPart)
                                }
                                else{
                                    setUpPlayerDashWithAgent(responseBody,"")
                                }
                            }
                        }
                    } else {
                        Log.e("MainActivity", "Response body is null")
                    }
                }
            } catch (e: java.lang.Exception) {
                withContext(Dispatchers.Main){
                    binding?.lottiePlayer?.visibility = View.GONE
                }
                Log.e("MainActivity", "Error fetching data: ${e.message}")
            }
        }
    }



    @OptIn(UnstableApi::class)
    private fun setUpPlayerPhp(path: String, secondPart: String) {
        val drmLicenseRequestHeaders = ImmutableMap.of<String, String>()
        val adaptiveMimeType =
            Util.getAdaptiveMimeTypeForContentType(
                if (TextUtils.isEmpty(null)
                ) Util.inferContentType(path)
                else Util.inferContentTypeForExtension(null.toString())
            )

        val trackSelectionParameters = TrackSelectionParameters.Builder(this).build()


        var playBitrate: String = "BT2000000"
        var setPlayBitrate = 0

        playBitrate = playBitrate.replace("BT", "")
        setPlayBitrate = playBitrate.toInt()


        // default off subtitles
        val newTrackSelectionParameters = trackSelectionParameters
            .buildUpon()
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
            .setMinVideoBitrate(setPlayBitrate)
            .build()

        val uuidString = "e2719d58-a985-b3c9-781a-b030af78d30e"
        val clippingConfiguration =
            MediaItem.ClippingConfiguration.Builder()

        val dashMediaItem = MediaItem.Builder().setUri(path)
            .setMimeType(adaptiveMimeType)
            .setClippingConfiguration(clippingConfiguration.build())
            .setMediaMetadata(
                MediaMetadata.Builder().setTitle("Football Streaming")
                    .build()
            ).setDrmConfiguration(
                MediaItem.DrmConfiguration.Builder(UUID.fromString(uuidString))
                    .setLicenseUri(secondPart)
                    .setLicenseRequestHeaders(drmLicenseRequestHeaders)
                    .setForceSessionsForAudioAndVideoTracks(false)
                    .setMultiSession(false)
                    .setForceDefaultLicenseUri(false)
                    .build()
            ).build()


        val defaultHttpDataSourceFactory: DataSource.Factory? = DefaultHttpDataSource.Factory()
        var dataSourceFactory: DataSource.Factory? = null


        if (xForwardedKey != null) {
            if (xForwardedKey.isNotEmpty()) {
                dataSourceFactory = DefaultDataSource.Factory(
                    this, CustomHttpDataSourceFactoryWithHeader()
                )

            } else {
                dataSourceFactory = DefaultDataSource.Factory(
                    this, CustomHttpDataSourceFactory(Constants.USER_AGENT)
                )
            }
        } else {
            dataSourceFactory = DefaultDataSource.Factory(
                this, CustomHttpDataSourceFactory(Constants.USER_AGENT)
            )
        }

        if (xForwardedKey.isNotEmpty() && Constants.USER_AGENT!="ExoPlayer-Drm"){
            dataSourceFactory = DefaultDataSource.Factory(
                this, CustomHttpDataSourceFactoryWithMulti(Constants.USER_AGENT)
            )
        }
        else{
            Log.d("ElsePart","else")
        }



        val dashMediaSource = dataSourceFactory?.let {
            androidx.media3.exoplayer.dash.DashMediaSource.Factory(it)
//                .setDrmSessionManagerProvider { customDrmSessionManager }
                .createMediaSource(dashMediaItem)
        }

        val loadControl = DefaultLoadControl()

        //////////
        playerMedia3 = ExoPlayer.Builder(this)
//            .setTrackSelector(newTrackSelectionParameters)
            .setLoadControl(loadControl)
//            .setMediaSourceFactory(
//                createMediaSourceFactory(
//                    binding?.playerView,
//                    defaultHttpDataSourceFactory
//                )
//            )
            .setSeekForwardIncrementMs(10000L)
            .setSeekBackIncrementMs(10000L)
            .build()



        binding?.playerView?.player = playerMedia3
        binding?.playerView?.keepScreenOn = true

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding?.playerView?.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FIT
            bindingExoPlayback?.fullScreenIcon?.setImageDrawable(context?.let {
                ContextCompat.getDrawable(
                    it, R.drawable.ic_full_screen
                )
            })
            count = 1
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding?.playerView?.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FIT
        }

        when (mLocation) {
            PlaybackLocation.LOCAL -> {
                if (mCastSession != null && mCastSession?.remoteMediaClient != null) {
                    mCastSession?.remoteMediaClient?.stop()
                    mCastContext?.sessionManager?.endCurrentSession(true)
                }
                mPlaybackState = PlaybackState.IDLE

                if (playerMedia3 != null) {
                    playerMedia3?.addListener(this)
                    playerMedia3?.trackSelectionParameters = newTrackSelectionParameters
                    dashMediaSource?.let { playerMedia3?.setMediaSource(it) }
//                    playerMedia3?.setMediaItem(dashMediaItem)
                    playerMedia3?.prepare()
                    binding?.playerView?.requestFocus()
                    playerMedia3?.playWhenReady = true
                }

            }

            PlaybackLocation.REMOTE -> {
                mCastSession?.remoteMediaClient?.play()
                mPlaybackState = PlaybackState.PLAYING
            }

            else -> {
            }
        }
    }



    @UnstableApi
    class CustomHttpDataSourceFactoryWithMulti(
        private val userAgent: String,
    ) : androidx.media3.datasource.HttpDataSource.Factory {
        override fun createDataSource(): androidx.media3.datasource.HttpDataSource {
            val dataSource =
                DefaultHttpDataSource.Factory().createDataSource()
            dataSource.setRequestProperty("X-Forwarded-For", xForwardedKey)
            dataSource.setRequestProperty("User-Agent", userAgent)
            return dataSource
        }

        override fun setDefaultRequestProperties(defaultRequestProperties: MutableMap<String, String>): androidx.media3.datasource.HttpDataSource.Factory {
            TODO("Not yet implemented")
        }
    }




    private fun checkForAds() {
        adManager = context?.let { AdManager(it, this, this) }


        if (locationBeforeProvider.equals(Constants.startApp, true)) {
            adManager?.loadAdProvider(
                locationBeforeProvider, adBefore,
                null, null, null, null
            )
        }
        if (binding?.mainTimerLay?.isVisible != true) {
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            } else {
                loadLocation2BottomProvider()
            }
            loadLocation2TopPermanentProvider()
        }
    }

    private fun loadLocation2BottomProvider() {
        if (!location2BottomProvider.equals("none", true)) {
            binding?.adViewBottom?.let { it1 ->
                binding?.fbAdViewBottom?.let { it2 ->
                    adManager?.loadAdProvider(
                        location2BottomProvider, adLocation2bottom,
                        it1, it2, binding?.unityBannerViewBottom, binding?.startAppBannerBottom
                    )
                }
            }
        }
    }

    //    @OptIn(UnstableApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            // Hide the controls in picture-in-picture mode.
            binding?.myToolbar?.visibility = View.GONE

        } else {
            // Show the video controls if the video is not playing
//            binding?.playerView?.showController()
            binding?.topAdLay?.visibility = View.VISIBLE
            binding?.bottomAdLay?.visibility = View.VISIBLE
            val orientation = resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                againFillAdViewsIfPortrait()
            }
            if (binding?.mainTimerLay?.isVisible == true) {
                loadLocation2TopAtTimerScreen()
            }

            binding?.myToolbar?.visibility = View.VISIBLE
            bindingExoPlayback?.exoControlView?.visibility = View.VISIBLE
        }
    }

    private fun againFillAdViewsIfPortrait() {
        loadLocation2TopPermanentProvider()
        loadLocation2BottomProvider()
    }


    private fun loadLocation2TopPermanentProvider() {
        if (!location2TopPermanentProvider.equals("none", true)) {
            binding?.adViewTopPermanent?.let { it1 ->
                binding?.fbAdViewTop?.let { it2 ->
                    adManager?.loadAdProvider(
                        location2TopPermanentProvider, adLocation2topPermanent,
                        it1, it2, binding?.unityBannerView, binding?.startAppBannerTop
                    )
                }
            }
        }

    }

    private fun screenModeController() {
        bindingExoPlayback?.fullScreenIcon?.setOnClickListener {
            when (count) {
                1 -> {

                    setImageViewListener("Fit", R.drawable.fit_mode)

                }

                2 -> {
                    setImageViewListener("Fill", R.drawable.full_mode)

                }

                3 -> {
                    setImageViewListener("Stretch", R.drawable.stretch)

                }

                4 -> {
                    setImageViewListener("Original", R.drawable.ic_full_screen)

                }
            }
        }
        bindingExoPlayback?.lockMode?.setOnClickListener {
            if (lockCounter == 0) {
                isLockMode = true
                viewVisibility(View.GONE)
                bindingExoPlayback?.lockAffect?.visibility = View.VISIBLE
                lockCounter++
            } else {
                viewVisibility(View.VISIBLE)
                isLockMode = false
                bindingExoPlayback?.lockAffect?.visibility = View.GONE
                lockCounter = 0
            }

        }
    }

    private fun viewVisibility(value: Int) {
        resources.configuration.orientation
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            bindingExoPlayback?.fullScreenIcon?.visibility = View.GONE
        } else {
            bindingExoPlayback?.fullScreenIcon?.visibility = value
        }

        bindingExoPlayback?.seekProgress?.visibility = value
        bindingExoPlayback?.exoPlayPause?.visibility = value

    }


    private fun initializeCastSdk() {
        try {
            mCastContext = CastContext.getSharedInstance(this)
            mCastSession = mCastContext?.sessionManager?.currentCastSession
            setupCastListener()
            mCastContext?.sessionManager?.addSessionManagerListener(
                mSessionManagerListener!!, CastSession::class.java
            )
        } catch (e: java.lang.Exception) {
            Log.d("CastSdk", "msg")
        }
    }


    ////Change orientation of screen programmatically......
    private fun changeOrientation() {
        Thread {
            orientationEventListener =
                object : OrientationEventListener(this) {
                    override fun onOrientationChanged(orientation: Int) {
                        val leftLandscape = 90
                        val rightLandscape = 270
                        runOnUiThread {
                            try {
                                if (epsilonCheck(orientation, leftLandscape) ||
                                    epsilonCheck(orientation, rightLandscape)
                                ) {
                                    if (!isLockMode)
                                        requestedOrientation =
                                            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                } else {
                                    if (!isLockMode) {
                                        if (orientation in 0..45 || orientation >= 315 || orientation in 135..225) {
                                            requestedOrientation =
                                                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                                        }

                                    }
                                }
                            } catch (e: Exception) {
                                Log.d("Exception", "msg")
                            }

                        }

                    }

                    private fun epsilonCheck(a: Int, b: Int): Boolean {
                        return a > b - 10 && a < b + 10
                    }
                }
            orientationEventListener?.enable()

        }.start()

    }


    override fun onDestroy() {
        super.onDestroy()
        ///
        try {
//            playerScreenInPip = false

            mCastContext?.sessionManager?.removeSessionManagerListener(
                mSessionManagerListener!!, CastSession::class.java
            )
            Constants.playerActivityInPip = false
            orientationEventListener?.disable()
            if (playerMedia3 != null) {
                playerMedia3!!.release()
                playerMedia3 = null

            }
            mHomeWatcher?.stopWatch()
            val bundle = Bundle()
            bundle.putString("Destroy", "yes")
            firebaseAnalytics?.logEvent("onDestroy", bundle)
            if (binding?.mainTimerLay?.isVisible == true) {
                timer?.cancel()
            }
            finish()
        } catch (e: java.lang.Exception) {
            val bundle = Bundle()
            bundle.putString("Destroy", "yes")
            firebaseAnalytics?.logEvent("onDestroy", bundle)
            Log.d("Exception", "msg")
        }
    }

    ///
//    @OptIn(androidx.media3.common.util.UnstableApi::class)
    @OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun setImageViewListener(string: String, id: Int) {
        if (binding?.playerView != null) {
            bindingExoPlayback?.layoutRight?.visibility = View.VISIBLE
            val animFadeIn =
                AnimationUtils.loadAnimation(
                    applicationContext,
                    R.anim.fade_in
                )
            bindingExoPlayback?.fullScreenIcon?.startAnimation(animFadeIn)
            when (string) {
                "Fit" -> {
                    binding?.playerView?.resizeMode =
                        AspectRatioFrameLayout.RESIZE_MODE_FILL
                    count++

                }

                "Fill" -> {
                    binding?.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    playerMedia3?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                    count++
                }

                "Stretch" -> {
                    binding?.playerView?.resizeMode =
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    playerMedia3?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                    count++
                }

                else -> {
                    binding?.playerView?.resizeMode =
                        AspectRatioFrameLayout.RESIZE_MODE_FIT

                    count = 1
                }
            }

            Handler(Looper.getMainLooper()).postDelayed({
                bindingExoPlayback?.layoutRight?.visibility = View.GONE
                changeImageDrawable(id, bindingExoPlayback?.fullScreenIcon)
            }, 500)

        }
        bindingExoPlayback?.chnagedText?.visibility = View.VISIBLE
        bindingExoPlayback?.chnagedText?.text = string
        Handler(Looper.getMainLooper()).postDelayed(
            { bindingExoPlayback?.chnagedText?.visibility = View.GONE },
            2000
        )

    }

    override fun onIsLoadingChanged(isLoading: Boolean) {

    }


    override fun onPlayerError(error: PlaybackException) {
        if (playerMedia3 != null) {
            playerMedia3?.playWhenReady = false
            playerMedia3?.stop()
            playerMedia3?.release()
        }

        againPlay()
    }

    private fun againPlay() {
        try {
            if (channel_Type.equals(dash, true)) {
                if (path.isNotEmpty()) {

//                    viewModel.fetchData()
//                    viewModel.clearKeyString.observe(this) {
//                        if (!it.isNullOrEmpty()) {
//
//                        }
//                    }
                    setUpPlayerDashWithAgent(path, "it")
//                    setUpPlayerDashWithAgent(path, it)
                }
            } else if (channel_Type.equals(Constants.userType1, true)) {
                val token = baseUrl.let { it1 -> Defemation.improveDeprecatedCode(it1) }
                path = baseUrl + token
                setUpPlayer(path)
            } else if (channel_Type.equals(Constants.userType2, true)) {
                path = Constants.userLink
            } else if (channel_Type.equals(Constants.hlsSource, true)) {
                if (path.isNotEmpty()) {
//                    setUpPlayerDashWithM3u8(path)
                    setUpPlayerWithM3u8(path)
                }
            } else if (channel_Type.equals(Constants.userTypePhp, true)) {
                binding?.lottiePlayer?.visibility = View.VISIBLE
                fetchData(path)
            }

        } catch (e: Exception) {
            logger.printLog("Exception", "" + e.message)
        }
    }


    private fun setupActionBar() {
        setSupportActionBar(binding?.myToolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(
            applicationContext,
            menu,
            R.id.media_route_menu_item
        )

        return true
    }


    ///Swipe Brightness Feature...

    ///Screen Brightness.....
    private fun screenBrightness(newBrightnessValue: Double) {

        val lp = window.attributes
        val newBrightness = newBrightnessValue.toFloat()
        lp.screenBrightness = newBrightness / 255f
        window.attributes = lp
    }

    ///Swipe volume feature...


    ///
    fun changeImageDrawable(drawable: Int, imageView: ImageView?) {
        imageView?.setImageDrawable(context?.let { ContextCompat.getDrawable(it, drawable) })

    }


    ////Load remote media when connected with casting device
    private fun loadRemoteMedia() {
        if (mCastSession == null) {
            return
        }
        val remoteMediaClient = mCastSession!!.remoteMediaClient ?: return
        remoteMediaClient.registerCallback(object : RemoteMediaClient.Callback() {
            override fun onStatusUpdated() {
                isChromcast = true
                val intent =
                    Intent(context, ExpendedActivity::class.java)
                startActivity(intent)
                remoteMediaClient.unregisterCallback(this)
            }
        })


        val loadData = MediaLoadRequestData.Builder()
            .setMediaInfo(buildMediaInfo())
            .build()

        remoteMediaClient.load(loadData)
//        buildMediaInfo()?.let { remoteMediaClient.load(loadData) }
    }


    //Function to check proxy network

    fun isProxyEnabled(context: Context): Boolean {
        val proxyHost = System.getProperty("http.proxyHost")
        val proxyPort = System.getProperty("http.proxyPort")
        return !proxyHost.isNullOrEmpty() && !proxyPort.isNullOrEmpty()
    }

    ///

    fun isProxySet(): Boolean {
        val proxyHost = android.net.Proxy.getDefaultHost()
        return !proxyHost.isNullOrEmpty()
    }


    override fun onPause() {
        super.onPause()
        ////
        try {
            isActivityResumed = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!isInPictureInPictureMode) {
                        if (playerMedia3 != null) {
                            playerMedia3!!.playWhenReady = false
                        }
                    } else {
//                removeAllAdsViews()
                        binding?.myToolbar?.visibility = View.GONE
                        bindingExoPlayback?.exoControlView?.visibility = View.GONE
                    }
                } else {
                    if (playerMedia3 != null) {
                        playerMedia3!!.playWhenReady = false
                    }
                }

            } else {
                if (playerMedia3 != null) {
                    playerMedia3!!.playWhenReady = false
                }
            }
        } catch (e: java.lang.Exception) {
            Log.d("Exception", "msg")
        }
    }

    override fun onResume() {
        super.onResume()
        NewAdManager.setAdManager(this)
        Constants.playerActivityInPip = false
//        playerScreenInPip = false

        isChromcast = false
        isAdShowning = false
        isActivityResumed = true
        binding?.myToolbar?.visibility = View.VISIBLE
        bindingExoPlayback?.exoControlView?.visibility = View.VISIBLE
        binding?.topAdLay?.visibility = View.VISIBLE
        binding?.bottomAdLay?.visibility = View.VISIBLE
        if (isPrivateDnsSetup(this)) {
            Toast.makeText(
                this,
                "Please turn off private dns,If not found then search dns in setting search",
                Toast.LENGTH_LONG
            ).show()
            try {
                startActivityForResult(Intent(Settings.ACTION_SETTINGS), 0)
            } catch (e: Exception) {
                Log.d("Exception", "msg")
            }
        }
        hideSystemUI()
        if (mCastSession != null) {
            mCastContext?.sessionManager?.addSessionManagerListener(
                mSessionManagerListener!!, CastSession::class.java
            )
            if (mCastSession != null && mCastSession!!.isConnected) {

                if (playerMedia3 != null) {
                    playerMedia3?.playWhenReady = false
                    playerMedia3?.release()
                }


                try {
                    lifecycleScope.launch(Dispatchers.Main) {
                        if (channel_Type.equals(userType3, true)) {
                            setUpPlayerP2p(path)
                        } else {
//                            setUpPlayer(path)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("Exception", "msg")
                }
            } else {

                updatePlaybackLocation(PlaybackLocation.LOCAL)
            }
        } else {
            if (playerMedia3 != null) {
                if (DebugChecker.checkDebugging(this)) {
                    playerMedia3?.playWhenReady = false
                    playerMedia3?.stop()

                } else {
                    playerMedia3?.playWhenReady = true
                }
            }
        }
        checkVpn()

        if (isProxyEnabled(this)) {
            if (playerMedia3 != null) {
                playerMedia3?.stop()
//                player?.release()
            }
            binding?.proxytext?.text = "Please disable proxy from network settings."
            binding?.adblockLayout?.visibility = View.VISIBLE
        } else {
            if (isProxySet() || isLocalProxyRunning() || detectFrida() ||
                detectXposed()|| isLibraryTampered()
            ) {
                if (playerMedia3 != null) {
                    playerMedia3?.stop()
//                    player?.release()
                }
                binding?.proxytext?.text = "Please disable proxy from network settings."
                binding?.adblockLayout?.visibility = View.VISIBLE
            } else {
                if (isCertificateInjected(this)) {
                    // Certificate is injected
                    binding?.proxytext?.text = "Please disable http Debugger."
                    binding?.adblockLayout?.visibility = View.VISIBLE
                } else {
                    binding?.adblockLayout?.visibility = View.GONE
                }
            }

        }
    }

    fun detectFrida(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("pidof frida-server")
            val output = process.inputStream.bufferedReader().use { it.readText() }
            output.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    private fun checkVpn() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                    val booleanVpnCheck = hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    booleanVpn = booleanVpnCheck == true
                }
            } else {
                booleanVpn = false
            }
        }

        if (booleanVpn != null) {
            if (booleanVpn!!) {
                if (binding?.adblockLayout?.isVisible!!) {
                    /////////

                } else {
                    binding?.adblockLayout?.visibility = View.VISIBLE

                }
            } else {
                binding?.adblockLayout?.visibility = View.GONE

            }
        }

    }


    /////Media builder function
    private fun buildMediaInfo(): MediaInfo? {


        return path?.let {
            MediaInfo.Builder(it)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("application/x-mpegURL")
                .build()
        }
    }

    ///Hide System bar.....
    private fun hideSystemUI() {
        // Set the content to appear under the system bars so that the content
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.let {
            WindowInsetsControllerCompat(window, it).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }


    fun isDebuggerAttached(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }


    //    @OptIn(UnstableApi::class)
    @OptIn(UnstableApi::class)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (binding?.playerView != null) {
            val orientation = newConfig.orientation

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (binding?.mainTimerLay?.isVisible == true) {
                    binding?.mainTimerLay?.background =
                        ContextCompat.getDrawable(this, R.drawable.count_down)
                    val params =
                        binding?.timeValue?.layoutParams as ViewGroup.MarginLayoutParams // Cast to MarginLayoutParams

                    params.setMargins(
                        0,
                        0,
                        0,
                        700
                    ) // Set margins (left, top, right, bottom) in pixels
                    binding?.timeValue?.layoutParams = params
                }
                ////
                if (binding?.mainTimerLay?.isVisible != true) {

                    binding?.adViewTop?.removeAllViews()
                    binding?.fbAdViewTop?.removeAllViews()
                    binding?.unityBannerView?.removeAllViews()
                    binding?.startAppBannerTop?.removeAllViews()

                    loadLocation2TopPermanentProvider()
                    loadLocation2BottomProvider()
                }

                bindingExoPlayback?.fullScreenIcon?.visibility = View.GONE

                binding?.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

                if (binding?.mainTimerLay?.isVisible == true) {
                    binding?.mainTimerLay?.background =
                        ContextCompat.getDrawable(this, R.drawable.count_down_land)
                    val params =
                        binding?.timeValue?.layoutParams as ViewGroup.MarginLayoutParams // Cast to MarginLayoutParams

                    params.setMargins(
                        0,
                        0,
                        0,
                        230
                    ) // Set margins (left, top, right, bottom) in pixels
                    binding?.timeValue?.layoutParams = params
                }
                if (binding?.mainTimerLay?.isVisible != true) {
                    binding?.adViewTopPermanent?.removeAllViews()
                    binding?.adViewBottom?.removeAllViews()
                    binding?.fbAdViewBottom?.removeAllViews()
                    binding?.unityBannerViewBottom?.removeAllViews()
                    binding?.startAppBannerBottom?.removeAllViews()

                    if (!Constants.location2TopProvider.equals("none", true)) {
                        binding?.adViewTop?.let { it1 ->
                            binding?.fbAdViewTop?.let { it2 ->
                                adManager?.loadAdProvider(
                                    Constants.location2TopProvider, Constants.adLocation2top,
                                    it1, it2, binding?.unityBannerView, binding?.startAppBannerTop
                                )
                            }
                        }
                    }
                }

                if (bindingExoPlayback?.lockAffect?.visibility == View.VISIBLE) {
                    //
                    bindingExoPlayback?.fullScreenIcon?.visibility = View.GONE


                } else {

                    bindingExoPlayback?.fullScreenIcon?.visibility = View.VISIBLE
                    binding?.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    bindingExoPlayback?.fullScreenIcon?.setImageDrawable(
                        context?.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.ic_full_screen
                            )
                        }
                    )
                    count = 1

                }

            }
        }

    }

    override fun onStop() {
        super.onStop()
        try {
            Constants.playerActivityInPip = false
//            Log.d("ReleaseVersion","val"+android.os.Build.VERSION.SDK_INT)

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                mCastContext?.sessionManager?.removeSessionManagerListener(
                    mSessionManagerListener!!, CastSession::class.java
                )
                if (playerMedia3 != null) {
                    playerMedia3?.stop()
                    playerMedia3?.release()
                    playerMedia3 = null
                }
                if (isChromcast) {
                    val bundle = Bundle()
                    bundle.putString("Chromcast", "yes")
                    firebaseAnalytics?.logEvent("onStop", bundle)
                } else {
                    if (!isAdShowning) {
                        mHomeWatcher?.stopWatch()
                        val bundle = Bundle()
                        bundle.putString("Chromcast", "false")
                        firebaseAnalytics?.logEvent("onStop", bundle)
                        if (binding?.mainTimerLay?.isVisible == true) {
                            timer?.cancel()
                        }
                        orientationEventListener?.disable()
                        finish()
                    } else {
                        val bundle = Bundle()
                        bundle.putString("AdShowing", "yes")
                        firebaseAnalytics?.logEvent("onStop", bundle)
                    }
                }
                ///if api level is
            } else {
                mCastContext?.sessionManager?.removeSessionManagerListener(
                    mSessionManagerListener!!, CastSession::class.java
                )

            }
        } catch (e: java.lang.Exception) {
            Log.d("Exception", "msg")
        }
    }


    ////Listener for castSession manager
    private fun setupCastListener() {
        if (mSessionManagerListener == null) {
            mSessionManagerListener = object : SessionManagerListener<CastSession> {

                override fun onSessionStarting(castSession: CastSession) {
                }

                override fun onSessionStarted(castSession: CastSession, s: String) {

                    onApplicationConnected(castSession)
                }

                override fun onSessionStartFailed(castSession: CastSession, i: Int) {
                    logger.printLog(tAG, "playback")
                    onApplicationDisconnected()
                }

                override fun onSessionEnding(castSession: CastSession) {

                }

                override fun onSessionEnded(castSession: CastSession, i: Int) {
                    onApplicationDisconnected()
                }

                override fun onSessionResuming(castSession: CastSession, s: String) {}
                override fun onSessionResumed(castSession: CastSession, b: Boolean) {
//                    invalidateOptionsMenu()
//                    onApplicationConnected(castSession)
                }

                override fun onSessionResumeFailed(castSession: CastSession, i: Int) {
                    onApplicationDisconnected()
                }

                override fun onSessionSuspended(castSession: CastSession, i: Int) {

                }

                private fun onApplicationConnected(castSession: CastSession) {
                    mCastSession = castSession
                    if (mPlaybackState != PlaybackState.PLAYING) {
                        playerMedia3?.pause()
                        loadRemoteMedia()
                        return
                    } else {
                        mPlaybackState = PlaybackState.IDLE
                        updatePlaybackLocation(PlaybackLocation.REMOTE)
                    }
                    invalidateOptionsMenu()
                }

                private fun onApplicationDisconnected() {
                    updatePlaybackLocation(PlaybackLocation.LOCAL)
                    mPlaybackState = PlaybackState.IDLE
                    mLocation = PlaybackLocation.LOCAL
                    invalidateOptionsMenu()
                }

            }
        }
    }

    //    @OptIn(UnstableApi::class)
    @OptIn(UnstableApi::class)
    private fun setUpPlayerWithM3u8(path: String) {

        binding?.lottiePlayer?.visibility = View.GONE
//        binding?.playerViewExo?.visibility = View.GONE
        binding?.playerView?.visibility = View.VISIBLE
        val listDrmModel: MutableList<DrmModel> = mutableListOf()


        // Setup DefaultDrmSessionManager for ClearKey
//        val keyId = "9002ec8c3dbc55c5bccdcd6871d80fd0".toByteArray(Charsets.UTF_8)
//        val key = "7099325123bae7810db508727bb0bc7d".toByteArray(Charsets.UTF_8)

        val drmKey = clearKeyKey

//        val drmKeyId =
//            "8ab47741930c476780515f9a00decb0a"

        var drmBody = ""

        if (drmKey.contains(":")) {
            val elementsKey = drmKey.split(":") // Split the string by commas
//            val elementsKeyId = drmKeyId.split(",") // Split the string by commas

            var length = elementsKey.size + 1
            var finalLen = length / 2

            var count = 0
            for (element in 0 until finalLen) {

                val drmKeyIdBytes =
                    elementsKey[count].chunked(2).map { it.toInt(16).toByte() }.toByteArray()
                val encodedDrmKeyId = Base64.encodeToString(
                    drmKeyIdBytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                )

                count += 1

                val drmKeyBytes =
                    elementsKey[count].chunked(2).map { it.toInt(16).toByte() }.toByteArray()
                val encodedDrmKey = Base64.encodeToString(
                    drmKeyBytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                )

                count += 1



                listDrmModel.add(DrmModel("oct", encodedDrmKey, encodedDrmKeyId))
            }

            val jsonObject = JsonObject()
            val jsonArray = JsonArray()

            for (keyData in listDrmModel) {
                val keyJsonObject = JsonObject()
                keyJsonObject.addProperty("kty", keyData.ktyString)
                keyJsonObject.addProperty("k", keyData.drmKey)
                keyJsonObject.addProperty("kid", keyData.drmKeyId)
                jsonArray.add(keyJsonObject)
            }

            jsonObject.add("keys", jsonArray)
            jsonObject.addProperty("type", "temporary")

            val gson = Gson()
            val jsonString = gson.toJson(jsonObject)

            drmBody = jsonString

//            Log.d(
//                "DrmBoday", "body" +drmBody
//            )
        } else {

        }


        val dashMediaItem = MediaItem.Builder().setUri(path).setMimeType(MimeTypes.APPLICATION_M3U8)
            .setMediaMetadata(MediaMetadata.Builder().setTitle("Football Streaming").build())
            .build()

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory()
        val trackSelector = DefaultTrackSelector(this, videoTrackSelectionFactory)
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setPreferredVideoMimeType(MimeTypes.VIDEO_H264)
        )
        /////////
//        val trackSelector = DefaultTrackSelector(this)
        val loadControl = DefaultLoadControl()

        val drmCallback = LocalMediaDrmCallback(drmBody.toByteArray())
        val drmSessionManager =
            DefaultDrmSessionManager.Builder().setPlayClearSamplesWithoutKeys(true)
                .setMultiSession(false).setKeyRequestParameters(HashMap())
                .setUuidAndExoMediaDrmProvider(C.CLEARKEY_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER)
                .build(drmCallback)

        val customDrmSessionManager: DrmSessionManager = drmSessionManager
        ///////////////
        val defaultHttpDataSourceFactory =
            DefaultHttpDataSource.Factory().setUserAgent(Constants.USER_AGENT).setTransferListener(
                DefaultBandwidthMeter.Builder(this).setResetOnNetworkTypeChange(false).build()
            )


        val dashChunkSourceFactory: DashChunkSource.Factory = DefaultDashChunkSource.Factory(
            defaultHttpDataSourceFactory
        )
        val manifestDataSourceFactory =
            DefaultHttpDataSource.Factory().setUserAgent(Constants.USER_AGENT)

        var dataSourceFactory: DataSource.Factory? = null


        if (xForwardedKey != null) {
            if (xForwardedKey.isNotEmpty()) {
                dataSourceFactory = DefaultDataSource.Factory(
                    this, CustomHttpDataSourceFactoryWithHeader()
                )

            } else {
                dataSourceFactory = DefaultDataSource.Factory(
                    this, CustomHttpDataSourceFactory(Constants.USER_AGENT)
                )
            }
        } else {
            dataSourceFactory = DefaultDataSource.Factory(
                this, CustomHttpDataSourceFactory(Constants.USER_AGENT)
            )
        }

        if (xForwardedKey.isNotEmpty() && Constants.USER_AGENT!="ExoPlayer-Drm"){
            dataSourceFactory = DefaultDataSource.Factory(
                this, CustomHttpDataSourceFactoryWithMulti(Constants.USER_AGENT)
            )
        }
        else{
            Log.d("ElsePart","else")
        }

        var mediaSource: MediaSource? = null


        if (clearKeyKey.isNullOrEmpty()) {
            Log.d(
                "ComingInSection", "yes" + " " + Constants.xForwardedKey
                        + " " + path
            )
            mediaSource =
                dataSourceFactory?.let {
                    HlsMediaSource.Factory(dataSourceFactory)
//                    .setDrmSessionManagerProvider { customDrmSessionManager }
                        .createMediaSource(dashMediaItem)
                }
        } else {
            Log.d("ComingInSection", "no")

            mediaSource =
                dataSourceFactory?.let {
                    HlsMediaSource.Factory(dataSourceFactory)
                        .setDrmSessionManagerProvider { customDrmSessionManager }
                        .createMediaSource(dashMediaItem)
                }
        }


        val renderersFactory =
            DefaultRenderersFactory(this).setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)

        ////////
        val renderersFactory2 =
            DefaultRenderersFactory(this).setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                .setEnableDecoderFallback(true)

        playerMedia3 = ExoPlayer.Builder(this).setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
//            .setSeekForwardIncrementMs(10000L)
//            .setSeekBackIncrementMs(10000L)
            .build()

        binding?.playerView?.player = playerMedia3
        binding?.playerView?.keepScreenOn = true

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding?.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            bindingExoPlayback?.fullScreenIcon?.setImageDrawable(context?.let {
                ContextCompat.getDrawable(
                    it, R.drawable.ic_full_screen
                )
            })
            count = 1
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding?.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }

        when (mLocation) {
            PlaybackLocation.LOCAL -> {
                if (mCastSession != null && mCastSession?.remoteMediaClient != null) {
                    mCastSession?.remoteMediaClient?.stop()
                    mCastContext?.sessionManager?.endCurrentSession(true)
                }
                mPlaybackState = PlaybackState.IDLE

                if (playerMedia3 != null) {
                    playerMedia3?.addListener(this)
                    mediaSource?.let { playerMedia3?.setMediaSource(it, true) }
                    playerMedia3?.prepare()
                    binding?.playerView?.requestFocus()
                    playerMedia3?.playWhenReady = true
                }

            }

            PlaybackLocation.REMOTE -> {
                mCastSession?.remoteMediaClient?.play()
                mPlaybackState = PlaybackState.PLAYING
            }

            else -> {
            }
        }
        ////////////
//        setOnGestureListeners()
    }


    //    @OptIn(UnstableApi::class)
    @OptIn(UnstableApi::class)
    private fun setUpPlayerDashWithAgent(path: String, s: String) {

//        binding?.playerViewExo?.visibility = View.GONE
        binding?.playerView?.visibility = View.VISIBLE
        binding?.lottiePlayer?.visibility = View.GONE
        val listDrmModel: MutableList<DrmModel> = mutableListOf()


        // Setup DefaultDrmSessionManager for ClearKey
//        val keyId = "9002ec8c3dbc55c5bccdcd6871d80fd0".toByteArray(Charsets.UTF_8)
//        val key = "7099325123bae7810db508727bb0bc7d".toByteArray(Charsets.UTF_8)

        val drmKey = clearKeyKey

//        val drmKeyId =
//            "8ab47741930c476780515f9a00decb0a"

        var drmBody = ""

        if (drmKey.contains(":")) {
            val elementsKey = drmKey.split(":") // Split the string by commas
//            val elementsKeyId = drmKeyId.split(",") // Split the string by commas

            var length = elementsKey.size + 1
            var finalLen = length / 2

            var count = 0
            for (element in 0 until finalLen) {

                val drmKeyIdBytes =
                    elementsKey[count].chunked(2).map { it.toInt(16).toByte() }.toByteArray()
                val encodedDrmKeyId = Base64.encodeToString(
                    drmKeyIdBytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                )

                count += 1

                val drmKeyBytes =
                    elementsKey[count].chunked(2).map { it.toInt(16).toByte() }.toByteArray()
                val encodedDrmKey = Base64.encodeToString(
                    drmKeyBytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                )

                count += 1



                listDrmModel.add(DrmModel("oct", encodedDrmKey, encodedDrmKeyId))
            }

            val jsonObject = JsonObject()
            val jsonArray = JsonArray()

            for (keyData in listDrmModel) {
                val keyJsonObject = JsonObject()
                keyJsonObject.addProperty("kty", keyData.ktyString)
                keyJsonObject.addProperty("k", keyData.drmKey)
                keyJsonObject.addProperty("kid", keyData.drmKeyId)
                jsonArray.add(keyJsonObject)
            }

            jsonObject.add("keys", jsonArray)
            jsonObject.addProperty("type", "temporary")

            val gson = Gson()
            val jsonString = gson.toJson(jsonObject)

            drmBody = jsonString

//            Log.d(
//                "DrmBoday", "body" +drmBody
//            )
        } else {
//            val drmKeyBytes =
//                drmKey.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
//            val encodedDrmKey = Base64.encodeToString(
//                drmKeyBytes,
//                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
//            )
//
//            val drmKeyIdBytes = drmKeyId.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
//            val encodedDrmKeyId = Base64.encodeToString(
//                drmKeyIdBytes,
//                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
//            )
//            drmBody =
//                "{\"keys\":[{\"kty\":\"oct\",\"k\":\"${encodedDrmKey}\",\"kid\":\"${encodedDrmKeyId}\"}],\"type\":\"temporary\"}"
        }


//        val drmKeyIdBytes = drmKeyId.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
//        val encodedDrmKeyId = Base64.encodeToString(
//            drmKeyIdBytes,
//            Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
//        )


        val dashMediaItem = MediaItem.Builder().setUri(path)
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .setMediaMetadata(
                MediaMetadata.Builder().setTitle("Football Streaming")
                    .build()
            )
            .build()

        val videoTrackSelectionFactory =
            AdaptiveTrackSelection.Factory()
        val trackSelector = DefaultTrackSelector(
            this,
            videoTrackSelectionFactory
        )
        trackSelector.setParameters(
            trackSelector.buildUponParameters()
                .setPreferredVideoMimeType(MimeTypes.VIDEO_H264)
        )

        ///Extra Code
        val trackSelectionParameters = TrackSelectionParameters.Builder( /* context= */
            this
        ).build()

        ///////////////
        // default off subtitles
        val newTrackSelectionParameters = trackSelectionParameters
            .buildUpon()
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
            .setMaxVideoSizeSd()

//            .setMinVideoBitrate(2000000)
            .build()
        ///Extra

//        val trackSelector = player?.trackSelector as? MappingTrackSelector ?: return
//
//        trackSelector.parameters = trackSelector.buildUponParameters()
//            .setPreferredAudioRoleFlags(C.ROLE_FLAG_MAIN) // Selects only MAIN audio tracks
//            .build()

        /////////
//        val trackSelector = DefaultTrackSelector(this)
        val loadControl = DefaultLoadControl()


        val drmCallback = LocalMediaDrmCallback(drmBody.toByteArray())
        val drmSessionManager =
            DefaultDrmSessionManager.Builder()
                .setPlayClearSamplesWithoutKeys(true)
                .setMultiSession(false).setKeyRequestParameters(HashMap())
                .setUuidAndExoMediaDrmProvider(
                    C.CLEARKEY_UUID,
                    FrameworkMediaDrm.DEFAULT_PROVIDER
                )
                .build(drmCallback)

        val customDrmSessionManager: DrmSessionManager =
            drmSessionManager
        ///////////////

        val defaultHttpDataSourceFactory =
            DefaultHttpDataSource.Factory()
                .setUserAgent(Constants.USER_AGENT).setTransferListener(
                    DefaultBandwidthMeter.Builder(this)
                        .setResetOnNetworkTypeChange(false).build()
                )


        val dashChunkSourceFactory: androidx.media3.exoplayer.dash.DashChunkSource.Factory =
            androidx.media3.exoplayer.dash.DefaultDashChunkSource.Factory(
                defaultHttpDataSourceFactory
            )
        val manifestDataSourceFactory =
            DefaultHttpDataSource.Factory()
                .setUserAgent(Constants.USER_AGENT)


        // Create a custom SSL context with the provided certificate
//        val trustManager = object : X509TrustManager {
//            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
//            }
//
//            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
//            }
//
//            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
//        }
        // Set up the SSL context to trust all certificates
//        val sslContext = SSLContext.getInstance("TLS")
//        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        ///////////////////////////////////////////////////////////////////////////////
        //Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate>? {
                return null
            }

            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {
                //
            }

            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {
                //
            }
        })
        //Install the all-trusting trust manager
//        // Create a DataSource factory using OkHttp
//        val dataSourceFactory = OkHttpDataSourceFactory(client)
//
//        // 5. Create a custom DataSource.Factory
//        val dataSourceFactoryManifest = DataSource.Factory {
//            val defaultHttpDataSource = DefaultHttpDataSource.Factory()
//                .setSslSocketFactory(sslContext.socketFactory)
//                .createDataSource()
//            defaultHttpDataSource
//        }

        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        } catch (e: KeyManagementException) {
            Log.d("Error", "KeyManagementException")
        } catch (e: NoSuchAlgorithmException) {
            Log.d("Error", "KeyManagementException")

        }
        var dataSourceFactory: DataSource.Factory? = null

        //Below lines must be commentted on Live ID
        // xForwardedKey ="13.106.174.0"
        //  xForwardedKey = "49.50.223.255"

        if (xForwardedKey != null) {
            if (xForwardedKey.isNotEmpty()) {
                dataSourceFactory = DefaultDataSource.Factory(
                    this, CustomHttpDataSourceFactoryWithHeader()
                )

            } else {
                dataSourceFactory = DefaultDataSource.Factory(
                    this, CustomHttpDataSourceFactory(Constants.USER_AGENT)
                )
            }
        } else {
            dataSourceFactory = DefaultDataSource.Factory(
                this, CustomHttpDataSourceFactory(Constants.USER_AGENT)
            )
        }


        if (xForwardedKey.isNotEmpty() && Constants.USER_AGENT!="ExoPlayer-Drm"){
            dataSourceFactory = DefaultDataSource.Factory(
                this, CustomHttpDataSourceFactoryWithMulti(Constants.USER_AGENT)
            )
        }
        else{
            Log.d("ElsePart","else")
        }

//        val headersMap = HashMap<String, String>()
//        headersMap.put("User-Agent", Constants.USER_AGENT)
//        headersMap.put("x-forwarded-for", "49.50.223.255")


        val dashMediaSource = dataSourceFactory?.let {
            androidx.media3.exoplayer.dash.DashMediaSource.Factory(it)
                .setDrmSessionManagerProvider { customDrmSessionManager }
                .createMediaSource(dashMediaItem)
        }
        //        DataSource.Factory dataSourceFactory = DemoUtil.getDataSourceFactory(PlayerActivity.this,headersMap);

        ///////////////


        //////////////
//        val mediaSourceFactory = DefaultMediaSourceFactory(this)
//            .setDrmSessionManagerProvider { customDrmSessionManager }
//            .createMediaSource(dashMediaItem)

        val renderersFactory =
            DefaultRenderersFactory(this)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)

        ////////
        val renderersFactory2 =
            DefaultRenderersFactory(this)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                .setEnableDecoderFallback(true)

        playerMedia3 = ExoPlayer.Builder(this)
//            .setTrackSelector(newTrackSelectionParameters)
            .setLoadControl(loadControl)
//            .setMediaSourceFactory(        createMediaSourceFactory(binding?.playerView,defaultHttpDataSourceFactory,dataSourceFactory,)
//            )
            .setSeekForwardIncrementMs(10000L)
            .setSeekBackIncrementMs(10000L)
            .build()



        binding?.playerView?.player = playerMedia3
        binding?.playerView?.keepScreenOn = true

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding?.playerView?.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FIT
            bindingExoPlayback?.fullScreenIcon?.setImageDrawable(context?.let {
                ContextCompat.getDrawable(
                    it, R.drawable.ic_full_screen
                )
            })
            count = 1
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding?.playerView?.resizeMode =
                AspectRatioFrameLayout.RESIZE_MODE_FIT
        }

        when (mLocation) {
            PlaybackLocation.LOCAL -> {
                if (mCastSession != null && mCastSession?.remoteMediaClient != null) {
                    mCastSession?.remoteMediaClient?.stop()
                    mCastContext?.sessionManager?.endCurrentSession(true)
                }
                mPlaybackState = PlaybackState.IDLE

                if (playerMedia3 != null) {
                    playerMedia3?.addListener(this)
                    playerMedia3?.trackSelectionParameters = newTrackSelectionParameters
                    dashMediaSource?.let { playerMedia3?.setMediaSource(it, true) }
                    playerMedia3?.prepare()
                    binding?.playerView?.requestFocus()
                    playerMedia3?.playWhenReady = true
                }

            }

            PlaybackLocation.REMOTE -> {
                mCastSession?.remoteMediaClient?.play()
                mPlaybackState = PlaybackState.PLAYING
            }

            else -> {
            }
        }

        ////////////
//        setOnGestureListeners()
    }

    private var serverSideAdsLoader: ImaServerSideAdInsertionMediaSource.AdsLoader? = null
    private val serverSideAdsLoaderState: ImaServerSideAdInsertionMediaSource.AdsLoader.State? =
        null

    // For ad playback only.
    private var clientSideAdsLoader: AdsLoader? = null

    //    @OptIn(markerClass = UnstableApi::class) // SSAI configuration
    @OptIn(UnstableApi::class)
    private fun createMediaSourceFactory(
        playerView: PlayerView?,
        defaultHttpDataSourceFactory: DataSource.Factory?
    ): MediaSource.Factory {
        val drmSessionManagerProvider =
            DefaultDrmSessionManagerProvider()
        drmSessionManagerProvider.setDrmHttpDataSourceFactory(defaultHttpDataSourceFactory)
        val serverSideAdLoaderBuilder: ImaServerSideAdInsertionMediaSource.AdsLoader.Builder =
            ImaServerSideAdInsertionMediaSource.AdsLoader.Builder(this, playerView!!)
        if (serverSideAdsLoaderState != null) {
            serverSideAdLoaderBuilder.setAdsLoaderState(serverSideAdsLoaderState)
        }
        serverSideAdsLoader = serverSideAdLoaderBuilder.build()
//        val imaServerSideAdInsertionMediaSourceFactory: ImaServerSideAdInsertionMediaSource.Factory =
//            ImaServerSideAdInsertionMediaSource.Factory(
//                serverSideAdsLoader!!,
//                DefaultMediaSourceFactory(this)
//                    .setDataSourceFactory(dataSourceFactory)
//            )
        return DefaultMediaSourceFactory( /* context= */this)
//            .setDataSourceFactory(dataSourceFactory)
            .setDrmSessionManagerProvider(drmSessionManagerProvider)
            .setLocalAdInsertionComponents(
                { adsConfiguration: MediaItem.AdsConfiguration? ->
                    this.getClientSideAdsLoader(
                        adsConfiguration!!
                    )
                }, playerView
            )
//            .setServerSideAdInsertionMediaSourceFactory(imaServerSideAdInsertionMediaSourceFactory)
    }

    private fun getClientSideAdsLoader(adsConfiguration: MediaItem.AdsConfiguration): AdsLoader {
        // The ads loader is reused for multiple playbacks, so that ad playback can resume.
        if (clientSideAdsLoader == null) {
            clientSideAdsLoader = ImaAdsLoader.Builder( /* context= */this).build()
        }
        clientSideAdsLoader?.setPlayer(playerMedia3)
        return clientSideAdsLoader!!
    }


    //    @UnstableApi
    @UnstableApi
    class CustomHttpDataSourceFactoryWithHeader(
    ) : androidx.media3.datasource.HttpDataSource.Factory {
        override fun createDataSource(): androidx.media3.datasource.HttpDataSource {
            val dataSource =
                androidx.media3.datasource.DefaultHttpDataSource.Factory().createDataSource()
            dataSource.setRequestProperty("X-Forwarded-For", xForwardedKey)
            return dataSource
        }

        override fun setDefaultRequestProperties(defaultRequestProperties: MutableMap<String, String>): androidx.media3.datasource.HttpDataSource.Factory {
            TODO("Not yet implemented")
        }
    }

    //    @UnstableApi
    @UnstableApi
    class CustomHttpDataSourceFactory(
        private val userAgent: String,
    ) : androidx.media3.datasource.HttpDataSource.Factory {
        override fun createDataSource(): androidx.media3.datasource.HttpDataSource {
            val dataSource =
                androidx.media3.datasource.DefaultHttpDataSource.Factory().createDataSource()
            dataSource.setRequestProperty("User-Agent", userAgent)
            return dataSource
        }

        override fun setDefaultRequestProperties(defaultRequestProperties: MutableMap<String, String>): androidx.media3.datasource.HttpDataSource.Factory {
            TODO("Not yet implemented")
        }
    }


    private fun updatePlaybackLocation(location: PlaybackLocation) {
        mLocation = location
    }


    private fun onApplicationConnected(castSession: CastSession) {
        mCastSession = castSession

        if (mPlaybackState == PlaybackState.IDLE) {
            if (mPlaybackState != PlaybackState.PLAYING) {
                loadRemoteMedia()
                mPlaybackState = PlaybackState.PLAYING
            }
            return
        }

        invalidateOptionsMenu()
    }

    private fun onApplicationDisconnected() {
        updatePlaybackLocation(PlaybackLocation.LOCAL)

        mPlaybackState = PlaybackState.IDLE
        mLocation = PlaybackLocation.LOCAL
        invalidateOptionsMenu()
    }

    //    @OptIn(UnstableApi::class)
    @OptIn(UnstableApi::class)
    private fun setOnGestureListeners() {

        binding?.playerView?.setOnClickListener {
            if (viewCount == 0) {
                binding?.playerView?.hideController()
                if (mediaRouteMenuItem != null) mediaRouteMenuItem!!.isVisible = false

                viewCount++
            } else {
                binding?.playerView?.showController()
                if (mediaRouteMenuItem != null) mediaRouteMenuItem!!.isVisible = true
                viewCount = 0

            }

        }

    }

    override fun onAdLoad(value: String) {
        adStatus = value.equals("success", true)

    }

    override fun onAdFinish() {
        isAdShowning = false

        if (binding?.lottiePlayer?.isVisible == true) {
            binding?.lottiePlayer?.visibility = View.GONE
        }
        Constants.videoFinish = true
        finish()

    }

    override fun onSessionEnded(p0: CastSession, p1: Int) {
        onApplicationDisconnected()
    }

    override fun onSessionEnding(p0: CastSession) {
        onApplicationDisconnected()
    }

    override fun onSessionResumeFailed(p0: CastSession, p1: Int) {
        onApplicationDisconnected()
    }

    override fun onSessionResumed(p0: CastSession, p1: Boolean) {
//                onApplicationConnected(p0)

    }

    override fun onSessionResuming(p0: CastSession, p1: String) {
    }

    override fun onSessionStartFailed(p0: CastSession, p1: Int) {
        onApplicationDisconnected()

    }

    override fun onSessionStarted(p0: CastSession, p1: String) {
        onApplicationConnected(p0)
    }

    override fun onSessionStarting(p0: CastSession) {

    }

    override fun onSessionSuspended(p0: CastSession, p1: Int) {


    }


    object VideoPlayerConfig {
        //Minimum Video you want to buffer while Playing
        const val MIN_BUFFER_DURATION = 7000

        //Max Video you want to buffer during PlayBack
        const val MAX_BUFFER_DURATION = 15000

        //Min Video you want to buffer before start Playing it
        const val MIN_PLAYBACK_START_BUFFER = 7000

        //Min video You want to buffer when user resumes video
        const val MIN_PLAYBACK_RESUME_BUFFER = 7000
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        try {

        } catch (e: Exception) {
            Log.d("Exception", "msg")
        }
    }


}