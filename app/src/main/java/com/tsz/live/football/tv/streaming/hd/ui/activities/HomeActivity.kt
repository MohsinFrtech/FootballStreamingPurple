package com.tsz.live.football.tv.streaming.hd.ui.activities

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.view.setMargins
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tsz.live.football.tv.streaming.hd.BuildConfig
import com.tsz.live.football.tv.streaming.hd.R
import com.tsz.live.football.tv.streaming.hd.adsdata.AdManager
import com.tsz.live.football.tv.streaming.hd.databinding.ActivityHomeBinding
import com.tsz.live.football.tv.streaming.hd.models.ApplicationConfiguration
import com.tsz.live.football.tv.streaming.hd.models.DataModel
import com.tsz.live.football.tv.streaming.hd.utils.AdsListener
import com.tsz.live.football.tv.streaming.hd.utils.Logger
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.app_update_dialog
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.baseUrlDemo
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.filterValue
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.nativeFieldVal
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.rateShown
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.rateUsDialogValue
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.rateUsKey
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.rateUsText
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.updateScreenStatus
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.userIp
import com.tsz.live.football.tv.streaming.hd.utils.objects.CustomDialogue
import com.tsz.live.football.tv.streaming.hd.utils.objects.DebugChecker
import com.tsz.live.football.tv.streaming.hd.utils.objects.Defemation
import com.tsz.live.football.tv.streaming.hd.utils.objects.InternetUtils.isPrivateDnsSetup
import com.tsz.live.football.tv.streaming.hd.utils.objects.SharedPreference
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.AdManagerListener
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.ApiResponseListeners
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.DialogListener
import com.tsz.live.football.tv.streaming.hd.viewModel.DataViewModel
import com.tsz.live.football.tv.streaming.hd.viewModel.MatchesViewModel

class HomeActivity : AppCompatActivity(), DialogListener, ApiResponseListeners,
NavController.OnDestinationChangedListener ,AdManagerListener{

    private val tags = "HomeActivity"
    private var binding : ActivityHomeBinding? = null
    private var context: Context? = null
    private val viewModel by lazy {
        ViewModelProvider(this)[DataViewModel::class.java]
    }

    private val matchesViewModel by lazy {
        ViewModelProvider(this)[MatchesViewModel::class.java]
    }
    private val logger = Logger()
    private var backBoolean = false
    private var navController: NavController? = null
    private var preference: SharedPreference? = null

    var time = "0"
    var intentLink = ""
    private var appRating: Boolean? = false
    private var appRatingStatus: Boolean? = false
    private var dialog: Dialog? = null
    private var dialog2: Dialog? = null
    var navigation: BottomNavigationView? = null
    private var adManager: AdManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        context = this

        setSupportActionBar(binding?.toolBar)
        binding?.lifecycleOwner = this
        viewModel?.apiResponseListeners = this
        adManager = AdManager(this, this, this)
//        AdSettings.addTestDevice("5eaf3d0b-d0df-4b28-a6db-f19953f5da5d")

        preference = SharedPreference(this)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        setStartDestination(Constants.appLiveStatus)

        setUpNavigationController()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                try {
                    if (backBoolean) {
                        if(appRating != true){
                            if (!isFinishing) {
                                showBackButtonDialogue("", false, "")
                            }
                        }else{
                            finishAffinity()
                        }
                    } else {
                        binding?.navHostFragment?.findNavController()?.popBackStack()
                    }
                } catch (e: Exception) {
                    logger.printLog(tags, "Exception : ${e.localizedMessage}")
                    logger.printLog(tags, "Exception : ${e.cause}")
                }
            }
        })

        binding?.eventEdittext?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.searchText.value=s.toString()
