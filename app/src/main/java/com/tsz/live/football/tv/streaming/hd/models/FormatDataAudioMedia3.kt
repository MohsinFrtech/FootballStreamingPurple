package com.tsz.live.football.tv.streaming.hd.models

import androidx.media3.common.Format
import androidx.media3.common.Tracks


data class FormatDataAudioMedia3(var token: Format?=null,
                                 var trckGroup: Tracks.Group?=null,
                                 var checkUncheck:Boolean=false
)