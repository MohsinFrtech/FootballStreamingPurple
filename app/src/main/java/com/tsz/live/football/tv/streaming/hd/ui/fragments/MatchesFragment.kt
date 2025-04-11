package com.tsz.live.football.tv.streaming.hd.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsz.live.football.tv.streaming.hd.R
import com.tsz.live.football.tv.streaming.hd.adsdata.AdManager
import com.tsz.live.football.tv.streaming.hd.databinding.FragmentMatchesBinding
import com.tsz.live.football.tv.streaming.hd.horizontalcalendar.HorizontalCalendar
import com.tsz.live.football.tv.streaming.hd.horizontalcalendar.HorizontalCalendarView
import com.tsz.live.football.tv.streaming.hd.horizontalcalendar.utils.HorizontalCalendarListener
import com.tsz.live.football.tv.streaming.hd.models.FootballMatches
import com.tsz.live.football.tv.streaming.hd.models.League
import com.tsz.live.football.tv.streaming.hd.models.NewList
import com.tsz.live.football.tv.streaming.hd.ui.adapter.MatchesItemAdapter
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.formatDate
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.AdManagerListener
import com.tsz.live.football.tv.streaming.hd.viewModel.MatchesViewModel
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MatchesFragment : Fragment() ,AdManagerListener{

    var matchesBinding: FragmentMatchesBinding? = null
    private val matchesViewModel by lazy {
        activity?.let { ViewModelProvider(it)[MatchesViewModel::class.java] }
    }

    var count = 0
    val list: MutableList<String> = ArrayList()
    val listdetail: MutableList<NewList> = ArrayList()
    private var adManager: AdManager? = null

    lateinit var horizontalCalendarView: HorizontalCalendar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_matches, container, false)
        matchesBinding = DataBindingUtil.bind(view)
        matchesBinding?.lifecycleOwner = this
        matchesBinding?.viewModel = matchesViewModel
        adManager = AdManager(requireContext(), requireActivity(), this)

        matchesBinding?.ivLive?.visibility = View.VISIBLE
        matchesBinding?.let { setUpCalender(it) }

        matchesViewModel?.getDateData(matchesViewModel!!.date.formatDate())
        dateAndTime(matchesViewModel?.date?.formatDate())

//        matchesBinding?.calendarView?.visibility = View.VISIBLE

        getAllMatchesByDate()

        matchesViewModel?.openCalender?.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            showDatePicker()
        })

        matchesBinding?.ivLive?.setOnClickListener {
            if (count == 0) {
//                matchesBinding?.calendarView?.visibility = View.INVISIBLE
                matchesBinding?.ivCalender?.visibility = View.INVISIBLE
                matchesBinding?.search?.visibility = View.INVISIBLE

                count++
                matchesBinding?.ivLive?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_live_active
                    )
                )
                matchesBinding?.tvLiveMatches?.text = "Live Scores"
                setLiveRecycler(true)
            } else {
//                matchesBinding?.calendarView?.visibility = View.VISIBLE
                matchesBinding?.ivCalender?.visibility = View.VISIBLE
                matchesBinding?.search?.visibility = View.VISIBLE

                count = 0
                matchesBinding?.ivLive?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_live_inactive_new
                    )
                )
                setLiveRecycler(false)
                matchesBinding?.tvLiveMatches?.text = "Scores"

            }
        }

        if (Constants.appLiveStatus){
            matchesBinding?.sideMenuIcon?.visibility = View.GONE
        }
        else{
            matchesBinding?.sideMenuIcon?.visibility = View.VISIBLE
        }

        matchesBinding?.search?.setOnClickListener {
            matchesBinding?.mainText?.visibility = View.VISIBLE
            matchesBinding?.search?.visibility = View.GONE
            matchesBinding?.cancel?.visibility = View.VISIBLE
        }

        matchesBinding?.cancel?.setOnClickListener {
            matchesBinding?.eventEdittext?.setText("")
            matchesBinding?.mainText?.visibility = View.GONE
            matchesBinding?.search?.visibility = View.VISIBLE
            matchesBinding?.cancel?.visibility = View.GONE
        }

        matchesBinding?.sideMenuIcon?.setOnClickListener {
            matchesViewModel?.isDrawerClicked?.value = true
        }

