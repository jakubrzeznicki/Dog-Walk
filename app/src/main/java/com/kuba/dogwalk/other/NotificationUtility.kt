package com.kuba.dogwalk.other

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.kuba.dogwalk.other.Constants.END_TIME_STAMP
import com.kuba.dogwalk.other.Constants.MESSAGE
import com.kuba.dogwalk.other.Constants.MESSAGE_GO_TO_WALK
import com.kuba.dogwalk.other.Constants.REASON
import com.kuba.dogwalk.other.Constants.REASON_CUSTOM_NOTIFICATION
import com.kuba.dogwalk.other.Constants.START_TIME_STAMP
import timber.log.Timber
import java.util.*


class NotificationUtility {

    fun setWalkNotifications(
        startTimeInMillis: Long,
        endTimeInMillis: Long,
        gap: Int,
        context: Context,
        turnOn: Boolean,
    ) {
        val alarmManager = context.getSystemService(Activity.ALARM_SERVICE) as AlarmManager

        val startHours = (startTimeInMillis / 1000 / 3600).toInt()
        val startMinutes = (startTimeInMillis / 1000 / 60 % 60).toInt()
        val endHours = (endTimeInMillis / 1000 / 3600).toInt()
        val endMinutes = (endTimeInMillis / 1000 / 60 % 60).toInt()

        val calendarStart = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, startHours)
            set(Calendar.MINUTE, startMinutes)
            set(Calendar.SECOND, 0)
        }

        val calendarEnd = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, endHours)
            set(Calendar.MINUTE, endMinutes)
            set(Calendar.SECOND, 0)
        }

        val alarmIntentRepeating = Intent(
            context.applicationContext,
            AlarmReceiver::class.java
        ).apply {
            putExtra(REASON, 0)
            putExtra(START_TIME_STAMP, calendarStart.timeInMillis)
            putExtra(END_TIME_STAMP, calendarEnd.timeInMillis)
            putExtra(MESSAGE, MESSAGE_GO_TO_WALK)
        }

        val alarmIntentStart = Intent(
            context.applicationContext,
            AlarmReceiver::class.java
        ).apply {
            putExtra(REASON, 1)
            putExtra(START_TIME_STAMP, calendarStart.timeInMillis)
            putExtra(END_TIME_STAMP, calendarEnd.timeInMillis)
            putExtra(MESSAGE, MESSAGE_GO_TO_WALK)
        }

        val alarmIntentEnd = Intent(
            context.applicationContext,
            AlarmReceiver::class.java
        ).apply {
            putExtra(REASON, 2)
            putExtra(START_TIME_STAMP, calendarStart.timeInMillis)
            putExtra(END_TIME_STAMP, calendarEnd.timeInMillis)
            putExtra(MESSAGE, MESSAGE_GO_TO_WALK)
        }


        val pendingIntentRepeating = PendingIntent.getBroadcast(
            context,
            0,
            alarmIntentRepeating,
            PendingIntent.FLAG_UPDATE_CURRENT

        )
        val pendingIntentStart = PendingIntent.getBroadcast(
            context,
            1,
            alarmIntentStart,
            PendingIntent.FLAG_UPDATE_CURRENT

        )
        val pendingIntentEnd = PendingIntent.getBroadcast(
            context,
            2,
            alarmIntentEnd,
            PendingIntent.FLAG_UPDATE_CURRENT

        )
        startOrCancelRepeatingAlarm(
            alarmManager,
            calendarStart,
            calendarEnd,
            pendingIntentRepeating,
            gap,
            turnOn
        )
        setOrCancelStartAlarm(alarmManager, calendarStart, pendingIntentStart, turnOn)
        setOrCancelEndAlarm(alarmManager, calendarEnd, pendingIntentEnd, turnOn)
    }

    private fun startOrCancelRepeatingAlarm(
        alarmManager: AlarmManager,
        calendarStart: Calendar,
        calendarEnd: Calendar,
        pendingIntent: PendingIntent,
        gap: Int,
        turnOn: Boolean
    ) {
        val calendarCurrent = Calendar.getInstance()
        var newStartTime = calendarStart.timeInMillis

        alarmManager.cancel(pendingIntent)
        if (turnOn) {
            if (calendarEnd.after(calendarCurrent) && calendarStart.after(calendarCurrent)) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarStart.timeInMillis + AlarmManager.INTERVAL_HOUR * gap.toLong(),
                    AlarmManager.INTERVAL_HOUR * gap.toLong(),
                    pendingIntent
                )
            } else if (calendarStart.before(calendarCurrent) && calendarEnd.after(calendarCurrent)) {
                while (newStartTime < calendarCurrent.timeInMillis) {
                    newStartTime += AlarmManager.INTERVAL_HOUR * gap.toLong()
                }

                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    newStartTime,
                    AlarmManager.INTERVAL_HOUR * gap.toLong(),
                    pendingIntent
                )
            } else {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    (calendarStart.timeInMillis + AlarmManager.INTERVAL_HOUR * gap.toLong()) + AlarmManager.INTERVAL_DAY,
                    AlarmManager.INTERVAL_HOUR * gap.toLong(),
                    pendingIntent
                )
            }
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun setOrCancelStartAlarm(
        alarmManager: AlarmManager,
        calendarStart: Calendar,
        pendingIntent: PendingIntent,
        turnOn: Boolean
    ) {
        val calendarCurrent = Calendar.getInstance()
        alarmManager.cancel(pendingIntent)
        if (turnOn) {
            if (calendarStart.after(calendarCurrent)) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarStart.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarStart.timeInMillis + AlarmManager.INTERVAL_DAY,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun setOrCancelEndAlarm(
        alarmManager: AlarmManager,
        calendarEnd: Calendar,
        pendingIntent: PendingIntent,
        turnOn: Boolean
    ) {
        val calendarCurrent = Calendar.getInstance()
        alarmManager.cancel(pendingIntent)
        if (turnOn) {
            if (calendarEnd.after(calendarCurrent)) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarEnd.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendarEnd.timeInMillis + AlarmManager.INTERVAL_DAY,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun setCustomNotification(
        date: Long,
        message: String,
        id: Int,
        turnOn: Boolean,
        context: Context
    ) {
        val alarmManager = context.getSystemService(Activity.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val alarmIntent = Intent(
            context.applicationContext,
            AlarmReceiver::class.java
        ).apply {
            putExtra(REASON, REASON_CUSTOM_NOTIFICATION)
            putExtra(START_TIME_STAMP, calendar.timeInMillis)
            putExtra(MESSAGE, message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id + 100,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        turnOnOrTurnOffCustomAlarm(
            alarmManager, calendar, pendingIntent, turnOn
        )
    }

    private fun turnOnOrTurnOffCustomAlarm(
        alarmManager: AlarmManager,
        calendar: Calendar,
        pendingIntent: PendingIntent?,
        turnOn: Boolean
    ) {
        if (turnOn) {
            val calendarCurrent = Calendar.getInstance()
            alarmManager.cancel(pendingIntent)
            if (calendar.after(calendarCurrent)) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        } else {
            alarmManager.cancel(pendingIntent)
        }
    }
}