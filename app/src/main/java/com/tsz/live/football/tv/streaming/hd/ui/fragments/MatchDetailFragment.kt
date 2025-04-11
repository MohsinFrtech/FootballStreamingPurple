package com.tsz.live.football.tv.streaming.hd.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tsz.live.football.tv.streaming.hd.databinding.FragmentMatchDetailBinding
import com.tsz.live.football.tv.streaming.hd.network.ApiResponseListener
import com.tsz.live.football.tv.streaming.hd.ui.activities.HomeActivity
import com.tsz.live.football.tv.streaming.hd.utils.AdsListener
class MatchDetailFragment : Fragment(), ApiResponseListener {

    val TAG = "ApiResult"

    private lateinit var binding: FragmentMatchDetailBinding
//    private lateinit var viewModel: MatchDetailViewModel
//    private lateinit var viewModelFactory: MatchDetailViewModelFactory

//    private val viewModel_fixtures: FixturesViewModel by lazy {
//        ViewModelProviders.of(requireActivity()).get(FixturesViewModel::class.java)
//    }
    private var checkAdListner: AdsListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMatchDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        checkAdListner = HomeActivity.checkAdListner
        val args = MatchDetailFragmentArgs.fromBundle(requireArguments())
        val matchID = args.matchID
        val matchDetail = args.matchDetail
        binding?.viewModel = matchDetail

        var score: String? = "${matchDetail?.goals?.home}-${matchDetail?.goals?.away}"
        if (score.equals("")) {
            score =""
//                matchDetail.value?.get(0)?.event_timestamp?.formatTime()?.toDate()?.toLocalTime()
        }
        if (matchDetail?.status?.longName?.equals("Not Started", true) == true) {
            score = matchDetail?.status?.longName.toString()
        } else {
//            showTimer.set(false)
        }

        if (matchDetail?.status?.short?.equals("1H", true) == true || matchDetail?.status?.short?.equals(
                "2H",
                true
            ) == true || matchDetail?.status?.short?.equals("HT", true) == true
        ) {
            score = "${matchDetail?.goals?.home}-${matchDetail?.goals?.away}"
        }


        binding?.tvScore?.text = score
        if (matchDetail?.score?.halftime?.away!=null && matchDetail?.score?.halftime?.home!=null){
           binding?.textScoreHlf?.text = "${matchDetail?.score?.halftime?.home}-${matchDetail?.score?.halftime?.away}"
        }
        else
        {
            binding?.textScoreHlf?.text = "0-0"
        }

        if (matchDetail?.score?.fulltime?.away!=null && matchDetail?.score?.fulltime?.home!=null){
            binding?.textScoreHlfull?.text = "${matchDetail?.score?.fulltime?.home}-${matchDetail?.score?.fulltime?.away}"
        }
        else
        {
            binding?.textScoreHlfull?.text = "0-0"
        }

        if (matchDetail?.score?.extratime?.away!=null && matchDetail?.score?.extratime?.home!=null){
            binding?.textScoreHlExtra?.text = "${matchDetail?.score?.extratime?.home}-${matchDetail?.score?.extratime?.away}"
        }
        else
        {
            binding?.textScoreHlExtra?.text = "0-0"
        }

        if (matchDetail?.score?.penalty?.away!=null && matchDetail?.score?.penalty?.home!=null){
            binding?.textScorePenality?.text = "${matchDetail?.score?.penalty?.home}-${matchDetail?.score?.penalty?.away}"
        }
        else
        {
            binding?.textScorePenality?.text = "0-0"
        }
        binding?.ivBack?.setOnClickListener {
            requireActivity().onBackPressed()
        }
        setTabs()
//        setObservers()

        return binding.root
    }

    private fun setTabs() {

    }

    private fun setObservers() {
//        viewModel_fixtures.liveFixturesList.observe(this, Observer {
//            it?.let {
//                viewModel.setLiveMatchData(it)
//            }
//        })
//
//        viewModel.backClick.observe(this, Observer {
//            activity!!.onBackPressed()
//        })
    }

    override fun onStarted() {
        Log.i(TAG, "Started")
    }

    override fun onSuccess() {
        Log.i(TAG, "Success")
    }

    override fun onFailure(message: String) {
        Log.i(TAG, "Error: $message")
    }

}
