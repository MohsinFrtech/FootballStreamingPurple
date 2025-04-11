package com.tsz.live.football.tv.streaming.hd.utils.objects


import android.util.Log
import androidx.navigation.NavDirections
import com.cleversolutions.ads.MediationManager
import com.tsz.live.football.tv.streaming.hd.BuildConfig
import com.google.android.gms.ads.nativead.NativeAd
import com.tsz.live.football.tv.streaming.hd.models.FormatDataAudioMedia3
import com.tsz.live.football.tv.streaming.hd.models.FormatDataMedia3
import com.tsz.live.football.tv.streaming.hd.models.NewList
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.OnHomePressedListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Constants {
    const val consentKey="Consent"
    var nativeFieldVal = ""
    const val mySecretCheckDel: String = "&"
    var startAppId = ""
    var casAiId = "demo"
    const val dash = "dash"
    var clearKeyKey=""
    var clearKeyId =""
    var USER_AGENT = "ExoPlayer-Drm"
    const val hlsSource = "hls"
    var xForwardedKey =""
    var googleAdMangerInterstitial = ""
    var isStartAppSdkInit = false
    var googleAdMangerBanner = ""
    var googleAdMangerNative = ""
    var videoFinish = false
    var isCasAiInit = false
    var casAiAdManager: MediationManager? = null
    var currentCountryCode = ""



    //Ads
    const val unityTestMode = false
    const val admob = "admob"
    const val facebook = "facebook"
    const val chartBoost = "chartboost"
    const val unity = "unity"
    const val startApp = "startapp"
    const val adManagerAds = "admanager"
    const val cas_Ai = "casAi"

    var admobInterstitial = ""
    //const val admobInterstitial = "ca-app-pub-3940256099942544/1033173712" //test Ad
    var fbPlacementIdInterstitial = ""
    var fbPlacementIdBanner = ""
    var chartBoostAppID = ""
    var chartBoostAppSig = "f"
    var nativeAdmob: String = ""
    var unityGameID = ""
    var nativeFacebook = ""
    var admobBannerId = ""
    var facebookPlacementIdInterstitial = ""


    ///Ad Location
    const val adMiddle = "Middle"
    const val adBefore = "BeforeVideo"
    const val adAfter = "AfterVideo"
    const val adTap = "tap"

    const val adLocation1 = "Location1"
    const val adLocation2top = "Location2Top"
    const val adLocation2topPermanent = "Location2TopPermanent"
    const val adLocation2bottom = "Location2Bottom"
    const val nativeAdLocation="native"
    var location2TopProvider = "none"
    var location2BottomProvider = "none"
    var location2TopPermanentProvider = "none"
    var nativeAdProvider = "none"
    var middleAdProvider = "none"
    var locationBeforeProvider = "none"
    var location1Provider = "none"
    var playerActivityInPip=false

    var locationAfter = "none"
    var isInitAdmobSdk = false
    var isInitFacebookSdk = false
    var isUnitySdkInit = false
    var isChartboostSdkInit = false
    const val adsSeparator: String = "&"



    //API Data
    const val stringId = 1
    var authToken = "BuildConfig.MAPS_AUTH"
    const val appVersionName = BuildConfig.VERSION_NAME
    const val appVersionCode = BuildConfig.VERSION_CODE
    var baseUrlChannel = "BuildConfig.MAPS_BASE"
    var baseUrlDemo = "https://nodeapi"
    const val baseIp = "https://api.ipify.org/"
    var userIp = "userIp"
    const val userApi = "get_url"
    const val channelApi = "details"
    var valuenine = ""
    var valueper = ""
    var channel_url_val = ""
    var encryptKey = ""
    var mListener: OnHomePressedListener? = null

     var navDirections: NavDirections? = null

    //splash
    var updateScreenStatus = false
    var app_update_dialog = false
    const val preferenceKey="message"
    const val rateUsKey = "rateus"

    //userMethod2 encryptKeyFun Defemation Class
    const val chName="UTF-8"
    const val asp="AES"
    const val instanceVal="PBKDF2WithHmacSHA1"
    const val transForm="AES/CBC/PKCS5Padding"


    //playLand
    const val splitnine = "999"
    const val splitper = "%"
    const val userRepAlgo = "[cCITS]"
    var emptyCheck = "emptyCheck"

    const val  key = ""
    const val  salt = ""
    var currentNativeAd: NativeAd?=null
    var currentNativeAdFacebook:com.facebook.ads.NativeAd?=null

    const val checkSize = 16

    var rateUsText = ""
    var rateUsDialogValue: Boolean = false
    var rateShown=false

    var positionClick=-1
    var previousClick=-1

    var appLiveStatus = false

    const val preferenceNoteLay="Notes"

    var positionClick2 = 0
    var previousClick2 = -1

    //userData
    const val userType1 = "flussonic"
    const val userType2 = "cdn"
    const val userType3 = "p24"
    const val userType4 = "app"
    const val userTypePhp = "php"

    const val sepUrl = ".net"
    const val phraseDel = "@"
    var userLink = ""
    var defaultString = ""

    //DefamationClass
    //userMethod1
    var myUserLock1 = "locked"
    var myUserCheck1 = "myUserCheck1"
    const val myUserCheckDel: String = "#"
    const val myUserCheckSize = 16
    const val userBase = "?token="
    const val userBaseDel = "/"
    const val algoTypeS1 = "SHA-1"
    const val algoTypeS2 = "SHA-256"
    const val algoName = "iso-8859-1"
    var filterValue = ""

    // Football data
    var footballBaseApi = ""
    var footballToken = ""
    var s_token = ""
    var port = ""

    fun checkNativeAd(list: List<NewList>): List<NewList?> {
        val tempchannels: MutableList<NewList?> =
            java.util.ArrayList<NewList?>()
        for (i in list.indices) {
            // if (list[i].live!!) {
            val diff = i % 5
            if (diff == 2) {

                tempchannels.add(null)
            }
            tempchannels.add(list[i])
            if (list.size == 2) {
                if (i == 1) {
                    tempchannels.add(null)

                }
            }
            // }
        }
        return tempchannels
    }
    var timeValueAtPlayer = 15

    var positionClick4 = 0
    var previousClick4 = -1
    fun Calendar.formatDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dateStr = sdf.format(this.time)
        return dateStr
    }
    fun calculateDifferenceBetweenDates(channelDate: Long): Long {

        val currentDate = Calendar.getInstance()
        var different: Long = currentDate.timeInMillis - channelDate
        if (different < 0) {
            try {
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val daysInMilli = hoursInMilli * 24

                val elapsedDays = different / daysInMilli
                different = different % daysInMilli

                val elapsedHours = different / hoursInMilli
                different = different % hoursInMilli

                val elapsedMinutes = different / minutesInMilli
                different = different % minutesInMilli

                val elapsedSeconds = different / secondsInMilli
                println("Difference: $elapsedDays days, $elapsedHours hours, $elapsedMinutes minutes, $elapsedSeconds seconds")

                val totalTime =
                    (elapsedHours * 60 * 60 * 1000)+(elapsedMinutes * 60 * 1000)+(elapsedSeconds * 1000)

                val hours = totalTime / (60 * 60 * 1000)
                val minutes = (totalTime % (60 * 60 * 1000)) / (60 * 1000)
                val seconds = (totalTime % (60 * 1000)) / 1000
                Log.d("Difference", "msg $totalTime $hours $minutes $seconds")

                return Math.abs(totalTime)
            } catch (e: Exception) {
                return 0
            }

        } else {
            return 0
        }

    }

    val dataFormatsAudioMedia3: MutableList<FormatDataAudioMedia3> =
        java.util.ArrayList<FormatDataAudioMedia3>()

    val dataFormatsMedia3: MutableList<FormatDataMedia3> =
        java.util.ArrayList<FormatDataMedia3>()
}