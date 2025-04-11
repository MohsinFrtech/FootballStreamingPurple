package com.tsz.live.football.tv.streaming.hd.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tsz.live.football.tv.streaming.hd.R
import com.tsz.live.football.tv.streaming.hd.databinding.FormatItemBinding
import com.tsz.live.football.tv.streaming.hd.models.FormatDataAudioMedia3
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.positionClick4
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.previousClick4
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.FormatSelectionAudioMedia3
import java.lang.Exception
import java.util.Locale


class FormatDataAdapterAudioMedia3(
    val context: Context,
    private val formatSelection: FormatSelectionAudioMedia3
) : ListAdapter<FormatDataAudioMedia3, FormatDataAdapterAudioMedia3.EventAdapterViewHolder>(
    EventAdapterDiffUtilCallback
) {


    private var bindingFormat: FormatItemBinding? = null
    private var liveChannelCount = 0

    object EventAdapterDiffUtilCallback : DiffUtil.ItemCallback<FormatDataAudioMedia3>() {
        override fun areItemsTheSame(oldItem: FormatDataAudioMedia3, newItem: FormatDataAudioMedia3): Boolean {
            return oldItem.token == newItem.token
        }

        override fun areContentsTheSame(oldItem: FormatDataAudioMedia3, newItem: FormatDataAudioMedia3): Boolean {
            return oldItem == newItem
        }

    }


    class EventAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgChannel = itemView.findViewById<ImageView>(R.id.check_uncheck)
        val textChannel = itemView.findViewById<TextView>(R.id.formatValue)
        val mainBack = itemView.findViewById<ConstraintLayout>(R.id.mainClick)

    }


    override fun onBindViewHolder(holder: EventAdapterViewHolder, position: Int) {
        try {
            if (currentList[position].token == null) {
                if (position == 0) {
                    holder.textChannel.text = "Auto"
                }
            } else {
              val fullName= currentList[position].token?.language?.let {
                    getLanguageNameFromCode(
                        it
                    )
                }
                holder.textChannel?.text = fullName.toString()

            }

            if (position == positionClick4) {
                holder.imgChannel.visibility = View.VISIBLE
            } else {
                holder.imgChannel.visibility = View.GONE
            }
            holder.mainBack.setOnClickListener {
                formatSelection.navigation(currentList[position], position,currentList[position].token?.language)
                positionClick4 = holder.absoluteAdapterPosition
                if (previousClick4 == -1)
                    previousClick4 = positionClick4
                else {
                    notifyItemChanged(previousClick4)
                    previousClick4 = positionClick4
                }
                notifyItemChanged(positionClick4)


            }
        }
        catch (e:Exception){
            Log.d("Exception","msg")
        }
    }

    fun getLanguageNameFromCode(languageCode: String): String {


        try {
            val localeParts = languageCode.split("-") // Handle "zh-CN", "pt-BR"
            val locale = if (localeParts.size > 1) Locale(localeParts[0], localeParts[1]) else Locale(localeParts[0])
            return locale.displayLanguage
        }
        catch (e:Exception){
            return  ""
        }
    }

    private fun getLanguageName(language: String?): String {



        when (language) {
            "en" -> {
                return "English"
            }
            "ar" -> {
                return "Arabic"
            }
            "de" -> {
                return "German"
            }
            "es" -> {
                return "Spanish"
                }
            "fr" -> {
                return "French"
            }
            "hbs-hrv"->{
                return "Croatian"
            }
            "it"->{
                return "Italian"
            }
            "ur"->{
                return "Urdu"
            }
            "ru"->{
                return "Russian"
            }
            "pt"-> {
                return "Portuguese"
            }
            "pl"->{
                return "Polish"
            }
            "nl"-> {
                return "Dutch"
            }
            "sv"->{
                return "Swedish"
            }
            "tr"->{
                return "Turkish"
            }
            "bg"->{
                return "Bulgarian"
            }
            "fi"->{
                return "Finnish"
            }
            "el"->{
                return "Greek"
            }
            "ja"->{
                return "Japanese"
            }
            else->{
                return "English"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.format_item, parent, false)
        bindingFormat = DataBindingUtil.bind(view)
        return EventAdapterViewHolder(view)
    }
}