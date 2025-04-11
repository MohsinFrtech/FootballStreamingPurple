package com.tsz.live.football.tv.streaming.hd.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tsz.live.football.tv.streaming.hd.databinding.FragmentEventBinding
import com.tsz.live.football.tv.streaming.hd.models.AddUser
import com.tsz.live.football.tv.streaming.hd.models.DataModel
import com.tsz.live.football.tv.streaming.hd.models.VariableFile
import com.tsz.live.football.tv.streaming.hd.network.RetrofitController
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.appVersionCode
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.authToken
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.baseUrlChannel
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.baseUrlDemo
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.channel_url_val
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.encryptKey
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.filterValue
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.myUserCheck1
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.splitnine
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.splitper
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.stringId
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.userIp
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.userLink
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.valuenine
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.valueper
import com.tsz.live.football.tv.streaming.hd.utils.objects.Defemation
import com.tsz.live.football.tv.streaming.hd.utils.objects.Defemation.encryptKeyFun
import com.tsz.live.football.tv.streaming.hd.utils.objects.InternetUtils
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.ApiResponseListeners
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit2.await
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class DataViewModel(application: Application?) : AndroidViewModel(application!!) {
    private val app: Application? = application
    var apiResponseListeners: ApiResponseListeners? = null
    private val viewModelJob = Job()
    private val corotineScope = CoroutineScope(viewModelJob + Dispatchers.IO)
    private val dataModelList = MutableLiveData<DataModel>()
    val dataModelListGet: LiveData<DataModel> get() = dataModelList

    val isLoading = MutableLiveData<Boolean>()
    var userLinkStatus = MutableLiveData<Boolean>()
    var baseValue = MutableLiveData<Boolean>()
    var appUpdateAvailable = MutableLiveData<Boolean>()
    val isLoadingIpApi = MutableLiveData<Boolean>()

    val searchText = MutableLiveData<String>()

    private val _stringValue = MutableLiveData<String?>()
    val stringValue: LiveData<String?>
        get() = _stringValue

    var binding: FragmentEventBinding? = null

    init {
        searchText.value = ""
        userLinkStatus.value = false
        baseValue.value = true
    }

    fun parseDataAndNotify(original:String,passVal:String){

        val stringValue =
            original?.let { it1 -> encryptKeyFun(it1, filterValue) }

        val jsonObject: JSONObject?
        try {
            jsonObject = stringValue?.let { JSONObject(it) }
            val gson: Gson = GsonBuilder().setLenient().create()

            val date =
                gson.fromJson(jsonObject.toString(), DataModel::class.java)

            dataModelList.value = date

            if (!date.app_version.isNullOrEmpty()) {

                val version: Int = appVersionCode
                val parsedInt = date.app_version!!.toInt()
                if (parsedInt > version) {
                    appUpdateAvailable.value = true
                } else {
                    appUpdateAvailable.value = false
                }
            }else{
                appUpdateAvailable.value = false
            }

            if (date.extra_1.toString().isNotEmpty()) {
                date.extra_1 = Defemation.decryptBase6(date.extra_1)
                val encrypt = date.extra_1.toString().trim()
                val yourArray: List<String> =
                    encrypt.split(splitper)
                valueper = yourArray[0].trim()
                val myRVal: List<String> =
                    yourArray[1].split(splitnine)

                valuenine = myRVal[0].trim()
            } else {

            }


        } catch (e: JSONException) {
//            isLoading.value = false
            apiResponseListeners?.onFailure("Something is wrong with response")
        }
    }


    fun setUpMainData(model: DataModel) {
        dataModelList.value = model
        if (!model.app_version.isNullOrEmpty()) {
            val versionCode: Int = appVersionCode
            val version: Int = model.app_version!!.toInt()

            if (version > versionCode) {
                appUpdateAvailable.value = true
            } else {
                appUpdateAvailable.value = false
            }
        } else {
            appUpdateAvailable.value = false
        }
    }

    fun setUpError(value: String) {
        apiResponseListeners?.onFailure(value)
    }

    fun onRefreshFixtures() {
        getCMSData()
        getIPdata()
    }

    fun getCMSData() {

//        Log.d("GetText", binding?.eventEdittext?.text.toString())
        isLoading.value = true
//        binding?.eventEdittext?.text?.clear()
        if (InternetUtils.isInternetOn(app)) {
            if (baseUrlChannel != "") {
                corotineScope.launch {
                    val varFile = VariableFile()
                    varFile.id = stringId.toString()
                    varFile.auth_token = authToken
                    varFile.build_no = appVersionCode.toString()
//                     varFile.build_no = "0"

                    val requestBody = Gson().toJson(varFile)
                        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                    val getResponse =
                        RetrofitController.apiServiceChannel.getChannelsAsync(requestBody)

                    try {
                        val resultResponse = getResponse.await()
                        withContext(Dispatchers.Main) {
                            resultResponse.let { dataStone ->
                                _stringValue.value = dataStone.data
//                                val stringValue =
//                                    dataStone.data?.let { it1 -> stoneDel(seperationBasedOnLetter!![0], "nXU") }
//
//                                val jsonObject: JSONObject?
//                                try {
//                                    jsonObject = stringValue?.let { JSONObject(it) }
//                                    val gson: Gson = GsonBuilder().setLenient().create()
//
//                                    val date =
//                                        gson.fromJson(jsonObject.toString(), DataModel::class.java)
//
//
//                                    _dataModelList.value = date
//
//
//                                    if (!date.app_version.isNullOrEmpty()) {
//
//                                        val version: Int = appVersionCode
//                                        val parsedInt = date.app_version!!.toInt()
//                                        if (parsedInt > version) {
//                                            appUpdateAvailable.value = true
//                                        } else {
//                                            appUpdateAvailable.value = false
//                                        }
//                                    }
//
//                                    if (date.extra_1.toString().isNotEmpty()) {
//                                        date.extra_1 = Defamation.decryptBase6(date.extra_1)
//                                        val encrypt = date.extra_1.toString().trim()
//                                        val yourArray: List<String> =
//                                            encrypt.split(userBaseExtraDel2)
//                                        myUserCheck1 = yourArray[0].trim()
//                                        val myRVal: List<String> =
//                                            yourArray[1].split(userBaseExtraDel1)
//                                        passphraseVal = myRVal[0].trim()
//                                    } else {
//
//                                    }
//
//
//                                } catch (e: JSONException) {
//                                    isLoading.value = false
//                                    apiResponseListener?.onFailure("Something is wrong with response")
//                                }

                            }
                            isLoading.value = false
                        }
                    } catch (e: Exception) {

                        if (e is SocketTimeoutException || e is UnknownHostException) {
                            withContext(Dispatchers.Main) {
                                _stringValue.value = ""
                                isLoading.value = false
                                apiResponseListeners?.onFailure("Server is taking too long to respond.")
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                _stringValue.value = ""
                                isLoading.value = false
                                apiResponseListeners?.onFailure("Something went wrong, Please try again")
                            }
                        }
                    }

                }
            }
        } else {
            isLoading.value = false
            apiResponseListeners?.onFailure("Internet connection lost! , please check your internet connection")
        }
    }

    fun getDemoData() {
        isLoading.value = true

        if (baseUrlDemo != "") {
            if (InternetUtils.isInternetOn(app)) {
//                isInternet.value = true

                val addUser = AddUser()
                addUser.passphrase = valuenine
                addUser.channel_url = channel_url_val
                val body = Gson().toJson(addUser)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())


                corotineScope.launch {
                    val getResponse = RetrofitController.apiServiceDemo.getDemoDataAsync(
                        body
                    )

                    try {
                        val responseResult = getResponse.await()
                        withContext(Dispatchers.Main) {
                            responseResult.let {
                                if (!it?.url.isNullOrEmpty()) {

                                    userLink = it?.url.toString()
                                    userLinkStatus.value = true

                                }

                            }
                            isLoading.value = false
                        }

                    } catch (e: Exception) {

                        withContext(Dispatchers.Main) {

                            isLoading.value = false

                        }

                    }

                }

            } else {

//                isInternet.value = false
                isLoading.value = false

            }
        } else {
            baseValue.value = false
        }
    }

    fun getIPdata() {
        if (InternetUtils.isInternetOn(app)) {
            isLoadingIpApi.value = false

            corotineScope.launch {
                val getResponse = RetrofitController.apiServiceChannel.getIPAsync()
                try {
                    val responseResult = getResponse?.await()
                    withContext(Dispatchers.Main) {
                        if (responseResult != null) {
                            userIp = responseResult.toString()
                            isLoadingIpApi.value = true
                        }
                    }
                } catch (e: java.lang.Exception) {
                    withContext(Dispatchers.Main) {
                        isLoadingIpApi.value = true
                    }
                }
            }
        }
    }


}