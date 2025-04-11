package com.tsz.live.football.tv.streaming.hd.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.google.gson.Gson
import com.tsz.live.football.tv.streaming.hd.models.FootballData
import com.tsz.live.football.tv.streaming.hd.models.FootballMatches
import com.tsz.live.football.tv.streaming.hd.network.ApiResponseListener
import com.tsz.live.football.tv.streaming.hd.network.RetrofitController
import com.tsz.live.football.tv.streaming.hd.utils.SingleLiveEvent
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import com.tsz.live.football.tv.streaming.hd.utils.objects.InternetUtils
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.await
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Calendar


class MatchesViewModel(application: Application?) : AndroidViewModel(application!!) {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)
    val isLoadingRecent = MutableLiveData<Boolean>()
    val isLoadingUpcoming = MutableLiveData<Boolean>()

    val isLoading = MutableLiveData<Boolean>()

    val isDrawerClicked = MutableLiveData<Boolean>()

    var apiResponseListener: ApiResponseListener? = null
    val searchText = MutableLiveData<String>()

//    private val _LeagueList = MutableList<Boolean>()
//    val LeagueList: LiveData<Boolean> get() = _LeagueList

    var i = 0
    var n = ""

    //is loading by date...
    private val _isLoadingByDate = MutableLiveData<Boolean>()
    val isLoadingByDate: LiveData<Boolean>
        get() = _isLoadingByDate

    //is loading by live...
    private val _isLoadingByLive = MutableLiveData<Boolean>()
    val isLoadingByLive: LiveData<Boolean>
        get() = _isLoadingByLive

    private val _liveMatchesList = MutableLiveData<List<FootballMatches>>()
    val liveMatchesList: LiveData<List<FootballMatches>>
        get() = _liveMatchesList

    private val _dateMatchesList = MutableLiveData<List<FootballMatches>>()
    val dateMatchesList: LiveData<List<FootballMatches>>
        get() = _dateMatchesList

    private val _upcomingMatchesList = MutableLiveData<List<FootballMatches>>()
    val upcoingMatchesList: LiveData<List<FootballMatches>>
        get() = _upcomingMatchesList
    var startDate: Calendar = Calendar.getInstance()
    var endDate: Calendar = Calendar.getInstance()
    var date: Calendar = Calendar.getInstance()
    private val _openCalender = SingleLiveEvent<Any>()
    val openCalender: LiveData<Any>
        get() = _openCalender

    init {
        isLoading.value = true
        isLoadingRecent.value = true
        isDrawerClicked.value = false
        isLoadingUpcoming.value = false
//        getRecentMatches()
//        getLiveMatches()
        setUpCalenderView()
    }

    fun setUpCalenderView() {
        startDate.add(Calendar.MONTH, -3)
        endDate.add(Calendar.MONTH, 3)

    }


    //get Live Matches...
    fun getLiveMatches() {
        _isLoadingByLive.value = true

        val token = Constants.footballToken
        val addUser = FootballData(token)
        val body = Gson().toJson(addUser)
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())


        coroutineScope.launch {
            val getResponse = RetrofitController.apiServiceFootball.getLiveMatches(
                body
            )

            try {
                val responseResult = getResponse.await()
                withContext(Dispatchers.Main) {
                    responseResult.let {
                        try {
                            it.sortedByDescending { it1 ->
                                it1.updatedAt
                            }
                            _liveMatchesList.value = responseResult
                        } catch (e: Exception) {
                            _isLoadingByLive.value = false
                            apiResponseListener?.onFailure("Something is wrong with response")
                        }
                    }
                    _isLoadingByLive.value = false
                }

            } catch (e: Exception) {
                if (e is UnknownHostException || e is SocketTimeoutException) {
                    withContext(Dispatchers.Main) {
                        _isLoadingByLive.value = false
                        apiResponseListener?.onFailure("Server is taking too long to respond.")
                    }
                }
                withContext(Dispatchers.Main) {
                    _isLoadingByLive.value = false
                    apiResponseListener?.onFailure("Server is taking too long to respond.")
                }
            }

        }
