package com.kuba.dogwalk.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kuba.dogwalk.data.local.dog.Dog
import com.kuba.dogwalk.data.local.dog.DogDao
import com.kuba.dogwalk.data.local.myWalk.MyWalk
import com.kuba.dogwalk.data.local.myWalk.MyWalkDao
import com.kuba.dogwalk.data.local.notification.Notification
import com.kuba.dogwalk.data.local.notification.NotificationDao
import com.kuba.dogwalk.other.Converters

@Database(
    entities = [MyWalk::class, Notification::class, Dog::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class DogWalkDatabase : RoomDatabase() {
    abstract fun myWalkDao(): MyWalkDao
    abstract fun notificationDao(): NotificationDao
    abstract fun dogDao(): DogDao
}