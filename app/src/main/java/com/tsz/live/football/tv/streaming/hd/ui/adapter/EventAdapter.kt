package com.tsz.live.football.tv.streaming.hd.ui.adapter

import android.content.Context
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsz.live.football.tv.streaming.hd.R
import com.tsz.live.football.tv.streaming.hd.databinding.ItemLayoutEventBinding
import com.tsz.live.football.tv.streaming.hd.models.Event
import com.tsz.live.football.tv.streaming.hd.ui.fragments.EventFragmentDirections
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.NavigateData

class EventAdapter(val context: Context, private val navigateData: NavigateData)
    : ListAdapter<Event, EventAdapter.EventAdapterViewHolder>(EventAdapterDiffUtilCallback){

    private var bindingEvent: ItemLayoutEventBinding?=null
    class EventAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    object EventAdapterDiffUtilCallback: DiffUtil.ItemCallback<Event>()
    {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem==newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_layout_event,parent,false)
        bindingEvent= DataBindingUtil.bind(view)
        return EventAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventAdapterViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        bindingEvent?.dataModel=currentList[position]
        bindingEvent?.executePendingBindings()

        holder.itemView.setSafeOnClickListener {
            val direction= EventFragmentDirections.actionEventToChannel(currentList[position])

            navigateData.navigation(direction)
        }
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
}