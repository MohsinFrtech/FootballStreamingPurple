package com.tsz.live.football.tv.streaming.hd.models

import android.util.Log
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants
import se.simbio.encryption.Encryption

class ProcessingFile {
    fun encryptData(strToDecrypt: String?):String
    {
        val iv = ByteArray(Constants.checkSize)
        val encryption: Encryption = Encryption.getDefault(
            Constants.myUserLock1,
            Constants.valueper, iv)

        Log.d("TV Constants", Constants.myUserLock1 +"  ---  " +  Constants.valueper)

        return encryption.decryptOrNull(strToDecrypt)
    }
}