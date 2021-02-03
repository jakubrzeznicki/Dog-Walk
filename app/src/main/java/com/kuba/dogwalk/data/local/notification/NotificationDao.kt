package com.kuba.dogwalk.data.local.notification

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: Notification)

    @Delete
    suspend fun delete(notification: Notification)

    @Query("SELECT * FROM notification WHERE isCustom=:custom ORDER BY id DESC")
    fun observeCustomNotifications(custom: Boolean): LiveData<List<Notification>>

    @Query("SELECT * FROM notification WHERE isCustom=:custom")
    fun observeWalkNotifications(custom: Boolean): LiveData<Notification>

    @Query("UPDATE notification SET enabled=:enabled WHERE id=:id")
    suspend fun updateWalkNotificationEnable(enabled: Boolean, id: Int)

    @Query("UPDATE notification SET enabled=:enabled WHERE id=:id")
    suspend fun updateCustomNotificationEnable(enabled: Boolean, id: Int)
}