//                search(s.toString(), list as MutableList<Event>)
            }
        })
        clickListener()
        getValuesData()

        binding?.drawerLayout?.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {
                if (Constants.appLiveStatus){
                    binding?.includedLayout?.scoreLay?.visibility = View.VISIBLE
                }
                else{
                    binding?.includedLayout?.scoreLay?.visibility = View.GONE
                    binding?.includedLayout?.notification?.let {
                        setMarginsProgrammatically(
                            it,
                            0,100,0,0,)
                    }

                }
            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {
                when (newState) {
                    DrawerLayout.STATE_IDLE -> println("Drawer is idle")
                    DrawerLayout.STATE_DRAGGING -> {
                        if (Constants.appLiveStatus){
                            binding?.includedLayout?.scoreLay?.visibility = View.VISIBLE
                        }
                        else{
                            binding?.includedLayout?.scoreLay?.visibility = View.GONE
                        }
                    }
                    DrawerLayout.STATE_SETTLING -> println("Drawer is settling")
                }
            }

        })

        matchesViewModel?.isDrawerClicked?.observe(this, Observer {
            if (it){
                if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) != true) {
                    binding?.drawerLayout?.openDrawer(GravityCompat.START)
                }
            }
        })
        setUpOriginal()

//        createBanner(adManager)
    }

    fun setMarginsProgrammatically(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        val layoutParams = view.layoutParams
        if (layoutParams is ConstraintLayout.LayoutParams) {
            layoutParams.setMargins(left, top, right, bottom)
            view.layoutParams = layoutParams
        }
    }



    private fun setStartDestination(appLiveStatus: Boolean) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        if (appLiveStatus == true) {
            graph.setStartDestination(R.id.event)
//            graph.startDestination = R.id.StreamingFragment
//            navigation?.menu?.findItem(R.id.StreamingFragment)?.isEnabled = true
//            navigation?.menu?.findItem(R.id.StreamingFragment)?.isVisible = true


        } else {
            graph.setStartDestination(R.id.football)
        }

        val navController = navHostFragment.navController
        navController.setGraph(graph, intent.extras)
    }

    private fun setUpNavigationController() {
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfigurationList = AppBarConfiguration(
            setOf(
                R.id.event,
            ), binding?.drawerLayout
        )

        setupActionBarWithNavController(this, navController!!, appBarConfigurationList)
        binding?.drawer?.setupWithNavController(navController = navController!!)
        navController?.addOnDestinationChangedListener(this)

    }

    private fun showBackButtonDialogue(
        appUpdateText: String?, permanent: Boolean?,
        destination: String
    ) {
        dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.custom_layout2)

        val textExit = dialog?.findViewById(R.id.rateus) as Button
        val textNo = dialog?.findViewById(R.id.cancel) as Button
        val textFront = dialog?.findViewById(R.id.textView) as TextView
        val textBelow = dialog?.findViewById(R.id.textView2) as TextView
        val textIcon = dialog?.findViewById(R.id.icon_clcik) as ImageView

        if (destination.equals("update", true)) {
            textFront.text = resources.getString(R.string.newVersion)
            textNo.text = resources.getString(R.string.update_text2)
            if (appUpdateText != null) {
                textBelow.text = appUpdateText
            }
            if (permanent == true) {
                dialog?.setCancelable(false)
                textExit.text = resources.getString(R.string.exit)

            } else {
                textExit.text = resources.getString(R.string.update_text1)
                dialog?.setCancelable(true)
            }
        } else {
            dialog?.setCancelable(true)
            textExit.text = "Rate Us"
            textNo.text = "Exit"
        }

        textExit.setOnClickListener {
            if (destination.equals("update", true)) {
                if (permanent == true) {
                    app_update_dialog = false
                    dialog?.dismiss()
                    finishAffinity()
                } else {
                    dialog?.dismiss()
                }

            } else {
                app_update_dialog = false
//                dialog.dismiss()
//                finishAffinity()
                rateClicked()
            }
        }

        textNo.setOnClickListener {
            if (destination.equals("update", true)) {
                rateClicked()
            } else {
                app_update_dialog = false
                dialog?.dismiss()
                finishAffinity()
            }
        }

        textIcon.setOnClickListener {
            rateClicked()
        }

        if (!isFinishing) {
            dialog?.show()
        }
    }

    private fun rateClicked() {
        try {
            val viewIntent = Intent(
                "android.intent.action.VIEW",
                Uri.parse("https://play.google.com/store/apps/details?id=${this.packageName}")
            )
            if (viewIntent.resolveActivity(packageManager) != null) {
                startActivity(viewIntent)
            }
            preference?.rateUs(rateUsKey,true)
        }catch (e: Exception){
            preference?.rateUs(rateUsKey, true)
        }
    }






    private fun getApiBaseUrl(replaceChar: String) {
        // char IS EXTRA 2
        try {
            val baseValue = Defemation.encryptBase64(replaceChar)
            val getSecretValue = Defemation.decryptPRNG(baseValue)
            Defemation.convertData(getSecretValue)
        } catch (e: Exception) {
            Log.d("Exception", "message" + e.message)
        }
    }

    private fun getValuesData() {

        if (userIp.contains("userIp")){
            viewModel?.getIPdata()

            viewModel.isLoadingIpApi.observe(this){
                if (it) {
                    binding?.progressBar?.visibility = View.GONE
                } else {
                    binding?.progressBar?.visibility = View.VISIBLE
                }
            }

        }

        try {
            val myParcelable: DataModel? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("data", DataModel::class.java)
                } else {
                    intent.getParcelableExtra<DataModel>("data")
                }
            if (myParcelable != null) {
                viewModel.setUpMainData(myParcelable)
            } else {
                viewModel.setUpError("Something went wrong,please retry.")
            }
        } catch (e: java.lang.Exception) {
            viewModel.setUpError("Something went wrong,please retry.")
            Log.d("Exception", "null")
        }
        setUpModelData()

        viewModel.dataModelListGet.observe(this) {

            nativeFieldVal = it.extra_3!!

            if (it.events.isNullOrEmpty()) {
                ///means events are  null
                CustomDialogue(this).showDialog(
                    this, "title", "Something went Wrong",
                    "Retry", "Exit", "eventValue"
                )
            }


            if (Constants.location1Provider != "none") {

                adManager?.loadAdProvider(
                    Constants.location1Provider, Constants.adLocation1, binding?.adView,
                    binding?.fbAdView, binding?.unityBannerView, binding?.startAppBanner
                )
            }

            if (it.application_configurations != null) {
                if (it.application_configurations!!.isNotEmpty()) {
                    showSplashMethod(it.application_configurations)
                }

            }

            viewModel.appUpdateAvailable.observe(this, Observer { it1 ->
                if (it1) {
                    if (!app_update_dialog) {
                        //showAppUpdateDialog(it.app_update_text, it.is_permanent_dialog)
                        //if (!BuildConfig.DEBUG) {
                        showBackButtonDialogue(
                            it.app_update_text,
                            it.is_permanent_dialog,
                            "update"
                        )
                        app_update_dialog = true
                        //}
                    }
                }else {
                    if (!it.application_configurations.isNullOrEmpty()) {
                        val status = preference?.getRateUsBool(rateUsKey)
                        if (status != true) {
                            it.application_configurations?.forEach { config ->
                                if (config.key.equals("rateShow", true)) {
                                    if (config.value != null) {
                                        if (config.value.equals("True", true)) {
                                            rateUsDialogValue = true
                                        } else {
                                            rateUsDialogValue = false
                                        }
                                    }
                                }

                                if (config.key.equals("rateText", true)) {
                                    if (config.value != null) {
                                        rateUsText = config.value.toString()
                                    }
                                }
                            }
                            if (rateUsDialogValue) {
                                if (!rateShown) {
                                    rateShown = true
                                    rateUsDialog(rateUsText)
                                }
                            }
                        }
                        //  Log.d("ValuesIn", "msf" + rateUsDialogValue + " " + rateUsText)
                    }
                }
            })
        }

        viewModel.baseValue.observe(this, Observer {
            if (!it) {
                CustomDialogue(this).showDialog(
                    this, "title", "Value are Missing",
                    "Retry", "Exit", "baseValue"
                )
            }
        })
    }


    private fun setUpOriginal() {
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
                    viewModel.parseDataAndNotify(seperationBasedOnLetter!![0], filterValue)
//                    setUpViewModel()


                } catch (e: java.lang.Exception) {
                    viewModel.setUpError("Something is wrong with response,please retry.")
                }
            }
        })

    }


    private fun setUpModelData() {
        viewModel.isLoading.observe(this) {
            if (it) {
                binding?.progressBar?.visibility = View.VISIBLE

            } else {
                binding?.progressBar?.visibility = View.GONE
            }
        }
    }

    private fun clickListener() {

        binding?.search?.setOnClickListener {
            binding?.mainText?.visibility = View.VISIBLE
            binding?.search?.visibility = View.GONE
            binding?.cancel?.visibility = View.VISIBLE
        }

        binding?.cancel?.setOnClickListener {
            binding?.eventEdittext?.setText("")
            binding?.mainText?.visibility = View.GONE
            binding?.search?.visibility = View.VISIBLE
            binding?.cancel?.visibility = View.GONE
        }

        binding?.sideMenuIcon?.setOnClickListener {
            if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) != true) {
                binding?.drawerLayout?.openDrawer(GravityCompat.START)
            }
        }

        binding?.includedLayout?.menu?.setOnClickListener {
            if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }
        }

        binding?.includedLayout?.scoreLay?.setOnClickListener {
            navController?.navigate(R.id.football)
            if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }
        }

        binding?.includedLayout?.ppLay?.setOnClickListener {
            showPrivacyPolicyPage()
        }

        binding?.includedLayout?.rateLay?.setOnClickListener {
            rateUsFunction()
        }

        binding?.includedLayout?.contLay?.setOnClickListener {
            contactUsFunction()

        }
        binding?.includedLayout?.shareLay?.setOnClickListener {
            shareUsFunction()
        }

        binding?.includedLayout?.versionText?.text = "Version: " + BuildConfig.VERSION_NAME
    }

    private fun contactUsFunction() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, Array(1) { "" })
        intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
        startActivity(Intent.createChooser(intent, "Send Email..."))
    }

    private fun shareUsFunction() {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT, "Please download this app for live  scores.\n" +
                        "https://play.google.com/store/apps/details?id=" + packageName
            )
            intent.type = "text/plain"
            startActivity(intent)
        } catch (e: Exception) {

        }
    }

    private fun showPrivacyPolicyPage() {
        try {
            val url = "https://traumsportzone.com/#privacy-policy"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e: ActivityNotFoundException) {

        }
    }

    private fun rateUsFunction() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {

            }
        }
    }

    private fun showSplashMethod(applicationConfigurations: List<ApplicationConfiguration>?) {
        var showingSplash = false
        if (!applicationConfigurations.isNullOrEmpty()) {
            applicationConfigurations.forEach { configValue ->

                if (configValue.key.equals("Time", true)) {
                    if (configValue.value != null) {
                        time = configValue.value!!
                    }
                }
                if (configValue.key.equals("baseURL", true)) {
                    if (configValue.value != null) {
                        baseUrlDemo = configValue.value.toString()
                    }
                }
                ///For setting button text
                if (configValue.key.equals("ButtonText", true)) {
                    if (configValue.value != null) {
                        binding?.splashButton?.text = configValue.value
                    }

                }

                ///For setting heading
                if (configValue.key.equals("Heading", true)) {
                    if (configValue.value != null) {
                        binding?.splashHeading?.text = configValue.value
                    }

                }

                ///For setting link
                if (configValue.key.equals("ButtonLink", true)) {
                    if (configValue.value != null) {
                        intentLink = configValue.value!!
                    }

                }

                ///For setting body
                if (configValue.key.equals("DetailText", true)) {
                    if (configValue.value != null) {
                        binding?.splashBody?.text = configValue.value
                    }
                }


                ///For setting show button
                if (configValue.key.equals("ShowButton", true)) {
                    if (configValue.value != null) {
                        if (configValue.value.equals("True", true)) {
                            binding?.splashButton?.visibility = View.VISIBLE
                        } else {
                            binding?.splashButton?.visibility = View.GONE
                        }

                    }

                }

                if (configValue.key.equals("ShowSplash", true)) {
                    if (configValue.value.equals("true", true)) {
                        if (!updateScreenStatus) {
                            showingSplash = true
                        }
                    }
                }

            }

            if (showingSplash) {
                try {
                    var timer: Int = time.toInt()
                    timer *= 1000
                    binding?.splashLayout?.visibility = View.VISIBLE
                    binding?.splashButton?.setOnClickListener {

                        val uri =
                            Uri.parse(intentLink)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)

                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        updateScreenStatus = true
                        binding?.splashLayout?.visibility = View.GONE
                    }, timer.toLong())
                } catch (e: NumberFormatException) {

                }

            }
        }

    }

    private fun rateUsDialog(
        rateText: String?
    ) {
        dialog2 = context?.let { Dialog(it) }
        dialog2?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog2?.setContentView(R.layout.app_update_layout)

        val rateClicked = dialog2?.findViewById(R.id.rateUs) as Button
        val cancelCliked = dialog2?.findViewById(R.id.cancelUs) as Button
        val rateTxt = dialog2?.findViewById(R.id.rateUsTitle) as TextView

        rateTxt.text = rateText

        rateClicked.setOnClickListener {
            rateClicked()
        }

        cancelCliked.setOnClickListener {
            dialog2?.dismiss()
        }

        if (!isFinishing) {
            dialog2?.show()
        }

    }




    override fun onResume() {
        super.onResume()
        DebugChecker.checkDebugging(this)
        checkVpn()
        isPrivateDnsSetup(this)
        appRatingStatus = preference?.getRateUsBool(rateUsKey)

        if(appRatingStatus == true){
            appRating = true

//            if (dialog != null) {
//                dialog?.dismiss()
//            }

            if (dialog2 != null) {
                dialog2?.dismiss()
            }

        }else{
            appRating = false
        }
    }

    private fun checkVpn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val connectivityManager = context?.getSystemService(ConnectivityManager::class.java)
            val activeNetwork = connectivityManager?.activeNetwork
            val networkCapabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
            if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true) {
                if (!binding?.adblockLayout?.isVisible!!) {
                    binding?.adblockLayout?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onPositiveDialogText(key: String) {
        when (key) {
            "baseValue" -> viewModel.getCMSData()
            "isInternet" -> viewModel.getCMSData()
            "eventValue" -> viewModel.getCMSData()
        }
    }

    override fun onNegativeDialogText(key: String) {
        when (key) {
            "baseValue" -> finishAffinity()
            "isInternet" -> finishAffinity()
            "eventValue" -> finishAffinity()
        }
    }

    override fun onStarted() {

    }

    override fun onSuccess() {

    }

    override fun onFailure(message: String) {
        CustomDialogue(this).showDialog(
            this, "title", message,
            "Retry", "Exit", "isInternet"
        )
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {

        if (Constants.appLiveStatus) {
            backBoolean = destination.id == R.id.event
        }
        else{
            if (destination.id == R.id.football){
                backBoolean =true
            }
            else{
                backBoolean = false
            }
        }


        if(destination.id == R.id.channel){
            binding?.mainAppBar?.visibility = View.GONE
        }else if(destination.id == R.id.football || destination.id == R.id.matchDetailFragment){
            binding?.mainAppBar?.visibility = View.GONE
        }
        else{
            binding?.mainAppBar?.visibility = View.VISIBLE
        }
    }

    companion object {
        var checkAdListner: AdsListener? = null
    }

    override fun onAdLoad(value: String) {

    }

    override fun onAdFinish() {

    }
}