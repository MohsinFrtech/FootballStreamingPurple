package com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces

import com.tsz.live.football.tv.streaming.hd.models.FormatDataAudioMedia3

////This interface is for controlling navigation between fragments
interface FormatSelectionAudioMedia3 {
    fun navigation(viewId: FormatDataAudioMedia3, pos:Int, countryCode:String?)
}