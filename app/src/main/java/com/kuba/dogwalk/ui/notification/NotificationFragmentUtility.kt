package com.kuba.dogwalk.ui.notification

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import com.google.android.material.textview.MaterialTextView
import com.kuba.dogwalk.other.Constants.DATE_FORMAT_DAY_MONTH_YEAR
import com.kuba.dogwalk.other.Constants.TIME_FORMAT_HOUR_AND_MINUTES
import java.text.SimpleDateFormat
import java.util.*

class NotificationFragmentUtility {


    @SuppressLint("SimpleDateFormat")
    fun setHourOfWalkDialog(context: Context, textView: MaterialTextView) {
        textView.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                textView.text = SimpleDateFormat(TIME_FORMAT_HOUR_AND_MINUTES).format(cal.time)

            }
            TimePickerDialog(
                context,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun setDatePickerDialog(context: Context, textView: MaterialTextView) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val sdf = SimpleDateFormat(DATE_FORMAT_DAY_MONTH_YEAR)
                val date = sdf.parse("$dayOfMonth.${month+1}.$year")
                textView.text = SimpleDateFormat(DATE_FORMAT_DAY_MONTH_YEAR).format(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}