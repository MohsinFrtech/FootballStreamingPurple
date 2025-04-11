package com.tsz.live.football.tv.streaming.hd.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsz.live.football.tv.streaming.hd.R
import com.tsz.live.football.tv.streaming.hd.adsdata.AdManager
import com.tsz.live.football.tv.streaming.hd.adsdata.NewAdManager
import com.tsz.live.football.tv.streaming.hd.databinding.FragmentChannelBinding
import com.tsz.live.football.tv.streaming.hd.models.Channel
import com.tsz.live.football.tv.streaming.hd.ui.adapter.ChannelAdapter
import com.tsz.live.football.tv.streaming.hd.utils.AppContextProvider
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.currentCountryCode
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.locationAfter
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.nativeAdProvider
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.nativeFieldVal
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.navDirections
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.AdManagerListener
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.NavigateData
import com.tsz.live.football.tv.streaming.hd.viewModel.DataViewModel
import java.lang.Exception

class ChannelFragment : Fragment(), NavigateData,AdManagerListener {
    private var adManager: AdManager? = null

    var bindingChannel : FragmentChannelBinding? = null
    private var adStatus = false
//    private var navDirections: NavDirections? = null
    private val viewModel by lazy {
        activity?.let { ViewModelProvider(it)[DataViewModel::class.java] }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_channel, container, false)
        bindingChannel = DataBindingUtil.bind(view)
        bindingChannel?.lifecycleOwner = this
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        adManager = AdManager(requireContext(),requireActivity(),this)

        if (Constants.middleAdProvider.equals(Constants.startApp, true)) {

            adManager?.loadAdProvider(
                Constants.middleAdProvider,
                Constants.adMiddle,
                null,
                null,
                null,
                null
            )

        }

        bindingChannel?.back?.setOnClickListener {
            findNavController().popBackStack()
        }

        bindingChannel?.back?.setOnClickListener {
            findNavController().popBackStack()
        }

        setChannelData()

//        bindingMain?.mainAppBar?.visibility = View.GONE
        return view
    }

    private fun setChannelData() {
        val args: ChannelFragmentArgs by navArgs()
        if (args.getEvent != null) {
            bindingChannel?.channelName?.text = args.getEvent!!.name
            setChannels(args.getEvent?.channels)
        }
    }

    private fun setChannels(channelList: List<Channel>?) {

        val liveChannels: MutableList<Channel> = ArrayList()
        for (channel in channelList!!) {
            if (channel.live == true) {
                channel.isSelected = false
            }
            //////
            var channels_belongs_country = false
            if (channel.live==true)
            {
                if (!channel.country_codes.isNullOrEmpty()){

                    channel.country_codes?.forEach {
                            code->
                        if (code?.equals(currentCountryCode, true) == true){
                            channels_belongs_country = true
                        }
                    }
                    if (channels_belongs_country){
                        liveChannels.add(channel)
                    }
                }else{
                    liveChannels.add(channel)
                }
            }
        }
        liveChannels.sortBy { it1 ->
            it1.priority
        }

        val listWithAd = checkNativeAd(liveChannels)

        val adapter = adManager?.let {
            ChannelAdapter(
                requireContext(), listWithAd, nativeAdProvider,
                this, nativeFieldVal, it
            )
        }

        bindingChannel?.recyclerviewChannels?.layoutManager = LinearLayoutManager(context)
        bindingChannel?.recyclerviewChannels?.adapter = adapter
        adapter?.submitList(listWithAd)
    }

    ////Function to return list of events with empty positions.....
    private fun checkNativeAd(list: List<Channel>): List<Channel?> {
        val tempChannels: MutableList<Channel?> =
            ArrayList()
        for (i in list.indices) {
            if (list[i].live!!) {
                val diff = i % 5
                if (diff == 2) {

                    tempChannels.add(null)
                }
                tempChannels.add(list[i])
                if (list.size == 2) {
                    if (i == 1) {
                        tempChannels.add(null)
                    }
                }
            }
        }
        return tempChannels
    }

    override fun navigation(viewId: NavDirections) {
        try {
            if (viewId.actionId != null) {
                navDirections = viewId
                if (!Constants.locationBeforeProvider.equals("none", true)) {
                    if (!Constants.locationBeforeProvider.equals(Constants.startApp,true)){
                        if (!Constants.locationBeforeProvider.equals(Constants.unity,true))
                        {
                            bindingChannel?.progrssBar?.visibility = View.VISIBLE
                            val local = AppContextProvider.getContext()
                            local?.let {
                                NewAdManager.showAds(
                                    Constants.locationBeforeProvider,
                                    requireActivity(),
                                    it
                                )
                            }
                        }
                        else{
                            if (Constants.playerActivityInPip){
                                findNavController().navigate(viewId)
                            }
                            else{
                                bindingChannel?.progrssBar?.visibility = View.VISIBLE
                                val local = AppContextProvider.getContext()
                                local?.let {
                                    NewAdManager.showAds(
                                        Constants.locationBeforeProvider,
                                        requireActivity(),
                                        it
                                    )
                                }
                            }

                        }
//                    requireActivity().window.setFlags(
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                    )
                    }
                    else{
                        findNavController().navigate(viewId)
                    }

                } else {
//                    requireActivity().getWindow()
//                        .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    findNavController().navigate(viewId)
                }
            }
        } catch (e: Exception) {
            Log.d("Exception","msg")
        }
    }

    override fun onResume() {
        super.onResume()
        NewAdManager.setAdManager(this)
        viewModel?.dataModelListGet?.observe(viewLifecycleOwner)
        {
            if (!it.extra_3.isNullOrEmpty()) {
                nativeFieldVal = it.extra_3!!
            }

            if (!it.app_ads.isNullOrEmpty()) {

                Constants.location2TopProvider = adManager?.checkProvider(
                    it.app_ads!!,
                    Constants.adLocation2top
                ).toString()
                Constants.location2TopPermanentProvider = adManager?.checkProvider(
                    it.app_ads!!,
                    Constants.adLocation2topPermanent
                ).toString()
                Constants.location2BottomProvider = adManager?.checkProvider(
                    it.app_ads!!,
                    Constants.adLocation2bottom
                ).toString()

                if (locationAfter.equals(Constants.startApp, true)) {
                    if (Constants.videoFinish) {
                        Constants.videoFinish = false
                        adManager?.loadAdProvider(locationAfter, locationAfter, null, null, null, null)
                    }
                }
            }

        }
    }

    override fun onAdLoad(value: String) {

    }

    override fun onAdFinish() {
        if (bindingChannel?.progrssBar?.isVisible == true){
            bindingChannel?.progrssBar?.visibility=View.GONE
        }
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if (navDirections != null) {
            findNavController().navigate(navDirections!!)
        }
    }

}