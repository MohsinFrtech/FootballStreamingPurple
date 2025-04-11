package com.tsz.live.football.tv.streaming.hd.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tsz.live.football.tv.streaming.hd.databinding.NewItemFixtureDetailBinding
import com.tsz.live.football.tv.streaming.hd.models.FootballMatches

class MatchesDetialAdpater(
    private val onClickListener: OnClickListener,
    private val showDate: Boolean = false,
    private val list: List<FootballMatches?>,
    private val adType: String = "none",
    private val context: Context
) : ListAdapter<FootballMatches, MatchesDetialAdpater.ViewHolder>(DiffCallback) {


    class ViewHolder(private var binding: NewItemFixtureDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(fixture: FootballMatches, dotGone: Boolean) {
            binding.apply {
                fixture2 = fixture
                if (dotGone){
                    dotted?.visibility = View.GONE

                }
                else{
                    dotted?.visibility = View.VISIBLE

                }
                teamScore.text = fixture.goals?.home.toString()
                teamScore2.text = fixture.goals?.away.toString()

                textStatus.text = fixture.status!!.short.toString()


                if (fixture.venue!!.name!= null) {
                    textGround?.text = fixture.venue?.name.toString()
                }
                else{
                    textGround?.text = fixture.league?.country.toString()

                }

                var score: String? = "${fixture?.goals?.home}-${fixture?.goals?.away}"
                if (score.equals("")) {
                    score = ""
//                matchDetail.value?.get(0)?.event_timestamp?.formatTime()?.toDate()?.toLocalTime()
                }
                if (fixture?.status?.longName?.equals("Not Started", true) == true) {
//            showTimer.set(true)
//            setTimer()
//                    binding?.textScore?.text = "0-0"  //By Haris
                    teamScore?.text = ""
                    teamScore2?.text = ""
                } else {
//            showTimer.set(false)
                }

                if (fixture?.status?.short?.equals(
                        "1H",
                        true
                    ) == true || fixture?.status?.short?.equals(
                        "2H",
                        true
                    ) == true || fixture?.status?.short?.equals("HT", true) == true
                ) {
                    score = "${fixture?.goals?.home}-${fixture?.goals?.away}"
                }
                if (score != null) {
                    if (fixture.status?.short.equals("NS", true)) {
//                        binding?.textScore?.text = "NS"
                        teamScore?.text = ""
                        teamScore2?.text = ""
                    } else {
                        if (score.contains("null")) {
//                            binding?.textScore?.text = "0-0"
                            teamScore?.text = ""
                            teamScore2?.text = ""
                        } else {
                            teamScore.text = fixture.goals?.home.toString()
                            teamScore2.text = fixture.goals?.away.toString()
//                            binding?.textScore?.text = score
                        }
                    }
                }  // By Haris
//                dateAndTime(fixture?.date, fixture, showDate)


            }
        }

    }

    object DiffCallback : DiffUtil.ItemCallback<FootballMatches>() {
        override fun areItemsTheSame(
            oldItem: FootballMatches,
            newItem: FootballMatches
        ): Boolean {
            return oldItem.status == newItem.status
        }

        override fun areContentsTheSame(
            oldItem: FootballMatches,
            newItem: FootballMatches
        ): Boolean {
            return oldItem.status == newItem.status
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NewItemFixtureDetailBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        var dotGone= false
        if (position == list.size - 1) {
            // This is the last item
            dotGone =true
        }
        else{
            dotGone =false
        }
        holder.bind(item,dotGone)
        val viewHolder: ViewHolder = holder as ViewHolder
        viewHolder.itemView.setOnClickListener {
            onClickListener.onClick(item)
        }
    }

    class OnClickListener(val clickListener: (match: FootballMatches) -> Unit) {
        fun onClick(matched: FootballMatches) = clickListener(matched)
    }

}