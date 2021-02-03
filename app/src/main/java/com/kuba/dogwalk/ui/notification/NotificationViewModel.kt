package com.kuba.dogwalk.ui.notification

import android.app.AlarmManager
import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuba.dogwalk.data.local.notification.Notification
import com.kuba.dogwalk.other.Constants.CHANGE_VALUE_OF_GAP_FIELD_MESSAGE
import com.kuba.dogwalk.other.Constants.FILL_ALL_FIELD_MESSAGE
import com.kuba.dogwalk.other.Constants.HOUR_OF_START_CUSTOM_NOTIFICATION
import com.kuba.dogwalk.other.Constants.MESSAGE_GO_TO_WALK
import com.kuba.dogwalk.other.Constants.NOTIFICATION_HAS_BEEN_SUCCESSFULLY_ADDED_MESSAGE
import com.kuba.dogwalk.other.Constants.NOTIFICATION_ITEM_DELETED_SUCCESSFULLY_MESSAGE
import com.kuba.dogwalk.other.Constants.TOO_LONG_NAME_OF_NOTIFICATION
import com.kuba.dogwalk.other.Constants.WALK_NOTIFICATION_TURN_OFF_MESSAGE
import com.kuba.dogwalk.other.Constants.WALK_NOTIFICATION_TURN_ON_MESSAGE
import com.kuba.dogwalk.other.Event
import com.kuba.dogwalk.other.NotificationUtility
import com.kuba.dogwalk.other.Resource
import com.kuba.dogwalk.repositories.DogWalkRepository
import kotlinx.coroutines.launch

class NotificationViewModel @ViewModelInject constructor(
    private val repository: DogWalkRepository
) : ViewModel() {

    val notifications = repository.observeCustomNotifications(true)

    val walkNotification = repository.observeWalkNotifications(false)

    private val _notificationStatus = MutableLiveData<Event<Resource<Notification>>>()
    val notificationStatus: LiveData<Event<Resource<Notification>>> =
        _notificationStatus


    fun deleteNotification(notification: Notification) = viewModelScope.launch {
        repository.deleteNotification(notification)
        _notificationStatus.postValue(
            Event(
                Resource.success(
                    NOTIFICATION_ITEM_DELETED_SUCCESSFULLY_MESSAGE,
                    notification
                )
            )
        )
    }

    private fun insertNotification(notification: Notification) = viewModelScope.launch {
        repository.insertNotification(notification)
    }

    fun updateWalkNotificationEnable(enabled: Boolean, id: Int, context: Context) =
        viewModelScope.launch {
            repository.updateWalkNotificationEnable(enabled, id)
            val notification = walkNotification.value
            if (!enabled) {
                _notificationStatus.postValue(
                    Event(
                        Resource.success(
                            WALK_NOTIFICATION_TURN_OFF_MESSAGE,
                            notification
                        )
                    )
                )
            }
            turnOnOrTurnOffWalkNotificationService(
                notification?.firstHours!! + AlarmManager.INTERVAL_HOUR,
                notification.lastHours!! + AlarmManager.INTERVAL_HOUR,
                notification.gap!!,
                context,
                enabled
            )
        }

    fun updateCustomNotificationEnable(enabled: Boolean, id: Int, context: Context) =
        viewModelScope.launch {
            repository.updateCustomNotificationEnable(enabled, id)
            val notification =
                notifications.value?.filter { notification -> notification.id == id }!![0]

            if (enabled) {
                _notificationStatus.postValue(
                    Event(
                        Resource.success(
                            WALK_NOTIFICATION_TURN_ON_MESSAGE,
                            notification
                        )
                    )
                )
            } else {
                _notificationStatus.postValue(
                    Event(
                        Resource.success(
                            WALK_NOTIFICATION_TURN_OFF_MESSAGE,
                            notification
                        )
                    )
                )

            }

            turnOnOrTurnOffCustomNotificationService(
                notification.date!!,
                notification.message!!,
                id,
                enabled,
                context
            )
        }

    private fun insertNotificationIntoDb(notification: Notification) = viewModelScope.launch {
        insertNotification(notification)
    }

    fun upsertWalkNotification(
        isEnabled: Boolean,
        firstHour: Long?,
        lastHour: Long?,
        gap: Int?,
        context: Context
    ) {
        if (firstHour == null || lastHour == null || gap == null) {
            _notificationStatus.postValue(
                Event(
                    Resource.error(
                        FILL_ALL_FIELD_MESSAGE,
                        null
                    )
                )
            )
            return
        }
        if (gap > 24 || gap <= 0) {
            _notificationStatus.postValue(
                Event(
                    Resource.error(
                        CHANGE_VALUE_OF_GAP_FIELD_MESSAGE,
                        null
                    )
                )
            )
            return
        }

        val notification = Notification(
            isEnabled,
            firstHours = firstHour,
            lastHours = lastHour,
            gap = gap.toInt(),
            message = MESSAGE_GO_TO_WALK,
            isCustom = false,
            id = 0
        )
        insertNotificationIntoDb(notification)

        turnOnOrTurnOffWalkNotificationService(
            firstHour + AlarmManager.INTERVAL_HOUR,
            lastHour + AlarmManager.INTERVAL_HOUR,
            gap,
            context,
            isEnabled
        )
        _notificationStatus.postValue(
            Event(
                Resource.success(
                    WALK_NOTIFICATION_TURN_ON_MESSAGE,
                    notification
                )
            )
        )

    }

    fun insertCustomNotification(
        date: Long?,
        message: String?,
        custom: Boolean = true,
        isEnabled: Boolean = true,
        context: Context,
    ): Boolean {
        if (message.isNullOrEmpty() || date == null) {
            _notificationStatus.postValue(
                Event(
                    Resource.error(
                        FILL_ALL_FIELD_MESSAGE,
                        null
                    )
                )
            )
            return false
        }

        if (message.length > 50) {
            _notificationStatus.postValue(
                Event(
                    Resource.error(
                        TOO_LONG_NAME_OF_NOTIFICATION,
                        null
                    )
                )
            )
            return false
        }

        val notification = Notification(
            isEnabled,
            firstHours = HOUR_OF_START_CUSTOM_NOTIFICATION,
            date = date,
            message = message,
            isCustom = custom,
        )
        insertNotificationIntoDb(notification)

        turnOnOrTurnOffCustomNotificationService(
            date!!,
            message,
            (if (!notifications.value.isNullOrEmpty()) notifications.value?.last()?.id?.plus(1) else 1)
                ?: 1,
            isEnabled,
            context
        )
        _notificationStatus.postValue(
            Event(
                Resource.success(
                    NOTIFICATION_HAS_BEEN_SUCCESSFULLY_ADDED_MESSAGE,
                    notification
                )
            )
        )
        return true
    }

    private fun turnOnOrTurnOffWalkNotificationService(
        startTimeInMillis: Long,
        endTimeInMillis: Long,
        gap: Int,
        context: Context,
        turnOn: Boolean,
    ) {
        NotificationUtility().setWalkNotifications(
            startTimeInMillis,
            endTimeInMillis,
            gap,
            context,
            turnOn
        )
    }

    private fun turnOnOrTurnOffCustomNotificationService(
        date: Long,
        message: String,
        id: Int,
        turnOn: Boolean,
        context: Context
    ) {
        NotificationUtility().setCustomNotification(
            date,
            message,
            id,
            turnOn,
            context
        )
    }
}