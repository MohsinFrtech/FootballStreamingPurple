package com.tsz.live.football.tv.streaming.hd.ui.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.getkeepsafe.relinker.ReLinker
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.tsz.live.football.tv.streaming.hd.R
import com.tsz.live.football.tv.streaming.hd.adsdata.GoogleMobileAdsConsentManager
import com.tsz.live.football.tv.streaming.hd.adsdata.NewAdManager
import com.tsz.live.football.tv.streaming.hd.databinding.ActivitySplashBinding
import com.tsz.live.football.tv.streaming.hd.utils.AppContextProvider
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.app_update_dialog
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.authToken
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.baseUrlChannel
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.currentCountryCode
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.emptyCheck
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.filterValue
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.footballBaseApi
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.footballToken
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.locationAfter
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.locationBeforeProvider
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.middleAdProvider
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.myUserLock1
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.nativeFieldVal
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.port
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.preferenceNoteLay
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.rateUsDialogValue
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.s_token
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.userRepAlgo
import com.tsz.live.football.tv.streaming.hd.utils.objects.CustomDialogue
import com.tsz.live.football.tv.streaming.hd.utils.objects.DebugChecker
import com.tsz.live.football.tv.streaming.hd.utils.objects.Defemation
import com.tsz.live.football.tv.streaming.hd.utils.objects.InternetUtils.isInternetOn
import com.tsz.live.football.tv.streaming.hd.utils.objects.InternetUtils.isPrivateDnsSetup
import com.tsz.live.football.tv.streaming.hd.utils.objects.SharedPreference
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.ApiResponseListeners
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.DialogListener
import com.tsz.live.football.tv.streaming.hd.viewModel.DataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class SplashActivity : AppCompatActivity(), DialogListener,ApiResponseListeners {

    private var bindingSplash: ActivitySplashBinding? = null
    private var permissionCount = 0
    private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager? = null
    var fullSize = 0
    private var char = "mint"
    private var preference: SharedPreference? = null
    private val viewModel by lazy {
        ViewModelProvider(this)[DataViewModel::class.java]
    }

    private var firebaseAnalytics: FirebaseAnalytics? = null

    private external fun getStringArray1(): Array<String?>?
    private external fun getStringArray2(): Array<String?>?
    private external fun getStringArray3(): Array<String?>?
    private external fun getStringArray4(): Array<String?>?
    private external fun getStringArray5(): Array<String?>?
    private external fun getStringArray6(): Array<String?>?
    private external fun getStringArray7(): Array<String?>?
    private external fun getStringArray8(): Array<String?>?
    private external fun getStringArray9(): Array<String?>?
    private external fun getStringArray10(): Array<String?>?
    private external fun getStringArray11(): Array<String?>?
    private external fun getStringArray12(): Array<String?>?
    private external fun getStringArray13(): Array<String?>?
    private external fun getStringArray14(): Array<String?>?
    private external fun getStringArray15(): Array<String?>?
    private external fun getStringArray16(): Array<String?>?
    private external fun getStringArray17(): Array<String?>?
    private external fun getStringArray18(): Array<String?>?
    private external fun getStringArray19(): Array<String?>?
    private external fun getStringArray20(): Array<String?>?
    private external fun getStringArray21(): Array<String?>?
    private external fun getStringArray22(): Array<String?>?
    private external fun getStringArray23(): Array<String?>?
    private external fun getStringArray24(): Array<String?>?
    private external fun getStringArray25(): Array<String?>?
    private external fun getStringArray26(): Array<String?>?
    private external fun getStringArray27(): Array<String?>?
    private external fun getStringArray28(): Array<String?>?
    private external fun getStringArray29(): Array<String?>?
    private external fun getStringArray30(): Array<String?>?
    private external fun getStringArray31(): Array<String?>?
    private external fun getStringArray32(): Array<String?>?
    private external fun getStringArray33(): Array<String?>?
    private external fun getStringArray34(): Array<String?>?
    private external fun getStringArray35(): Array<String?>?
    private external fun getStringArray36(): Array<String?>?
    private external fun getStringArray37(): Array<String?>?
    private external fun getStringArray38(): Array<String?>?
    private external fun getStringArray39(): Array<String?>?
    private external fun getStringArray40(): Array<String?>?

    private var isFromNotification = false

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                isFromNotification = true
                //  ifPermissionGrantedThenNotResume=true
                bindingSplash?.notificationLayout?.visibility = View.GONE
                // Permission is granted. Continue the action or workflow in your
                subscribeOrUnSubscribeTopic()
            } else {
//                ifPermissionGrantedThenNotResume=true
//                bindingHome?.notificationLayout?.visibility = View.VISIBLE
                isFromNotification = true
                permissionCount++
                if (permissionCount==1){
                    makePermission()
                    permissionCount++
                    preference?.saveNotificationPermission(preferenceNoteLay, true)
                }
                else{
                    bindingSplash?.notificationLayout?.visibility = View.VISIBLE
                }
            }
        }

    private fun subscribeOrUnSubscribeTopic() {
        val getStatus = preference?.getBool(Constants.preferenceKey)
        if (getStatus == true) {
            navigationToNextScreen()
            ///means already subscribe to topic...
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic("event")
                .addOnCompleteListener { task ->
                    if (task.isComplete) {
                        //
                        preference?.saveBool(Constants.preferenceKey, true)
                    }
                }

            navigationToNextScreen()
        }
    }

    private fun navigationToNextScreen() {
        if (isInternetOn(this)) {
            bindingSplash?.lottieAnimLayout?.visibility = View.GONE
            bindingSplash?.noInternetText?.visibility = View.GONE
            bindingSplash?.retry?.visibility = View.GONE
            moveToMainScreen()
        } else {
            bindingSplash?.lottieAnimLayout?.visibility = View.VISIBLE
            bindingSplash?.noInternetText?.visibility = View.VISIBLE
            bindingSplash?.retry?.visibility = View.VISIBLE
            bindingSplash?.homeAnimLayout?.visibility = View.GONE
        }
    }

    private fun moveToMainScreen() {
        bindingSplash?.homeAnimLayout?.visibility = View.GONE
        if (isDeviceRooted()) {
            CustomDialogue(this).showDialog(
                this, "Alert!", "Please use application on real device",
                "", "Ok", "baseValue"
            )
        } else {
            apiCall()
        }
    }

    private fun apiCall() {
        ReLinker.loadLibrary(this,"cppproject", object : ReLinker.LoadListener {
            override fun success() {
                lifecycleScope.launch(Dispatchers.Main) {
                    val cppFile = getProjectConcat(1)
                    authToken = cppFile?.get(10).toString()
                    baseUrlChannel = cppFile?.get(11).toString()
                    emptyCheck = cppFile?.get(13).toString()
                    s_token = cppFile?.get(15).toString()
                    port = cppFile?.get(16).toString()
                    footballBaseApi = cppFile?.get(17).toString()
                    //API Parameter
                    footballToken = cppFile?.get(18).toString()
                    viewModel?.getIPdata()
                    setUpModelData()
//                    getIndexValue("mint")

                }
            }

            override fun failure(t: Throwable?) {
                runOnUiThread {
                    showFailedCppDialog()
                }
            }

        })

    }

    private fun showFailedCppDialog() {
        CustomDialogue(this).showDialog(
            this, "title", "Something went wrong, Please Restart your phone",
            "", "Exit", "eventValue"
        )
    }


    private fun getIndexValue(fitX: String?) {
        try {

            var ml1 = ""
            var xLimit = 40
            var addValue =0

            var sendValue = ""
            if (char.equals("mint", true)) {
                sendValue = emptyCheck
            } else {
                sendValue = fitX!!
            }

//            getApiBaseUrl(sendValue)

            val (array1, array2, array3) = splitArrayfunction(sendValue.trim())
            val sizeMain = fullSize
            for (x in array3.indices) {
                addValue = addValue+2

                var final= array2[x].toInt()
                if (final>0)
                {
                    ///
                }
                else
                {
                    final=10
                }

                ///////////
                val numberFile = getProjectConcat(final)
                if (array3[x].toInt() in 0..9) {
                    val indexValue= numberFile?.get(array3[x].toInt())
                    var finalX = addValue + array1[x].toInt()

                    if (finalX in 0..39)
                    {

                        val finalVal=indexValue?.toCharArray()?.get(finalX)
                        xLimit =final
                        ml1 += StringBuilder().append(finalVal).toString()
                    }
                }


            }

            if (char.equals("mint", true)) {
                Constants.encryptKey = ml1
//                getValuesData()

            } else {
                val getFileNumberAt2nd = getProjectConcat(sizeMain)
                templateFile(ml1, sizeMain, getFileNumberAt2nd)
            }

        } catch (e: Exception) {
           Log.d("Exception","msg")
        }
    }
    private fun getProjectConcat(x: Int): Array<String?>? {
        return when (x) {
            1 -> {
                getStringArray1()
            }
            2 -> {
                getStringArray2()
            }
            3 -> {
                getStringArray3()
            }
            4 -> {
                getStringArray4()
            }
            5 -> {
                getStringArray5()
            }
            6 -> {
                getStringArray6()
            }
            7 -> {
                getStringArray7()
            }
            8 -> {
                getStringArray8()
            }
            9 -> {
                getStringArray9()
            }
            10 -> {
                getStringArray10()
            }
            11 -> {
                getStringArray11()
            }
            12 -> {
                getStringArray12()
            }
            13 -> {
                getStringArray13()
            }
            14 -> {
                getStringArray14()
            }
            15 -> {
                getStringArray15()
            }
            16 -> {
                getStringArray16()
            }
            17 -> {
                getStringArray17()
            }
            18 -> {
                getStringArray18()
            }
            19 -> {
                getStringArray19()
            }
            20 -> {
                getStringArray20()
            }
            21 -> {
                getStringArray21()
            }
            22 -> {
                getStringArray22()
            }
            23 -> {
                getStringArray23()
            }
            24 -> {
                getStringArray24()
            }
            25 -> {
                getStringArray25()
            }
            26 -> {
                getStringArray26()
            }
            27 -> {
                getStringArray27()
            }
            28 -> {
                getStringArray28()
            }
            29 -> {
                getStringArray29()
            }
            30 -> {
                getStringArray30()
            }
            31 -> {
                getStringArray31()
            }
            32 -> {
                getStringArray32()
            }
            33 -> {
                getStringArray33()
            }
            34 -> {
                getStringArray34()
            }
            35 -> {
                getStringArray35()
            }
            36 -> {
                getStringArray36()
            }
            37 -> {
                getStringArray37()
            }
            38 -> {
                getStringArray38()
            }
            39 -> {
                getStringArray39()
            }
            40 -> {
                getStringArray40()
            }
            else -> {
                return null
            }
        }
    }

    private fun getValuesData() {
//        setUpModelData()

        viewModel.dataModelListGet.observe(this) {

            nativeFieldVal = it.extra_3!!
            if (it.live!= null){
                Constants.appLiveStatus = it.live!!
            }

            if (!it.extra_2.isNullOrEmpty()) {
                char = "goi"
                getIndexValue(it.extra_2)
            }

            if (!it.app_ads.isNullOrEmpty()) {

                val context = AppContextProvider.getContext()

                val nativeAdProviderName =
                    NewAdManager?.checkProvider(it.app_ads!!, Constants.nativeAdLocation)
                        .toString()
                Constants.nativeAdProvider = nativeAdProviderName

                middleAdProvider = NewAdManager.checkProvider(it.app_ads!!, Constants.adMiddle)
                Constants.location1Provider =
                    NewAdManager.checkProvider(it.app_ads!!, Constants.adLocation1)
                locationBeforeProvider =
                    NewAdManager.checkProvider(it.app_ads!!, Constants.adBefore)
                locationAfter = NewAdManager.checkProvider(it.app_ads!!, Constants.adAfter)

                if (!middleAdProvider.equals("none", true)) {
                    if (context != null) {
                        if (!middleAdProvider.equals(Constants.startApp, true)) {
                            NewAdManager.loadAdProvider(
                                middleAdProvider, Constants.adMiddle,
                                null, null, null, null,
                                context, this
                            )
                        }
                    }
                }


                if (!locationBeforeProvider.equals("none", true)) {
                    if (!middleAdProvider.equals("none", true)) {
                        if (!middleAdProvider.equals(locationBeforeProvider, true)) {
                            if (context != null) {
                                if (!locationBeforeProvider.equals(Constants.startApp, true)) {
                                    NewAdManager.loadAdProvider(
                                        locationBeforeProvider, Constants.adBefore,
                                        null, null, null, null,
                                        context, this
                                    )
                                }
                            }
                        }
                    } else {
                        if (context != null) {
                            if (!locationBeforeProvider.equals(Constants.startApp, true)) {
                                NewAdManager.loadAdProvider(
                                    locationBeforeProvider, Constants.adBefore,
                                    null, null, null, null,
                                    context, this
                                )
                            }
                        }
                    }
                }

                if (!locationAfter.equals("none", true)) {
                    if (!middleAdProvider.equals("none", true)) {
                        if (!middleAdProvider.equals(locationAfter)) {
                            if (!locationBeforeProvider.equals("none", true)) {
                                if (!locationBeforeProvider.equals(locationAfter)) {
                                    if (context != null) {
                                        if (!locationAfter.equals(Constants.startApp, true)) {
                                            NewAdManager.loadAdProvider(
                                                locationAfter, Constants.adAfter,
                                                null, null, null, null,
                                                context, this
                                            )
                                        }
                                    }
                                }

                            } else {
                                if (context != null) {
                                    if (!locationAfter.equals(Constants.startApp, true)) {

                                        NewAdManager.loadAdProvider(
                                            locationAfter, Constants.adAfter,
                                            null, null, null, null,
                                            context, this
                                        )
                                    }

                                }
                            }
                        }
                    } else {
                        if (!locationBeforeProvider.equals("none", true)) {
                            if (!locationBeforeProvider.equals(locationAfter)) {
                                if (context != null) {
                                    if (!locationAfter.equals(Constants.startApp, true)) {
                                        NewAdManager.loadAdProvider(
                                            locationAfter, Constants.adAfter,
                                            null, null, null, null,
                                            context, this
                                        )
                                    }
                                }
                            }
                        } else {
                            if (context != null) {
                                if (!locationAfter.equals(Constants.startApp, true)) {

                                    NewAdManager.loadAdProvider(
                                        locationAfter, Constants.adAfter,
                                        null, null, null, null,
                                        context, this
                                    )
                                }

                            }
                        }
                    }

                }
            }

            val modelValue = it
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("data", modelValue)
            startActivity(intent)
            finish()

        }
    }

    private fun setUpModelData() {
        authToken = authToken

        baseUrlChannel = baseUrlChannel

        viewModel.getCMSData()
        viewModel.isLoading.observe(this) {
            if (it) {
                bindingSplash?.homeAnimLayout?.visibility = View.VISIBLE

            } else {
                bindingSplash?.homeAnimLayout?.visibility = View.GONE
            }
        }

        viewModel.stringValue.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                try {
                    var decryptVal = ""
                    val seperationBasedOnLetter = it?.split("_____")

                    if (seperationBasedOnLetter != null) {
                        if (seperationBasedOnLetter?.size!! > 0) {
                            decryptVal =
                                Defemation.convertDecData(seperationBasedOnLetter[seperationBasedOnLetter.size - 1])
                        }
                    }
                    getIndexValueEn(decryptVal,seperationBasedOnLetter!![0])
                } catch (e: java.lang.Exception) {
                    viewModel.setUpError("Something is wrong with response,please retry.")
                    Log.d("Exception","msg")
                }
            }
        })

    }

    fun getIndexValueEn(fitX: String, org: String) {
        try {
//            val screenUtil = ScreenUtil()

            var ml1 = ""
            var xLimit = 40
            var sendValue = fitX
            var addValue =0

            val (array1, array2, array3) = splitArrayfunction(sendValue)
//            val sizeMain = screenUtil.returnValueOfSize()

            for (a1 in array3.indices) {

                val fileNumber = array1[a1]
                val stringToPick = array1[a1]
                addValue += 3

                if (fileNumber.toInt() in 1..39){

                    if (stringToPick.toInt() in 0..9) {

                        if (array3[a1].toInt() in 0..9) {
                            var finalX = addValue + array3[a1].toInt()
                            val numberFile = getProjectConcat(fileNumber.toInt())
                            val indexValueWithInFile = numberFile?.get(stringToPick.toInt())
                            if (finalX in 0..39) {
//                                Log.d(
//                                    "ValueCheckerrr", "check" + array3[a1] + " " + addValue + " "
//                                            + finalX
//                                )
                                val finalVal = indexValueWithInFile?.toCharArray()?.get(finalX)
                                ml1 += StringBuilder().append(finalVal).toString()
                            }

                        }

                    }

                }
            }

            filterValue = ml1
            viewModel.parseDataAndNotify(org,ml1)
            getValuesData()

        } catch (e: java.lang.Exception) {

            Log.d("tAG", "message" + e.message)
        }
    }





    fun splitArrayfunction (valueParams: String): Triple<Array<String>, Array<String>, Array<String>> {
        val myValue = valueParams
        val mainArray: Array<String> = myValue.toCharArray().map { it.toString() }.toTypedArray()
        val mainArraySize = mainArray.size
        fullSize = mainArraySize
        val array1 = mainArray.copyOfRange(0, (mainArraySize + 1) / 3)
        val arr2 = mainArray.copyOfRange((mainArraySize + 1) / 3, mainArraySize)
        val array2 = arr2.copyOfRange(0, (arr2.size + 1) / 2)
        val array3 = arr2.copyOfRange((arr2.size + 1) / 2, arr2.size)
        return Triple(array1, array2, array3)
    }


    fun templateFile(strCon: String, sizeVal: Int, mainIndex: Array<String?>?) {
        val string2Pick = (sizeVal / 4)
        val char2Pick = (sizeVal * 0.7).toInt()

        if (string2Pick in 0..9) {
            val char1ToReplace = mainIndex?.get(string2Pick)?.toCharArray()?.get(char2Pick)
            val rep = userRepAlgo.toRegex()
            myUserLock1 = rep.replace(strCon, char1ToReplace.toString())

        }

    }


    private fun isDeviceRooted(): Boolean {
        return checkForSuFile() || checkForSuCommand() ||
                checkForSuperuserApk() || checkForBusyBoxBinary() || checkForMagiskManager()
    }

    private fun checkForSuCommand(): Boolean {
        return try {
            // check if the device is rooted
            val file = File("/system/app/Superuser.apk")
            if (file.exists()) {
                return true
            }
            val command: Array<String> = arrayOf("/system/xbin/which", "su")
            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            if (reader.readLine() != null) {
                return true
            }
            return false
        } catch (e: Exception) {
            false
        }
    }

    private fun checkForSuFile(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in paths) {
            if (File(path).exists()) {
                return true
            }
        }
        return false
    }

    private fun checkForSuperuserApk(): Boolean {
        val packageName = "eu.chainfire.supersu"
        val packageManager = packageManager
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
                true
            } else {
                packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
                true
            }

        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun checkForMagiskManager(): Boolean {
        val packageName = "com.topjohnwu.magisk"
        val packageManager = packageManager
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
                true
            } else {
                packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
                true
            }

        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun checkForBusyBoxBinary(): Boolean {
        val paths = arrayOf("/system/bin/busybox", "/system/xbin/busybox", "/sbin/busybox")
        try {
            for (path in paths) {
                val process = Runtime.getRuntime().exec(arrayOf("which", path))
                if (process.waitFor() == 0) {
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        preference = SharedPreference(this)
        super.onCreate(savedInstanceState)
        bindingSplash = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        //Initialize firebase instance...
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        viewModel?.apiResponseListeners =  this
        FirebaseApp.initializeApp(this)
        initializeFirebaseAnalytics()
        rateUsDialogValue = false
        app_update_dialog = false
        Constants.updateScreenStatus =false
        bindingSplash?.retry?.setOnClickListener {
            bindingSplash?.homeAnimLayout?.visibility = View.VISIBLE
            if (isInternetOn(this)) {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!DebugChecker.checkDebugging(this)) {
                        checkEitherEmulatorOrNot()
                    }
                }, 2000)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    bindingSplash?.homeAnimLayout?.visibility = View.GONE
                }, 2000)
            }
        }

        currentCountryCode = getNetworkCountryCode(this).toString()


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bindingSplash?.notificationLayout?.isVisible == true || bindingSplash?.noInternetText?.isVisible == true) {
                    finishAffinity()
                } else {

                }
            }
        })

        bindingSplash?.yesBtn?.setOnClickListener {
            bindingSplash?.notificationLayout?.visibility = View.GONE
            //  makePermission()

            apiCall()
        }
        bindingSplash?.skipBtn?.setOnClickListener {
            isFromNotification = false
            bindingSplash?.notificationLayout?.visibility = View.GONE
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts(
                    "package",
                    packageName, null
                )
                intent.data = uri
                startActivity(intent)
            }
            catch (e:java.lang.Exception){
                Log.d("Exception","msg")
            }
        }
    }

    fun getNetworkCountryCode(context: Context): String? {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.networkCountryIso // Returns ISO country code (e.g., "us", "in")
    }

    override fun onResume() {
        super.onResume()
        Constants.updateScreenStatus = false
        Constants.rateShown = false
        Constants.app_update_dialog = false
        if (!isFromNotification){
            showConsentDialog()
        }
    }

    private fun initializeFirebaseAnalytics() {
        try {
            firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        } catch (e: java.lang.Exception) {
            Log.d("Exception", "msg")
        }
    }

    private fun showConsentDialog() {
        val getConsentStatus = preference?.getConsentStatus(Constants.consentKey)
        if (getConsentStatus == true) {
            checkNecessities()
        } else {
            googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(this)
            googleMobileAdsConsentManager?.gatherConsent(this) { consentError ->
                if (consentError != null) {
                    preference?.saveConsent(Constants.consentKey, true)
                    checkNecessities()
                    Log.d("UmpDataCollected", "ump" + consentError.message)
                }else {
                    checkNecessities()
                }

                if (googleMobileAdsConsentManager?.canRequestAds == true) {
                    preference?.saveConsent(Constants.consentKey, true)
                    Log.d("UmpDataCollected", "yes")
//                    checkNecessities()
//                initializeMobileAdsSdk()
                } else {
                    //
                    checkNecessities()
                    Log.d("UmpDataCollected", "yes")
                }
//            if (googleMobileAdsConsentManager?.isPrivacyOptionsRequired == true) {
//                // Regenerate the options menu to include a privacy setting.
//                invalidateOptionsMenu()
//            }
            }

            // This sample attempts to load ads using consent obtained in the previous session.
            if (googleMobileAdsConsentManager != null) {
                if (googleMobileAdsConsentManager!!.canRequestAds) {
//                checkNecessities()
                }
            }

        }

    }

    private fun checkNecessities() {
        if (!DebugChecker.checkDebugging(this)) {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    if (isPrivateDnsSetup(this)) {
                        Toast.makeText(
                            this,
                            "Please turn off private dns,If not found then search dns in setting search",
                            Toast.LENGTH_LONG
                        ).show()
                        try {
                            startActivityForResult(Intent(Settings.ACTION_SETTINGS), 0)
                        } catch (e: java.lang.Exception) {
                            Log.d("Exception", "msg")
                        }
                    } else {
                        checkEitherEmulatorOrNot()
                    }
                },
                2000
            )
        }

    }

    private fun checkEitherEmulatorOrNot() {
        try {
            val isProbablyRunningOnEmulator: Boolean by lazy {
                // Android SDK emulator
                return@lazy ((Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                        && Build.FINGERPRINT.endsWith(":user/release-keys")
                        && Build.MANUFACTURER == "Google" && Build.PRODUCT.startsWith("sdk_gphone_") && Build.BRAND == "google"
                        && Build.MODEL.startsWith("sdk_gphone_"))
                        //
                        || Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")
                        //bluestacks
                        || "QC_Reference_Phone" == Build.BOARD && !"Xiaomi".equals(
                    Build.MANUFACTURER,
                    ignoreCase = true
                ) //bluestacks
                        || Build.MANUFACTURER.contains("Genymotion")
                        || Build.HOST.startsWith("Build") //MSI App Player
                        || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                        || Build.PRODUCT == "google_sdk"
                        || Build.FINGERPRINT.contains("generic")
                        // another Android SDK emulator check
                        )
            }

            if (isProbablyRunningOnEmulator) {

                CustomDialogue(this).showDialog(
                    this, "Alert!", "Please use application on real device",
                    "", "Ok", "baseValue"
                )
            } else {
                val checkNoticationLayShow = preference?.getNotificationPermission(preferenceNoteLay)
                if (checkNoticationLayShow == true) {
                    navigationToNextScreen()
                }
                else{
                    checkNotificationPermission()
                }
            }

        } catch (e: Exception) {
            Log.d("Exception", "" + e.message)
        }
    }


    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    subscribeOrUnSubscribeTopic()

                }

