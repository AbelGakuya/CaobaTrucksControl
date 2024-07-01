package com.example.caobatruckscontrol.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.caobatruckscontrol.R
import com.example.caobatruckscontrol.presentation.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "MyNotificationChannel"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            // Handle the data payload
            if (remoteMessage.data["type"] == "tripCreated") {
                val tripId = remoteMessage.data["tripId"]
                val tripMessage = "New trip created: $tripId"
                sendNotification("New Trip Created", tripMessage)
            }
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            // Create and show notification
            sendNotification(it.title ?: "Title", it.body ?: "Body")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        updateTokenToServer(token)
    }

    private fun updateTokenToServer(token: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(user.uid)

            // Update or set the FCM token under the user's document in Firestore
            userDocRef.update("deviceToken", token)
                .addOnSuccessListener {
                    Log.d(TAG, "FCM token updated successfully to Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error updating FCM token to Firestore", e)
                }
        } else {
            Log.e(TAG, "User is not logged in, cannot update FCM token")
        }
    }



    private fun sendNotification(
        title: String,
        messageBody: String
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // You can add extra data to the intent if needed
        // notificationIntent.putExtra("tripId", tripId)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.logo_1_png)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

}

