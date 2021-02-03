package com.kuba.dogwalk.data.local.notification

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification")
data class Notification(
    var enabled: Boolean = false,
    var firstHours: Long? = 0L,
    var lastHours: Long? = 0L,
    val gap: Int? = null,
    var date: Long? = 0L,
    var message: String? = null,
    var isCustom: Boolean = false,
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null
)