//                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
//                -> {
//                    bindingHome?.notificationLayout?.visibility = View.VISIBLE
//
//                }

                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    makePermission()
                }
            }
        } else {
            subscribeOrUnSubscribeTopic()
        }
    }

    private fun makePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

//            if (permissionCount > 3) {
//                bindingHome?.notificationLayout?.visibility = View.GONE
//                navigationToNextScreen()
//
//            } else if (permissionCount == 2) {
//                bindingHome?.notificationLayout?.visibility = View.GONE
//
//                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                val uri = Uri.fromParts(
//                    "package",
//                    packageName, null
//                )
//                intent.data = uri
//                startActivity(intent)
//            } else {
//                requestPermissionLauncher.launch(
//                    Manifest.permission.POST_NOTIFICATIONS
//                )
//            }
//
//            permissionCount++

            requestPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    override fun onPositiveDialogText(key: String) {
       viewModel?.getCMSData()
    }

    override fun onDestroy() {
        super.onDestroy()
        requestPermissionLauncher.unregister()
    }

    override fun onNegativeDialogText(key: String) {
        when (key) {
            "baseValue" -> finishAffinity()
            "isInternet" -> finishAffinity()
            "eventValue" -> startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
        }
    }

    override fun onStarted() {

    }

    override fun onSuccess() {

    }

    override fun onFailure(message: String) {
        if (!isFinishing) {
            try {
                CustomDialogue(this).showDialog(
                    this, "Alert", message,
                    "Retry", "Exit", "isInternet"
                )
            }
            catch (e: WindowManager.BadTokenException){
                Log.d("Exception","msg")
            }

        }
    }
}