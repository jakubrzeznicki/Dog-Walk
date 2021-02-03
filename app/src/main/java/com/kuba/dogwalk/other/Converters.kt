package com.kuba.dogwalk.other

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.kuba.dogwalk.data.local.dog.Activity
import com.kuba.dogwalk.data.local.dog.Gender
import com.kuba.dogwalk.data.local.dog.Goal
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class Converters {

    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @TypeConverter
    fun fromBitmap(bmp: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toStringGender(gender: Gender): String {
        return if (gender == Gender.BITCH) Gender.BITCH.toString() else Gender.DOG.toString()
    }

    @TypeConverter
    fun fromStringGender(genderString: String): Gender {
        return if (genderString == Gender.BITCH.toString()) Gender.BITCH else Gender.DOG

    }

    @TypeConverter
    fun toStringActivity(activity: Activity): String {
        return when (activity) {
            Activity.MEDIUM -> Activity.MEDIUM.toString()
            Activity.LOW -> Activity.LOW.toString()
            else -> Activity.HIGH.toString()
        }
    }

    @TypeConverter
    fun fromStringActivity(activityString: String): Activity {
        return when (activityString) {
            Activity.MEDIUM.toString() -> Activity.MEDIUM
            Activity.LOW.toString() -> Activity.LOW
            else -> Activity.HIGH
        }
    }

    @TypeConverter
    fun toStringGoal(goal: Goal): String {
        return when (goal) {
            Goal.LOSS -> Goal.LOSS.toString()
            Goal.HOLD -> Goal.HOLD.toString()
            else -> Goal.GAIN.toString()
        }
    }

    @TypeConverter
    fun fromStringGoal(goalString: String): Goal {
        return when (goalString) {
            Goal.LOSS.toString() -> Goal.LOSS
            Goal.HOLD.toString() -> Goal.HOLD
            else -> Goal.GAIN
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertStringDateIntoTimeInMillis(timeAsString: String): Long {
        val dateFormatter = SimpleDateFormat(Constants.TIME_FORMAT_HOUR_AND_MINUTES)
        val time = dateFormatter.parse(timeAsString)
        return time.time
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTimeInMillisIntoStringDate(timeInMilli: Long): String {
        val dateFormatter = SimpleDateFormat(Constants.DATE_FORMAT_DAY_MONTH_YEAR)
        val date = Date().apply {
            time = timeInMilli
        }
        return dateFormatter.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun convertTimeInMillisIntoStringTime(timeInMilli: Long): String {
        val dateFormatter = SimpleDateFormat(Constants.TIME_FORMAT_HOUR_AND_MINUTES)
        val date = Date().apply {
            time = timeInMilli
        }
        return dateFormatter.format(date)
    }
}