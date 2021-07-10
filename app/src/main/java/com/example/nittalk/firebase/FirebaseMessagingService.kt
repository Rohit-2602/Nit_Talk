package com.example.nittalk.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.nittalk.R
import com.example.nittalk.ui.auth.AuthActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class FirebaseMessagingService: FirebaseMessagingService() {

    private lateinit var notificationManager: NotificationManagerCompat

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        FirebaseUtil().updateToken(token)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        val authIntent = Intent(this, AuthActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, authIntent, PendingIntent.FLAG_ONE_SHOT)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("CHANNEL_ID_1", "Channel1", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Description of Channel"

            manager.createNotificationChannel(channel)

        }

        notificationManager = NotificationManagerCompat.from(this)
        val notification = NotificationCompat.Builder(this, "CHANNEL_ID_1")
            .setSmallIcon(R.drawable.bulbasaur)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)
            .setContentText(message)
            .build()

        val notificationId = Random().nextInt(1000000)

        notificationManager.notify(notificationId + 1, notification)

    }

}