package com.tsz.live.football.tv.streaming.hd.utils.objects

import android.content.Context
import android.content.SharedPreferences
import com.tsz.live.football.tv.streaming.hd.R


class SharedPreference(context: Context?) {

    private var mPref: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null

    init {
        mPref = context?.getSharedPreferences(
            context.getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        mEditor = mPref?.edit()
    }

    fun saveString(key: String, value: String) {
        mEditor?.putString(key, value)
        mEditor?.commit()
    }

    fun getString(key: String):String? {
        return mPref?.getString(key, "")
    }

    fun saveBool(key: String, value: Boolean) {
        mEditor?.putBoolean(key, value)
        mEditor?.commit()
    }

    fun getBool(key: String):Boolean? {
       return mPref?.getBoolean(key, false)
    }

    fun getInt(key: String):Int? {
        return mPref?.getInt(key, 0)
    }
    fun saveInt(key: String,value: Int)
    {
        mEditor?.putInt(key, value)
        mEditor?.commit()
    }

    fun rateUs(key: String, value: Boolean) {
        mEditor?.putBoolean(key, value)
        mEditor?.commit()
    }

    fun getRateUsBool(key: String):Boolean? {
        return mPref?.getBoolean(key, false)
    }

    fun saveConsent(key: String, value: Boolean) {
        mEditor?.putBoolean(key, value)
        mEditor?.commit()
    }

    fun getConsentStatus(key: String):Boolean? {
        return mPref?.getBoolean(key, false)
    }

    fun saveNotificationPermission(key: String, value: Boolean) {
        mEditor?.putBoolean(key, value)
        mEditor?.commit()
    }

    fun getNotificationPermission(key: String):Boolean? {
        return mPref?.getBoolean(key, false)
    }

}