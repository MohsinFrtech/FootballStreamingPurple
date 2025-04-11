package com.tsz.live.football.tv.streaming.hd.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.mListener
import com.tsz.live.football.tv.streaming.hd.utils.objects.interfaces.OnHomePressedListener
import java.lang.Exception

class HomeWatcher(val context: Context?){

    val TAG = "hg"
    private var mContext: Context? = context
    private var mFilter: IntentFilter? = null
//    private var mListener: OnHomePressedListener? = null
    private var mReceiver: InnerReceiver? = null

    init {
//        val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
//        intent.action = context?.packageName
        mFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
    }


    fun setOnHomePressedListener(listener: OnHomePressedListener?) {
        try {
            mListener = listener
            mReceiver = InnerReceiver()
        }
        catch (e:Exception){
            Log.d("Exception","msg")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startWatch() {
        if (mReceiver != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mContext?.registerReceiver(mReceiver, mFilter,Context.RECEIVER_EXPORTED)
            }else
            {
                mContext?.registerReceiver(mReceiver, mFilter,0)
            }
        }
    }

    fun stopWatch() {
        if (mReceiver != null) {
            mContext?.unregisterReceiver(mReceiver)
        }
    }

    internal class InnerReceiver : BroadcastReceiver() {
        val SYSTEM_DIALOG_REASON_KEY = "reason"
        val SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions"
        val SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps"
        val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"
        override fun onReceive(context: Context?, intent: Intent) {
            try {
                val action: String? = intent.action
                if (action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                    val reason: String = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)!!
                    if (reason != null) {
                        Log.d("HomePressedfd", "home  "+reason.toString())

//                    Log.e(TAG, "action:$action,reason:$reason")
                        if (mListener != null) {
                            if (reason == SYSTEM_DIALOG_REASON_HOME_KEY) {

                                mListener!!.onHomePressed()
                            } else if (reason == SYSTEM_DIALOG_REASON_RECENT_APPS) {
                                mListener!!.onHomeLongPressed()
                            }
                            else if (reason == "fs_gesture") {
                                mListener!!.onHomePressed()
                            }
                        }
                    }
                }
            }
            catch (e:Exception){
                Log.d("Exception","msg")
            }

        }
    }
}