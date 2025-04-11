package com.tsz.live.football.tv.streaming.hd.utils.playerUtils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

class SeekBarClass : androidx.appcompat.widget.AppCompatSeekBar {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        performClick()
        return true
    }


    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

}