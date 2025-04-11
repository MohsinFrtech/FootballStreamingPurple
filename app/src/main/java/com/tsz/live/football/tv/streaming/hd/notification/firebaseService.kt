package com.tsz.live.football.tv.streaming.hd.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tsz.live.football.tv.streaming.hd.R
import com.tsz.live.football.tv.streaming.hd.ui.activities.SplashActivity
import java.util.*

////Service class for handling push notifications.....
class FirebaseService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        Log.d("Newnotifications","msg"+p0.toString())
        generateNotification(p0)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    ////generate notification function....
    private fun generateNotification(remoteMessage: RemoteMessage) {

        val data = remoteMessage.data

        if (data["appname"].equals(applicationContext.packageName, true)) {
            val notifType = data["type"].toString()
            if (notifType.equals("PersonalNotification", ignoreCase = true)) {
                val image = data["image"].toString()
                if (image.isEmpty()) {
                    sendPersonalNotif(null, data)
                } else {
                    getBitmapAsyncAndDoWork(image, data)
                }
            }
        }

    }

    private fun getBitmapAsyncAndDoWork(
        imageUrl: String,
        data: Map<String, String>
    ) {
        val bitmap = arrayOf<Bitmap?>(null)
        Glide.with(applicationContext)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                ) {
                    bitmap[0] = resource
                    sendPersonalNotif(bitmap[0], data)
                }


                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    sendPersonalNotif(null, data)
                }
            })
    }

    /////if notification type is personal notification....
    private fun sendPersonalNotif(
        bitmap: Bitmap?,
        data: Map<String, String>
    ) {

        val title = data["title"].toString()
        val description = data["description"].toString()
        val url = data["url"].toString()
        val image_backend = data["image"].toString()
        val intent = if (url.contains("https://") || url.contains("http://")) {
            Intent("android.intent.action.VIEW", Uri.parse(url))
        } else {
            Intent(this, SplashActivity::class.java)
        }
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }
//        val pendingIntent =
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = "fcm_default_channel"
        val defaultSoundUri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val bigText =
            NotificationCompat.BigTextStyle()
        bigText.bigText(description)
        val notificationBuilder =
            NotificationCompat.Builder(this, channelId)
                .setStyle(bigText)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        if (bitmap != null) {
            notificationBuilder.setStyle(
                NotificationCompat.BigPictureStyle().bigPicture(bitmap)
            )
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "default channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        val integer: Long = Date().time / 1000L % Int.MAX_VALUE
        notificationManager.notify(
            integer.toInt(),
            notificationBuilder.build()
        )
    }
}