//        if (InternetUtils.isInternetOns()) {
//            val token = Constants.footballToken
//            val addUser = FootballData(token)
//            val body = Gson().toJson(addUser)
//                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//
//
//            coroutineScope.launch {
//                val getResponse = RetrofitController.apiServiceFootball.getLiveMatches(
//                    body
//                )
//
//                try {
//                    val responseResult = getResponse.await()
//                    withContext(Dispatchers.Main) {
//                        responseResult.let {
//                            try {
//                                it.sortedByDescending { it1 ->
//                                    it1.updatedAt
//                                }
//                                _liveMatchesList.value = responseResult
//                            } catch (e: Exception) {
//                                _isLoadingByLive.value = false
//                                apiResponseListener?.onFailure("Something is wrong with response")
//                            }
//                        }
//                        _isLoadingByLive.value = false
//                    }
//
//                } catch (e: Exception) {
//                    if (e is UnknownHostException || e is SocketTimeoutException) {
//                        withContext(Dispatchers.Main) {
//                            _isLoadingByLive.value = false
//                            apiResponseListener?.onFailure("Server is taking too long to respond.")
//                        }
//                    }
//                    withContext(Dispatchers.Main) {
//                        _isLoadingByLive.value = false
//                        apiResponseListener?.onFailure("Server is taking too long to respond.")
//                    }
//
//                }
//
//            }
//
//        } else {
//            _isLoadingByLive.value = false
//            apiResponseListener?.onFailure("Internet connection lost! , please check your internet connection")
//
//        }
    }

    fun changeStartDate(date: Calendar) {
        this.date = date
    }

    //get Recent Matches...
    fun getRecentMatches() {
        isLoadingRecent.value = true
        if (InternetUtils.isInternetOns()) {
            val token = Constants.footballToken
            val addUser = FootballData(token)
            val body = Gson().toJson(addUser)
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())


            coroutineScope.launch {
                val getResponse = RetrofitController.apiServiceFootball.getRecentMatches(
                    body
                )

                try {
                    val responseResult = getResponse.await()
                    withContext(Dispatchers.Main) {
                        responseResult.let {
                            try {
//                                it.sortedByDescending { it1 ->
//                                    it1.updatedAt
//                                }
//                                _recentMatchesList.value = responseResult
                            } catch (e: Exception) {
                                isLoadingRecent.value = false
                                apiResponseListener?.onFailure("Something is wrong with response")
                            }
                        }
                        isLoadingRecent.value = false
                    }

                } catch (e: Exception) {
                    if (e is UnknownHostException || e is SocketTimeoutException) {
                        withContext(Dispatchers.Main) {
                            isLoadingRecent.value = false
                            apiResponseListener?.onFailure("Server is taking too long to respond.")
                        }
                    }
                    withContext(Dispatchers.Main) {
                        isLoadingRecent.value = false
                        apiResponseListener?.onFailure("Server is taking too long to respond.")
                    }

                }

            }

        } else {
            isLoadingRecent.value = false
            apiResponseListener?.onFailure("Internet connection lost! , please check your internet connection")

        }
    }


    fun getDateData(date: String){
        _isLoadingByDate.value = true
        coroutineScope.launch {
            getMatchesByDate(date)
        }
    }
    //get Recent Matches...
   private suspend fun getMatchesByDate(formatDate: String) {
        val token = Constants.footballToken
        val addUser = FootballData(token, 0, 0, 0, 0, formatDate)
        val body = Gson().toJson(addUser)
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        try {
            val getResponse = RetrofitController.apiServiceFootball.getMatchesBydate(body)

            val responseResult = getResponse.await()
            withContext(Dispatchers.Main) {
                responseResult.let {
                    try {
                        _dateMatchesList.value = responseResult
                    } catch (e: Exception) {
                        _isLoadingByDate.value = false
                        apiResponseListener?.onFailure("Something is wrong with response")
                    }
                }
                _isLoadingByDate.value = false
            }

        } catch (e: Exception) {
            if (e is UnknownHostException || e is SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    _isLoadingByDate.value = false
                    apiResponseListener?.onFailure("Server is taking too long to respond.")
                }
            }
            withContext(Dispatchers.Main) {
                _isLoadingByDate.value = false
                apiResponseListener?.onFailure("Server is taking too long to respond.")
            }

        }
