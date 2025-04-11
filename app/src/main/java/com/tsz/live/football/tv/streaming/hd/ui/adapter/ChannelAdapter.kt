package com.tsz.live.football.tv.streaming.hd.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.SystemClock
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.ads.NativeAd
import com.tsz.live.football.tv.streaming.hd.R
import com.tsz.live.football.tv.streaming.hd.adsdata.AdManager
import com.tsz.live.football.tv.streaming.hd.databinding.ItemLayoutChannelBinding
import com.tsz.live.football.tv.streaming.hd.databinding.NativeAdLayoutBinding
import com.tsz.live.football.tv.streaming.hd.models.Channel
import com.tsz.live.football.tv.streaming.hd.models.ProcessingFile
import com.tsz.live.football.tv.streaming.hd.ui.fragments.ChannelFragmentDirections
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.USER_AGENT
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.channel_url_val
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.dash
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.defaultString
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.hlsSource
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.nativeFacebook
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.positionClick
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.previousClick
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.sepUrl
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.userType2
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.userType4
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.userTypePhp
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.xForwardedKey
import com.tsz.live.football.tv.streaming.hd.utils.objects.Defemation
import com.tsz.live.football.tv.streaming.hd.utils.objects.SharedPreference
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.NavigateData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ChannelAdapter(
    val context: Context,
    val list: List<Channel?>,
    private val adType: String,
    private val navigateData: NavigateData,
    private val localVal: String,
    private val adManager: AdManager
): ListAdapter<Channel, RecyclerView.ViewHolder>(ChannelAdapterDiffUtilCallback) {

    private var channelAdapterBinding: ItemLayoutChannelBinding? = null
    private val nativeAdsLayout = 1
    private val simpleMenuLayout = 0
    private var fbNativeAd: NativeAd? = null
    private var binding2: NativeAdLayoutBinding? = null

    private var sharedPreference: SharedPreference? = null

    init {
        sharedPreference = SharedPreference(context)
    }

    object ChannelAdapterDiffUtilCallback : DiffUtil.ItemCallback<Channel>() {
        override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            nativeAdsLayout -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.native_ad_layout, parent, false)
                binding2 = DataBindingUtil.bind(view)
                NativeChannelViewHolder(view)
            }
            else -> {

                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_layout_channel, parent, false)
                channelAdapterBinding = DataBindingUtil.bind(view)
                ChannelAdapterViewHolder(view)
            }
        }
    }

    class ChannelAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageViewChannel = itemView.findViewById<ImageView>(R.id.channelImg)
        val textChannel = itemView.findViewById<TextView>(R.id.channel_name)
        val channelDateTime = itemView.findViewById<TextView>(R.id.channel_time)
        val channelBck = itemView.findViewById<CardView>(R.id.mainCard)
    }

    class NativeChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemViewType(position: Int): Int {
        if (list[position] == null) {
            return nativeAdsLayout
        }
        return simpleMenuLayout
    }

    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    class SafeClickListener(

        private var defaultInterval: Int = 1000,
        private val onSafeCLick: (View) -> Unit
    ) : View.OnClickListener {
        private var lastTimeClicked: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
                return
            }
            lastTimeClicked = SystemClock.elapsedRealtime()
            onSafeCLick(v)
        }
    }

    private fun dateAndTime(channelDate: String?, viewHolder: ChannelAdapterViewHolder) {
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH)
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

        if (minutes < 9) {
            viewHolder?.channelDateTime?.text =
                "$hours:0$minutes $timeInAmOrPm"
        } else {
            viewHolder?.channelDateTime?.text =
                "$hours:$minutes $timeInAmOrPm"
        }

        viewHolder?.channelDateTime?.visibility = View.VISIBLE
    }

    private fun setSelected(pos: Int) {
        try {
            if (list.size > 1) {
                list[sharedPreference?.getInt("position")!!]?.isSelected = false
                sharedPreference?.saveInt("position", pos)
            }
            list[pos]?.isSelected = true

            Constants.positionClick = pos

            notifyItemChanged(pos)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        holder.setIsRecyclable(false)
        when (getItemViewType(position)) {
            nativeAdsLayout -> {
                ////For native ads if ads_provider provide native ads..
                val viewHolder: NativeChannelViewHolder = holder as NativeChannelViewHolder
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
                ///To set remaining events
//                channelAdapterBinding?.modeldata = currentList[position]
                val viewHolder: ChannelAdapterViewHolder = holder as ChannelAdapterViewHolder
                viewHolder.textChannel.text = currentList[position].name
                if (!currentList[position].image_url.isNullOrEmpty()) {
                    loadImage(viewHolder.imageViewChannel, currentList[position].image_url)
                }
                else
                {
                    Glide.with(context)
                        .load(R.drawable.splash_icon)
                        .placeholder(R.drawable.splash_icon)
                        .into(viewHolder.imageViewChannel)
                }


                if (!currentList[position].date.isNullOrEmpty())
                    dateAndTime(currentList[position].date,viewHolder)





                if (position == positionClick) {
                    viewHolder?.channelBck?.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.purple)
                    )
                } else {
                    viewHolder?.channelBck?.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.colorPrimary)
                    )
                }

                holder.itemView.setSafeOnClickListener {
                    Constants.positionClick2 = 0
                    Constants.previousClick2 = -1


                    val timeValue: Long = if (!currentList[position].date.isNullOrEmpty()) {
                        getSelectedChannelTimeInMillis(currentList[position].date)
                    } else {
                        0
                    }

                    /// For handling initial time.......
                    if (!currentList[position]?.initial_time.isNullOrEmpty()){
                        val timeFromCms = currentList[position]?.initial_time?.toString()
                        if (timeFromCms != null) {
                            Constants.timeValueAtPlayer = timeFromCms.toInt()
                        }
                        else{
                            Constants.timeValueAtPlayer = 15
                        }
                    }
                    else{
                        Constants.timeValueAtPlayer = 15
                    }

                    positionClick = holder.absoluteAdapterPosition
                    if (previousClick == -1)
                        previousClick = positionClick
                    else {
                        notifyItemChanged(previousClick)
                        previousClick = positionClick
                    }
                    notifyItemChanged(positionClick)
                    try {
                        if (currentList[position]?.channel_type.equals(
                                userType2, true
                            )
                        ) {
                            if (currentList[position]?.url?.contains(sepUrl) == true
                                && Constants.valuenine.isNotEmpty()
                            ) {
                                val separatedPart =
                                    currentList[position]?.url?.split(sepUrl)

                                channel_url_val = separatedPart?.get(1).toString()

                                val channelDirection =
                                    ChannelFragmentDirections.actionChannelToPlayer2(
                                        "baseLink",
                                        "linkAppend", userType2,timeValue
                                    )
                                navigateData.navigation(channelDirection)
                            }

                        }else if(currentList[position]?.channel_type.equals(userType4, true)){
                            val url = currentList[position]?.url
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }

                        else if (currentList[position]?.channel_type.equals(
                                dash, true)){
                            //for playing dash media...
                            Constants.clearKeyKey =""
                            USER_AGENT = "ExoPlayer-Drm"
                            xForwardedKey =""
                            ///Dash media source...
                            if (!currentList[position]?.clear_key.isNullOrEmpty())
                            {
                                Constants.clearKeyKey = currentList[position]?.clear_key.toString()
                            }

                            //User Agent...
                            if (!currentList[position]?.user_agent.isNullOrEmpty()){
                                USER_AGENT = currentList[position]?.user_agent.toString()
                            }

                            if (!currentList[position]?.forwarded_for.isNullOrEmpty()){
                                xForwardedKey = currentList[position]?.forwarded_for.toString()
                            }
                            /////
                            val channelDirection=ChannelFragmentDirections.actionChannelToPlayer2(currentList[position].url, currentList[position].url,
                                dash,timeValue)
                            navigateData.navigation(channelDirection)
                        }
                        else if (currentList[position]?.channel_type.equals(
                                hlsSource, true)){
                            //for playing dash media...
                            Constants.clearKeyKey =""
                            USER_AGENT = "ExoPlayer-Drm"
                            xForwardedKey =""
                            ///Dash media source...
                            if (!currentList[position]?.clear_key.isNullOrEmpty())
                            {
                                Constants.clearKeyKey = currentList[position]?.clear_key.toString()
                            }
                            //User Agent...
                            if (!currentList[position]?.user_agent.isNullOrEmpty()){
                                USER_AGENT = currentList[position]?.user_agent.toString()
                            }

                            if (!currentList[position]?.forwarded_for.isNullOrEmpty()){
                                xForwardedKey = currentList[position]?.forwarded_for.toString()
                            }

                            val channelDirection =
                                ChannelFragmentDirections.actionChannelToPlayer2(
                                    currentList[position].url, currentList[position].url,
                                    hlsSource,timeValue
                                )
                            navigateData.navigation(channelDirection)


                        }
                        else if (currentList[position]?.channel_type.equals(
                                userTypePhp, true)){
                            //php channel .....
                            val channelDirection =
                                ChannelFragmentDirections.actionChannelToPlayer2(
                                    currentList[position].url, currentList[position].url,
                                    userTypePhp,timeValue
                                )
                            navigateData.navigation(channelDirection)
                        }
                        else {
                            val processingFile = ProcessingFile()
                            if (localVal.isNotEmpty()) {
                                defaultString = processingFile.encryptData(localVal)

                                val token =
                                    currentList[position].url?.let { it1 ->
                                        Defemation.improveDeprecatedCode(
                                            it1
                                        )
                                    }

                                // select channel for p2p
                                val userType = if (currentList[position]?.channel_type.equals(
                                        Constants.userType1, true))
                                {
                                    Constants.userType1
                                } else {
                                    Constants.userType3
                                }

                                val linkAppend = currentList[position].url + token
                                val channelDirection =
                                    ChannelFragmentDirections.actionChannelToPlayer2(
                                        currentList[position].url,
                                        linkAppend, userType,timeValue
                                    )
                                navigateData.navigation(channelDirection)
                            }
                        }
                    }
                    catch (e:Exception){

                    }
                }


            }
        }

    }

    private fun getSelectedChannelTimeInMillis(channelDate: String?): Long {
        try {
            val df = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH)
            df.timeZone = TimeZone.getTimeZone("UTC")
            val date = channelDate?.let { df.parse(it) }
            df.timeZone = TimeZone.getDefault()
            val formattedDate = date?.let { df.format(it) }
            val date2: Date? = formattedDate?.let { df.parse(it) }
            val calendar = Calendar.getInstance()
            if (date2 != null) {
                calendar.time = date2
            }
            return calendar.timeInMillis
        } catch (e: Exception) {
            return 0
        }
    }



}