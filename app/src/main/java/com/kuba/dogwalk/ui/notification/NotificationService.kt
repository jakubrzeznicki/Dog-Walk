package com.kuba.dogwalk.ui.notification

import android.annotation.SuppressLint
import android.app.*
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kuba.dogwalk.R
import com.kuba.dogwalk.other.Constants
import com.kuba.dogwalk.other.Constants.MESSAGE
import com.kuba.dogwalk.other.Constants.NOTIFICATION
import com.kuba.dogwalk.other.Constants.NOTIFICATION_REMINDER_CHANNEL_ID
import com.kuba.dogwalk.other.Constants.NOTIFICATION_REMINDER_CHANNEL_NAME
import com.kuba.dogwalk.other.Constants.NOTIFICATION_REMINDER_ID
import com.kuba.dogwalk.other.Constants.START_TIME_STAMP
import com.kuba.dogwalk.other.Constants.TITLE
import com.kuba.dogwalk.ui.MainActivity
import java.util.*

class NotificationService : IntentService(NOTIFICATION_SERVICE) {

    private lateinit var baseNotificationBuilder: NotificationCompat.Builder

    override fun onHandleIntent(intent: Intent?) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(notificationManager)
        }

        var timestamp = 0L
        var message = ""
        if (intent != null && intent.extras != null) {
            timestamp = intent.extras!!.getLong(START_TIME_STAMP)
            message = intent.extras!!.getString(MESSAGE)!!
        }

        if (timestamp > 0) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp

            val notifyIntent = Intent(this, MainActivity::class.java)
            notifyIntent.apply {
                putExtra(TITLE, getString(R.string.sample_notification))
                putExtra(NOTIFICATION, true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                action = Constants.ACTION_SHOW_WALK_LIST_FRAGMENT
            }

            val pendingIntent = PendingIntent.getActivity(
                this.applicationContext,
                0,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                baseNotificationBuilder =
                    NotificationCompat.Builder(this, NOTIFICATION_REMINDER_CHANNEL_ID)
                        .setAutoCancel(true)
                        .setOngoing(false)
                        .setSmallIcon(R.drawable.ic_pet)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(message)
                        .setContentIntent(pendingIntent)

                notificationManager.notify(
                    NOTIFICATION_REMINDER_ID,
                    baseNotificationBuilder.build()
                )
            }
        }
    }

    @SuppressLint("NewApi")
    private fun createChannel(notificationManager: NotificationManager) {

        val notificationChannel = NotificationChannel(
            NOTIFICATION_REMINDER_CHANNEL_ID,
            NOTIFICATION_REMINDER_CHANNEL_NAME,
            IMPORTANCE_HIGH
        )

        notificationChannel.apply {
            enableVibration(true)
            setShowBadge(true)
            enableLights(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannel(notificationChannel)

    }
}