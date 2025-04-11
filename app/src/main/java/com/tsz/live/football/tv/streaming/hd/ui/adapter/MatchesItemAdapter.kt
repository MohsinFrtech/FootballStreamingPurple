package com.tsz.live.football.tv.streaming.hd.ui.adapter

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.facebook.ads.NativeAd
import com.tsz.live.football.tv.streaming.hd.R
import com.tsz.live.football.tv.streaming.hd.adsdata.AdManager
import com.tsz.live.football.tv.streaming.hd.databinding.ItemFixtureBinding
import com.tsz.live.football.tv.streaming.hd.databinding.NativeAdLayoutBinding
import com.tsz.live.football.tv.streaming.hd.databinding.NewItemFixtureBinding
import com.tsz.live.football.tv.streaming.hd.models.FootballMatches
import com.tsz.live.football.tv.streaming.hd.models.NewList
import com.tsz.live.football.tv.streaming.hd.ui.fragments.MatchesFragmentDirections
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.nativeFacebook
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MatchesItemAdapter(
    private val onClickListener: OnClickListener,
    private val showDate: Boolean = false,
    private val list: List<NewList?>,
    private val adType: String = "none",
    private val context: Context,
    private  val findNavController: NavController,
    private val adManager: AdManager
) :
    ListAdapter<NewList, RecyclerView.ViewHolder>(DiffCallback) {
    private val Simple_Menu_Layout = 0
    private val Native_Ads_Layout = 1
    private var fbNativeAd: NativeAd? = null

    var binding2: NativeAdLayoutBinding? = null


    class ViewHolder(private var binding: NewItemFixtureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            fixture: NewList,
            showDate: Boolean,
            context: Context,
            findNavController: NavController
        ) {
             binding.fixture = fixture
            var count = 0
//            binding.firstLay.setOnClickListener{
//                if (count == 0){
//                    binding.itemFixtureRecycler.visibility = View.GONE
//                   // binding.imgdown.setImageDrawable(R.drawable.forward)
//                    binding?.imgdown?.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            context,
//                            R.drawable.forward
//                        )
//                    )
//                    count++
//                }else{
//                    binding.itemFixtureRecycler.visibility = View.VISIBLE
//                    binding?.imgdown?.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            context,
//                            R.drawable.down
//                        )
//                    )
//                    count = 0
//                }
//            }

            val adapterMatches =
                context?.let { it1 ->
                    MatchesDetialAdpater(
                        onClickListener = MatchesDetialAdpater.OnClickListener {
                        val directions =
                            MatchesFragmentDirections.actionFootballToDetail(
                                "12", it
                            )
                        findNavController.navigate(directions)
                        }, showDate ,fixture.ListOfMatches, Constants.nativeAdProvider, context

                    )
                }
            binding?.itemFixtureRecycler?.layoutManager = LinearLayoutManager(context)
            binding?.itemFixtureRecycler?.adapter = adapterMatches
            adapterMatches?.submitList(fixture.ListOfMatches)
            binding.executePendingBindings()
        }

        private fun dateAndTime(channelDate: String?, fixture: FootballMatches, showDate: Boolean) {
            try {
                val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
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

//                if (fixture.status != null) {
//                    if (fixture.status!!.short.equals("NS", true)) {
//                        binding?.textStatus?.text = "$hours:$minutes $timeInAmOrPm"
//                    } else {
//                        binding?.textStatus?.text = fixture.status!!.short.toString()
//                    }
//                }  // By Haris

//                if (!showDate) {
//                    binding?.dateTime?.text = "$dayOfTheWeek ,$day $monthString $year"
//                    Log.d(
//                        "DataAndTime", "val" + "$dayOfTheWeek,$day $monthString $year"
//                                + " " + "$hours:$minutes $timeInAmOrPm"
//                    )
//                }

            } catch (e: Exception) {
                Log.d("Exception", "")
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Native_Ads_Layout -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.native_ad_layout, parent, false)
                binding2 = DataBindingUtil.bind(view)
                MatchesItemAdViewHolder(view)
            }

            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                ViewHolder(NewItemFixtureBinding.inflate(layoutInflater, parent, false))
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (list?.get(position) == null) {
            return Native_Ads_Layout
        }
        return Simple_Menu_Layout
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var viewType = getItemViewType(position)
        when (viewType) {
            Native_Ads_Layout -> {
                val viewHolder: MatchesItemAdViewHolder = holder as MatchesItemAdViewHolder

                ////////////
                if (adType.equals(Constants.facebook, true)) {

                    if (Constants.currentNativeAdFacebook != null) {
                        binding2?.fbNativeAdContainer?.let {
                            adManager.inflateFbNativeAd(
                                Constants.currentNativeAdFacebook!!, it
                            )
                        }
                    }
                    else
                    {
                        fbNativeAd = com.facebook.ads.NativeAd(context, nativeFacebook)
                        binding2?.adLoadLay2?.visibility = View.VISIBLE
                        binding2?.fbNativeAdContainer?.let {
                            adManager.loadFacebookNativeAd(
                                fbNativeAd!!,
                                it, binding2?.adLoadLay2
                            )
                        }
                    }
                }
                else if (adType.equals(Constants.adManagerAds, true)) {
                    binding2?.adLoadLay?.visibility = View.VISIBLE
                    binding2?.nativeAdView?.let {
                        adManager.loadAdmobNativeAdWithManager(
                            viewHolder,
                            it,
                            binding2?.adLoadLay
                        )
                    }
                }
                else if (adType.equals(Constants.admob, true)) {

                    if (Constants.currentNativeAd != null) {
                        binding2?.nativeAdView?.let {
                            adManager.populateNativeAdView(
                                Constants.currentNativeAd!!,
                                it
                            )
                        }
//                        binding2?.nativeAdView?.let { adManager.loadAdmobNativeAd(viewHolder, it) }
                    } else {
                        binding2?.adLoadLay?.visibility = View.VISIBLE
                        binding2?.nativeAdView?.let {
                            adManager.loadAdmobNativeAd(
                                viewHolder,
                                it,
                                binding2?.adLoadLay
                            )
                        }
                    }
                }
            }

            else -> {
                val viewHolder: ViewHolder = holder as ViewHolder

                val fixture = getItem(position)
                viewHolder.bind(fixture, showDate,context,findNavController)
//                viewHolder.itemView.setOnClickListener {
//                    onClickListener.onClick(fixture)
//                }
            }
        }

    }

    class MatchesItemAdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    companion object DiffCallback : DiffUtil.ItemCallback<NewList>() {
        override fun areItemsTheSame(oldItem: NewList, newItem: NewList): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: NewList,
            newItem: NewList
        ): Boolean {
            return oldItem.League.id == newItem.League.id
        }

    }

    class OnClickListener(val clickListener: (match: FootballMatches) -> Unit) {
        fun onClick(matched: FootballMatches) = clickListener(matched)
    }
}