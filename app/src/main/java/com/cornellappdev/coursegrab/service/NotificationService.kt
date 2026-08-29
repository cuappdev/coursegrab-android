package com.cornellappdev.coursegrab.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cornellappdev.coursegrab.R
import com.cornellappdev.coursegrab.models.CourseNotification
import com.cornellappdev.coursegrab.networking.CourseGrabRepository
import com.cornellappdev.coursegrab.ui.notification.NotificationModal
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var repository: CourseGrabRepository

    @Inject
    lateinit var json: Json

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if message contains a data payload.
        val payload = remoteMessage.data["message"]
        if (payload != null) {
            val courseInfo = try {
                json.decodeFromString<CourseNotification>(payload)
            } catch (e: SerializationException) {
                Log.w(TAG, "Unreadable notification payload", e)
                null
            }

            if (courseInfo != null) sendNotification(courseInfo)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {

        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Deprecated("Deprecated in Java")
    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String) {
        CoroutineScope(Dispatchers.Main).launch {
            repository.sendDeviceToken(token)
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param course FCM message body received.
     */
    private fun sendNotification(course: CourseNotification) {
        val intent = Intent(this, NotificationModal::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("courseDetails", course)
        val pendingIntent = PendingIntent.getActivity(
            this, 10032, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_coursegrab_icon)
            .setContentTitle(course.title)
            .setContentText(course.body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(course.body)
            )

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "CourseGrab Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(course.section.catalog_num, notificationBuilder.build())
    }

    private companion object {
        const val TAG = "NotificationService"
    }
}