//        if (InternetUtils.isInternetOns()) {
//            val token = Constants.footballToken
//            val addUser = FootballData(token, 0, 0, 0, 0, formatDate)
//            val body = Gson().toJson(addUser)
//                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//
//            try {
//                val getResponse = RetrofitController.apiServiceFootball.getMatchesBydate(
//                    body
//                )
//                val responseResult = getResponse.await()
//                withContext(Dispatchers.Main) {
//                    responseResult.let {
//                        try {
//                            _dateMatchesList.value = responseResult
//                        } catch (e: Exception) {
//                            _isLoadingByDate.value = false
//                            apiResponseListener?.onFailure("Something is wrong with response")
//                        }
//                    }
//                    _isLoadingByDate.value = false
//                }
//
//            } catch (e: Exception) {
//                if (e is UnknownHostException || e is SocketTimeoutException) {
//                    withContext(Dispatchers.Main) {
//                        _isLoadingByDate.value = false
//                        apiResponseListener?.onFailure("Server is taking too long to respond.")
//                    }
//                }
//                withContext(Dispatchers.Main) {
//                    _isLoadingByDate.value = false
//                    apiResponseListener?.onFailure("Server is taking too long to respond.")
//                }
//
//            }
//        } else {
//            withContext(Dispatchers.Main){
//                _isLoadingByDate.value = false
//                apiResponseListener?.onFailure("Internet connection lost! , please check your internet connection")
//            }
//        }
    }

    fun selectDate() {
        _openCalender.call()
    }

    //get Upcoming Matches...
//    fun getUpcomingMatches() {
//        isLoadingUpcoming.value = true
//        if (InternetUtils.isInternetOns()) {
//            val token = Constants.footballToken
//            val addUser = FootballData(token)
//            val body = Gson().toJson(addUser)
//                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//
//
//            coroutineScope.launch {
//                val getResponse = RetrofitController.apiServiceFootball.getUpcomingMatches(
//                    body
//                )
//
//                try {
//                    val responseResult = getResponse.await()
//                    withContext(Dispatchers.Main) {
//                        responseResult.let {
//                            try {
//                                it.sortedByDescending { it1 ->
//                                    it1.updatedAt
//                                }
//                                _upcomingMatchesList.value = responseResult
//                            } catch (e: Exception) {
//                                isLoadingUpcoming.value = false
//                                apiResponseListener?.onFailure("Something is wrong with response")
//                            }
//                        }
//                        isLoadingUpcoming.value = false
//                    }
//
//                } catch (e: Exception) {
//                    if (e is UnknownHostException || e is SocketTimeoutException) {
//                        withContext(Dispatchers.Main) {
//                            isLoadingUpcoming.value = false
//                            apiResponseListener?.onFailure("Server is taking too long to respond.")
//                        }
//                    }
//                    withContext(Dispatchers.Main) {
//                        isLoadingUpcoming.value = false
//                        apiResponseListener?.onFailure("Server is taking too long to respond.")
//                    }
//
//                }
//
//            }
//
//        } else {
//            isLoadingUpcoming.value = false
//            apiResponseListener?.onFailure("Internet connection lost! , please check your internet connection")
//
//        }
//    }

    // On ViewModel Cleared
    override fun onCleared() {
        super.onCleared()
        viewModelJob.let {
            viewModelJob.cancel()
        }

    }

}