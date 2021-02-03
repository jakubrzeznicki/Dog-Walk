package com.kuba.dogwalk.data.local.dog

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "dog"
)
data class Dog(
    var name: String? = "",
    var breed: String? = "",
    var gender: Gender? = Gender.DOG,
    var birthDate: Long? = 0L,
    var weight: Double? = 0.0,
    var photo: String? = null,
    var goal: Goal? = Goal.HOLD,
    var activity: Activity? = Activity.MEDIUM,
    var calories: Int? = 0,
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null
)

enum class Gender {
    DOG,
    BITCH
}

enum class Goal {
    LOSS,
    HOLD,
    GAIN
}

enum class Activity {
    LOW,
    MEDIUM,
    HIGH
}