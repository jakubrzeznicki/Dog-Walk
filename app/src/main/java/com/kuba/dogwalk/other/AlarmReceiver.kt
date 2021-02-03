package com.kuba.dogwalk.other


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kuba.dogwalk.other.Constants.END_TIME_STAMP
import com.kuba.dogwalk.other.Constants.MESSAGE
import com.kuba.dogwalk.other.Constants.REASON
import com.kuba.dogwalk.other.Constants.START_TIME_STAMP
import com.kuba.dogwalk.ui.notification.NotificationService
import java.util.*


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val service = Intent(context, NotificationService::class.java)
        service.putExtra(START_TIME_STAMP, intent.getLongExtra(START_TIME_STAMP, 0))
        service.putExtra(MESSAGE, intent.getStringExtra(MESSAGE))

        val currentTime = Calendar.getInstance()
        val startTime = Calendar.getInstance().apply {
            timeInMillis = intent.getLongExtra(START_TIME_STAMP, currentTime.timeInMillis)
        }
        val endTime = Calendar.getInstance().apply {
            timeInMillis = intent.getLongExtra(END_TIME_STAMP, currentTime.timeInMillis)
        }

        when (intent.getIntExtra(REASON, 0)) {
            0 -> if (startTime.before(currentTime) && endTime.after(currentTime)) {
                context.startService(service)
            }
            1 -> {
                context.startService(service)
            }
            else -> {
                context.startService(service)
            }
        }
    }
}