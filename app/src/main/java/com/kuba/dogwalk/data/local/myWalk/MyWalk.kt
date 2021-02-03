package com.kuba.dogwalk.data.local.myWalk

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_walk_item")
data class MyWalk(
    var photo: Bitmap? = null,
    var distance: Int = 0,
    var time: Long = 0L,
    var timestamp: Long = 0L,
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
)