//        getAllliveMatches()

        return view
    }

    private fun searchEvents(list: List<NewList>, s: String) {
        search(s, list as MutableList<NewList>)
    }

    private fun search(
        text: String,
        liveAndNonemptyEvents: MutableList<NewList>
    ) {
        //new array list that will hold the filtered data
        val localList: MutableList<NewList> = java.util.ArrayList<NewList>()
        //looping through existing elements
        for (s in liveAndNonemptyEvents) {

            //if the existing elements contains the search input
            if (s.League.name?.trim()?.lowercase(Locale.ROOT)?.contains(text.lowercase(Locale.ROOT))!!) {
                //adding the element to filtered list
                localList.add(s)
            }
        }

        if (!localList.isNullOrEmpty()){
            val listWithAd: List<NewList?> =
                if (Constants.nativeAdProvider != "none") {
                    Constants.checkNativeAd(localList)
                } else {
                    localList
                }
            val adapterMatches =
                context?.let { it1 ->
                    adManager?.let { it2 ->
                        MatchesItemAdapter(
                            onClickListener = MatchesItemAdapter.OnClickListener {
                                val directions =
                                    MatchesFragmentDirections.actionFootballToDetail(
                                        "12", it
                                    )
                                findNavController().navigate(directions)
                            }, true, listWithAd, Constants.nativeAdProvider, requireContext(),findNavController(),
                            it2

                        )
                    }
                }
            matchesBinding?.matchesRecycler?.layoutManager = LinearLayoutManager(requireContext())
            matchesBinding?.matchesRecycler?.adapter = adapterMatches
            adapterMatches?.submitList(listWithAd)
        }
        else{
            val adapterMatches =
                context?.let { it1 ->
                    adManager?.let { it2 ->
                        MatchesItemAdapter(
                            onClickListener = MatchesItemAdapter.OnClickListener {
                                val directions =
                                    MatchesFragmentDirections.actionFootballToDetail(
                                        "12", it
                                    )
                                findNavController().navigate(directions)
                            }, true, localList, Constants.nativeAdProvider, requireContext(),findNavController(),
                            it2

                        )
                    }
                }
            matchesBinding?.matchesRecycler?.layoutManager = LinearLayoutManager(requireContext())
            matchesBinding?.matchesRecycler?.adapter = adapterMatches
            adapterMatches?.submitList(localList)
        }


    }

    private fun getAllliveMatches() {
        matchesViewModel?.isLoadingByLive?.observe(viewLifecycleOwner, Observer {
            if (it) {
                matchesBinding?.progress?.visibility = View.VISIBLE
                matchesBinding?.matchesRecycler?.visibility = View.GONE
                matchesBinding?.noRecentMatches?.visibility = View.GONE
            } else {
                matchesBinding?.progress?.visibility = View.GONE
            }
        })
        matchesViewModel?.liveMatchesList?.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                matchesBinding?.noRecentMatches?.visibility = View.GONE
                convertDataIntoExpanded(it)
            } else {
                matchesBinding?.matchesRecycler?.visibility = View.GONE
                matchesBinding?.noRecentMatches?.text = "No live match available to show."
                matchesViewModel?.isLoadingByLive?.observe(viewLifecycleOwner, Observer {
                    if (!it){
                        matchesBinding?.noRecentMatches?.visibility = View.VISIBLE
                    }
                })
//                if (matchesBinding?.progress?.isVisible == true) {
//                    matchesBinding?.noRecentMatches?.visibility = View.GONE
//                } else {
//                    matchesBinding?.noRecentMatches?.visibility = View.VISIBLE
//                }
            }
        })
    }

    private fun setLiveRecycler(b: Boolean) {
        if (b) {
            if (matchesBinding?.progress?.isVisible != true) {
                matchesViewModel?.getLiveMatches()
                getAllliveMatches()
            }
        } else {
            if (matchesBinding?.progress?.isVisible != true) {
                getAllMatchesByDate()
            }
        }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                val cal = Calendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                horizontalCalendarView.selectDate(cal, true)

            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    //Matches sorted by date
    private fun getAllMatchesByDate() {
        matchesViewModel?.isLoadingByDate?.observe(viewLifecycleOwner, Observer {
            if (it) {
                matchesBinding?.progress?.visibility = View.VISIBLE
                matchesBinding?.matchesRecycler?.visibility = View.GONE
                matchesBinding?.noRecentMatches?.visibility = View.GONE
            } else {
                matchesBinding?.progress?.visibility = View.GONE
            }
        })
        matchesViewModel?.dateMatchesList?.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                matchesBinding?.noRecentMatches?.visibility = View.GONE
                convertDataIntoExpanded(it)
            } else {
                matchesBinding?.matchesRecycler?.visibility = View.GONE
                matchesBinding?.noRecentMatches?.text ="Matches are not available for this selected date."
                matchesBinding?.noRecentMatches?.visibility = View.VISIBLE
//                if (matchesBinding?.progress?.isVisible == true) {
//                    matchesBinding?.noRecentMatches?.visibility = View.GONE
//                } else {
//                    matchesBinding?.noRecentMatches?.visibility = View.VISIBLE
//                }
            }
        })

    }

    private fun convertDataIntoExpanded(footballMatches: List<FootballMatches>) {
        list?.clear()
        listdetail?.clear()
        footballMatches.forEach {
            list.add(it.league?.name.toString())
        }

        val Distinctlist = list.distinct()
        for (a in 0 until Distinctlist.size) {
            val valueDisinct = Distinctlist.get(a)

            val updsedList: MutableList<FootballMatches> = mutableListOf()
            var valueB: League? = null
            for (b in footballMatches.indices) {
                val Name = footballMatches.get(b).league?.name.toString()
                if (valueDisinct == Name) {
                    valueB = footballMatches.get(b).league
                    updsedList.add(footballMatches.get(b))
                }
            }
            listdetail.add(NewList(valueB!!, updsedList))
        }

        setLiveMatchesAdapter(listdetail, true)
    }

    private fun setLiveMatchesAdapter(it: List<NewList>, value: Boolean) {
        matchesBinding?.matchesRecycler?.visibility = View.VISIBLE
        val matchList = it
        val listWithAd: List<NewList?> =
            if (Constants.nativeAdProvider != "none") {
                Constants.checkNativeAd(it)
            } else {
                it
            }
        val adapterMatches =
            context?.let { it1 ->
                adManager?.let { it2 ->
                    MatchesItemAdapter(
                        onClickListener = MatchesItemAdapter.OnClickListener {
                            val directions =
                                MatchesFragmentDirections.actionFootballToDetail(
                                    "12", it
                                )
                            findNavController().navigate(directions)
                        }, value, listWithAd, Constants.nativeAdProvider, requireContext(),findNavController(),
                        it2

                    )
                }
            }
        matchesBinding?.matchesRecycler?.layoutManager = LinearLayoutManager(requireContext())
        matchesBinding?.matchesRecycler?.adapter = adapterMatches
        adapterMatches?.submitList(listWithAd)
        matchesBinding?.eventEdittext?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                searchEvents(matchList, s.toString())
//                matchesViewModel?.searchText?.value=s.toString()
//                search(s.toString(), list as MutableList<Event>)
            }
        })

