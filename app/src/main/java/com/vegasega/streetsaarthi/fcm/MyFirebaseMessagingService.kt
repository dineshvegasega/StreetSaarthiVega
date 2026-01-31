package com.vegasega.streetsaarthi.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.vegasega.streetsaarthi.R
import com.vegasega.streetsaarthi.datastore.DataStoreKeys.LOGIN_DATA
import com.vegasega.streetsaarthi.datastore.DataStoreUtil.readData
import com.vegasega.streetsaarthi.models.Login
import com.vegasega.streetsaarthi.screens.mainActivity.MainActivity
import com.vegasega.streetsaarthi.networking.Main
import com.vegasega.streetsaarthi.networking.Screen
import com.vegasega.streetsaarthi.utils.getChannelName
import com.vegasega.streetsaarthi.utils.getNotificationId
import com.vegasega.streetsaarthi.utils.getTitle
import com.vegasega.streetsaarthi.utils.isAppIsInBackground
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

//        Log.e("TAG", "onMessageReceived: " + remoteMessage.getFrom());
        Log.e("TAG", "onMessageReceived: Noti" + remoteMessage.getNotification());
        Log.e("TAG", "onMessageReceived: Data" + remoteMessage.getData());
          Log.e("TAG", "isAppIsInBackground()" +  isAppIsInBackground());

        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {
                val _id = Gson().fromJson(loginUser, Login::class.java).id
                val json: JSONObject = JSONObject((remoteMessage.data as Map<*, *>?)!!)
                if(json.getString("user_id") == ""+_id){
                    val notiId = json.getString("type").getNotificationId()
                    when(notiId){
                        1 -> noti(json, notiId)
                        2 -> noti(json, notiId)
                        3 -> noti(json, notiId)
                        4 -> noti(json, notiId)
                        5 -> noti(json, notiId)
                        6 -> noti(json, notiId)
                        7 -> noti(json, notiId)
                        8 -> noti(json, notiId)
                    }
                }
            }
        }
    }


    @SuppressLint("MutableImplicitPendingIntent")
    fun noti(json: JSONObject, notiId: Int) {
        val chName = json.getString("type").getChannelName()
        val intent = Intent(this, MainActivity::class.java).putExtras(Bundle().apply {
            if (json.getString("type") == "Vendor Details" || json.getString("type") == "VendorDetails") {
                putString("key", "profile")
            } else if (json.getString("title").contains("Feedback") || json.getString("title")
                    .contains("Complaint")
            ) {
                putString("key", "feedback")
            } else {
                putString("key", json.getString("type"))
            }

            putString("_id", if (json.getString("type_id") != null) json.getString("type_id") else "")
            putString(Screen, Main)
        })
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            PendingIntent.getActivity(
                this,
                notiId,
                intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this,
                notiId,
                intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                this,
                notiId,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        }




        val importance = NotificationManager.IMPORTANCE_HIGH
        var mChannel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = NotificationChannel(chName, chName, importance)
            mChannel.enableLights(true)
            mChannel.lightColor = Color.WHITE
            mChannel.setShowBadge(true)
            mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }


        val notification = NotificationCompat.Builder(this, chName)
            .setSmallIcon(R.mipmap.ic_launcher) //.setLargeIcon(icon)
            .setPriority(Notification.PRIORITY_HIGH)
            .setContentTitle(getString(R.string.app_name))
            .setStyle(NotificationCompat.BigTextStyle().bigText(applicationContext.getTitle(json.getString("type"), json.getString("title"))))
            .setAutoCancel(true)
            .setChannelId(chName)
            .setContentIntent(pendingIntent)
            .setContentText(applicationContext.getTitle(json.getString("type"), json.getString("title"))).build()
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationManager.createNotificationChannel(
            mChannel!!
        )
        notificationManager.notify(notiId, notification)
    }




}