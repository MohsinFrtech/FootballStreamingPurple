package com.tsz.live.football.tv.streaming.hd.utils.playerUtils

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.databinding.DataBindingUtil
import androidx.media3.common.C
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.collect.ImmutableList
import com.tsz.live.football.tv.streaming.hd.R
import com.tsz.live.football.tv.streaming.hd.databinding.PlayerScreenBottomLayoutBinding
import com.tsz.live.football.tv.streaming.hd.models.FormatDataMedia3
import com.tsz.live.football.tv.streaming.hd.ui.adapter.FormatDataAdapterMedia3
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.FormatSelectionMedia3


class PlayerScreenBottomSheetMedia3(player: ExoPlayer, size: Int) : BottomSheetDialogFragment(),
    FormatSelectionMedia3 {
    private val sizeBottomSheet = (size + 1.5) * 10.5

    private var playerScreenBottomSheet: PlayerScreenBottomLayoutBinding? = null

    private val playerInstance = player
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBackgroundDialog)
    }

    override fun onStart() {
        super.onStart()
        //this forces the sheet to appear at max height even on landscape
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.player_screen_bottom_layout, container, false)
        playerScreenBottomSheet = DataBindingUtil.bind(layout)
        playerScreenBottomSheet?.lifecycleOwner = this
        setFormatDataAdapter()
        playerScreenBottomSheet?.doneBtn?.setOnClickListener {
            dismiss()
        }

        playerScreenBottomSheet?.backClick?.setOnClickListener {
            dismiss()
        }

        return layout
    }

    private fun setFormatDataAdapter() {
        if (!Constants.dataFormatsMedia3.isNullOrEmpty()) {
            val adapter = FormatDataAdapterMedia3(requireActivity(), this)
            playerScreenBottomSheet?.channelRecycler?.layoutManager = LinearLayoutManager(context)
            playerScreenBottomSheet?.channelRecycler?.adapter = adapter
            adapter?.submitList(Constants.dataFormatsMedia3)
        }
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet: FrameLayout =

            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        val percent = sizeBottomSheet.toFloat() / 100
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * percent
        val percentheight = rect.height() * percent
        // Height of the view
        bottomSheet.layoutParams.height = percentheight.toInt()

        // Behavior of the bottom sheet
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.apply {
            peekHeight = resources.displayMetrics.heightPixels // Pop-up height
            state = BottomSheetBehavior.STATE_EXPANDED

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
    }

    //    @OptIn(UnstableApi::class)
    @OptIn(UnstableApi::class)
    override
    fun navigation(viewId: FormatDataMedia3, pos: Int) {
        val SUPPORTED_TRACK_TYPES = ImmutableList.of(
            C.TRACK_TYPE_VIDEO, C.TRACK_TYPE_AUDIO, C.TRACK_TYPE_TEXT, C.TRACK_TYPE_IMAGE
        )
        if (viewId.token == null) {
            val trackSelector = playerInstance.trackSelector as DefaultTrackSelector
            val newParameters = TrackSelectionParameters.Builder(/* context */).build()
            trackSelector.setParameters(newParameters)
        } else {

            var trackSelectionParameters: TrackSelectionParameters? = null
            ////
            val builder: TrackSelectionParameters.Builder? =
                playerInstance?.trackSelectionParameters?.buildUpon()
            builder?.clearOverridesOfType(
                SUPPORTED_TRACK_TYPES.get(
                    0
                )
            )

            val trackSelector = playerInstance.trackSelector as DefaultTrackSelector

            // Assuming you have the trackGroup and trackIndex of the selected format
            val overrides = viewId.trckGroup?.mediaTrackGroup?.let {
                TrackSelectionOverride(
                    it, pos - 1
                )
            }
            overrides?.let { builder?.setOverrideForType(it) }
            trackSelectionParameters = builder?.build()
            if (trackSelectionParameters != null) {
                trackSelector.setParameters(trackSelectionParameters)
            }
        }
        dismiss()
    }


}