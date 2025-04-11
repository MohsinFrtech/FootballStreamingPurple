package com.tsz.live.football.tv.streaming.hd.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.chartboost.sdk.impl.b
import com.tsz.live.football.tv.streaming.hd.R
import com.tsz.live.football.tv.streaming.hd.adsdata.AdManager
import com.tsz.live.football.tv.streaming.hd.adsdata.NewAdManager
import com.tsz.live.football.tv.streaming.hd.databinding.ActivityHomeBinding
import com.tsz.live.football.tv.streaming.hd.databinding.FragmentEventBinding
import com.tsz.live.football.tv.streaming.hd.models.Channel
import com.tsz.live.football.tv.streaming.hd.models.Event
import com.tsz.live.football.tv.streaming.hd.ui.adapter.EventAdapter
import com.tsz.live.football.tv.streaming.hd.utils.AppContextProvider
import com.tsz.live.football.tv.streaming.hd.utils.Logger
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.middleAdProvider
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.navDirections
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.AdManagerListener
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.NavigateData
import com.tsz.live.football.tv.streaming.hd.viewModel.DataViewModel
import java.lang.Exception
import java.util.ArrayList
import java.util.Locale

class EventFragment : Fragment(), NavigateData, AdManagerListener {

    var bindingMain: ActivityHomeBinding? = null
    var bindingFragment: FragmentEventBinding? = null

    //    private var navDirections: NavDirections? = null
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[DataViewModel::class.java]
    }
    private var navController: NavController? = null
    var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        savedInstanceState?.getBundle("nav_state")?.let {
            findNavController().restoreState(it)
        }
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_event, container, false)
        bindingFragment = DataBindingUtil.bind(view)
        bindingFragment?.lifecycleOwner = this
        bindingFragment?.dataModel = viewModel
        NewAdManager.setAdManager(this)

        setUpViewModel()

        return view
    }

    override fun onResume() {
        super.onResume()
        NewAdManager.setAdManager(this)
    }
    var liveChannelCount = 0

    private fun setUpViewModel() {
        viewModel.dataModelListGet.observe(viewLifecycleOwner, Observer {
            if (it.live == true) {
                bindingFragment?.noLiveText?.visibility = View.GONE
                bindingFragment?.eventRecycler?.visibility = View.VISIBLE
                if (!it.events.isNullOrEmpty()) {

                    bindingFragment?.noLiveText?.visibility = View.GONE
                    bindingFragment?.eventRecycler?.visibility = View.VISIBLE

                    val liveEvents: MutableList<Event> = ArrayList<Event>()

                    for (i in it.events!!) {
                        /////
                        var event_belongs_country = false
                        if (i.live == true) {
                            liveChannelCount = 0
                            if (!i.country_codes.isNullOrEmpty()) {
                                i.country_codes!!.forEach { code ->
                                    if (code?.equals(Constants.currentCountryCode, true) == true) {
                                        event_belongs_country = true
                                    }
                                }

                                if (event_belongs_country) {
                                    if (!i.channels.isNullOrEmpty()) {

                                        for (channel in i.channels!!) {
                                            if (channel.live == true) {
                                                liveChannelCount++
                                            }
                                        }

                                        if (liveChannelCount > 0) {
                                            liveEvents.add(i)
                                        }
                                    }
                                }
                            } else {
                                if (!i.channels.isNullOrEmpty()) {

                                    for (channel in i.channels!!) {
                                        if (channel.live == true) {
                                            liveChannelCount++
                                        }
                                    }

                                    if (liveChannelCount > 0) {
                                        liveEvents.add(i)
                                    }
                                }
                            }
                        }
                    }
                    if (liveEvents.isNotEmpty()) {
                        bindingFragment?.noLiveText?.visibility = View.GONE
                        bindingFragment?.eventRecycler?.visibility = View.VISIBLE
                        ///Sorting through event list....
                        liveEvents.sortBy { it1 ->
                            it1.priority
                        }
                        setAdapter(liveEvents)
                    } else {
                        bindingFragment?.noLiveText?.visibility = View.VISIBLE
                        bindingFragment?.eventRecycler?.visibility = View.GONE
                    }

                } else {
                    bindingFragment?.noLiveText?.visibility = View.VISIBLE
                    bindingFragment?.eventRecycler?.visibility = View.GONE
                }
            } else {
                bindingFragment?.noLiveText?.visibility = View.VISIBLE
                bindingFragment?.eventRecycler?.visibility = View.GONE
            }
        })
    }

    private fun setAdapter(liveEvents: MutableList<Event>) {
        val listAdapter = EventAdapter(requireContext(), this)
        bindingFragment?.eventRecycler?.layoutManager = GridLayoutManager(context, 2)
        bindingFragment?.eventRecycler?.adapter = listAdapter
        listAdapter.submitList(liveEvents)
        viewModel.searchText.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                searchEvents(listAdapter, liveEvents, it)
            }
        })
    }

    private fun searchEvents(adapter: EventAdapter, list: List<Event>, s: String) {
        search(s, list as MutableList<Event>)
//        bindingMain?.eventEdittext?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//
//            }
//        })
    }

    private fun search(
        text: String,
        liveAndNonemptyEvents: MutableList<Event>
    ) {
        //new array list that will hold the filtered data
        val localList: MutableList<Event> = ArrayList<Event>()
        //looping through existing elements
        for (s in liveAndNonemptyEvents) {
            //if the existing elements contains the search input
            if (s.name?.trim()?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.ROOT))!!) {
                //adding the element to filtered list
                localList.add(s)
            }
        }

        //     if (localList.isNotEmpty()) {
        val listAdapter = EventAdapter(requireContext(), this)
        bindingFragment?.eventRecycler?.layoutManager = GridLayoutManager(context, 2)
        bindingFragment?.eventRecycler?.adapter = listAdapter
        listAdapter.submitList(localList)
        //   }

    }


    override fun navigation(viewId: NavDirections) {
        try {
            if (viewId.actionId != null) {
                Constants.positionClick = -1
                Constants.previousClick = -1
                navDirections = viewId
                if (!middleAdProvider.equals("none", true)) {
                    if (!middleAdProvider.equals(Constants.startApp, true)) {
                        bindingFragment?.progressBar?.visibility = View.VISIBLE
                        val local = AppContextProvider.getContext()
                        local?.let { NewAdManager.showAds(middleAdProvider, requireActivity(), it) }
                    } else {
                        findNavController().navigate(viewId)
                    }
                } else {
                    findNavController().navigate(viewId)
                }

            }
        } catch (e: Exception) {
            Log.d("Exception", "msg")
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        findNavController().saveState()?.let {
            outState.putBundle("nav_state", it)
        }
    }

    override fun onAdLoad(value: String) {

    }

    override fun onAdFinish() {
        if (bindingFragment?.progressBar?.isVisible == true) {
            bindingFragment?.progressBar?.visibility = View.GONE
        }

        if (navDirections != null) {
//            requireActivity().findNavController()
            findNavController().navigate(navDirections!!)
        }
    }
}