package com.tsz.live.football.tv.streaming.hd.utils.objects

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import com.tsz.live.football.tv.streaming.hd.BuildConfig
import com.tsz.live.football.tv.streaming.hd.R


object DebugChecker {

    fun checkDebugging(context: Context): Boolean{


        if (!BuildConfig.DEBUG){
            if (Settings.Secure.getInt(
                context?.contentResolver,
                Settings.Global.ADB_ENABLED,
                0
            ) == 1
                ){
                showDialog(context)
                return true
            }
        }
        return false
    }

    private fun showDialog(context: Context?) {
        context?.let {
            androidx.appcompat.app.AlertDialog.Builder(it)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Error!")
                .setCancelable(false)
                .setMessage(context.getString(R.string.usbDebugging))
                .setPositiveButton(
                    "Goto Disable"
                ) { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                        val componentName = intent.resolveActivity(context.packageManager)
                        if (componentName == null) {
                            Toast.makeText(
                                context, "No Activity to handle Intent action",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            context.startActivity(intent)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .show()
        }
    }

}