//        matchesViewModel?.searchText?.observe(viewLifecycleOwner, Observer {
//            if (it != null) {
//
//            }
//        })


    }
    private fun dateAndTime(channelDate: String?){
        try {
            val df = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
            df.timeZone = TimeZone.getTimeZone("UTC")
            val date = channelDate?.let { df.parse(it) }
            df.timeZone = TimeZone.getDefault()
            val formattedDate = date?.let { df.format(it) }
            val date2: Date? = formattedDate?.let { df.parse(it) }
            val cal = Calendar.getInstance()
            if (date2 != null) {
                cal.time = date2
            }
            var hours = cal[Calendar.HOUR_OF_DAY]
            val minutes = cal[Calendar.MINUTE]
            var timeInAmOrPm = ""

            if (hours > 0) {
                timeInAmOrPm = if (hours >= 12) {
                    "PM"
                } else {
                    "AM"
                }
            }


            if (hours > 0) {
                if (hours >= 12) {
                    if (hours == 12) {
                        /////
                    } else {
                        hours -= 12
                    }
                }
            }

            if (hours == 0) {
                hours = 12
                timeInAmOrPm = "AM"
            }

            val dayOfTheWeek =
                DateFormat.format("EEEE", date) as String

            val day = DateFormat.format("dd", date) as String

            val monthString =
                DateFormat.format("MMM", date) as String
            val year = DateFormat.format("yyyy", date) as String


            matchesBinding?.textDate?.text =  "$dayOfTheWeek,$day $monthString,$year"
        }
        catch (e:Exception){

        }

    }


    private fun setUpCalender(binding: FragmentMatchesBinding) {
        horizontalCalendarView = HorizontalCalendar.Builder(binding.root, binding.calendarView.id)
            .range(matchesViewModel?.startDate, matchesViewModel?.endDate)
            .defaultSelectedDate(matchesViewModel?.date)
            .datesNumberOnScreen(7)
            .configure()
            .showTopText(false)
            .sizeMiddleText(10F)
            .formatMiddleText("dd MMM")
            .formatBottomText("EEE")
            .sizeBottomText(11F)
            .end()
            .build()

        horizontalCalendarView.calendarListener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar, position: Int) {
                matchesViewModel?.changeStartDate(date)
                dateAndTime(matchesViewModel?.date?.formatDate())

                matchesViewModel?.getDateData(date.formatDate())
            }

            override fun onCalendarScroll(calendarView: HorizontalCalendarView, dx: Int, dy: Int) {
                //Not needed as of now
            }

            override fun onDateLongClicked(date: Calendar, position: Int): Boolean {
                return true
            }
        }

    }

    override fun onAdLoad(value: String) {

    }

    override fun onAdFinish() {

    }


}