package com.kuba.dogwalk.other

import android.graphics.Color

object Constants {

    const val DATABASE_NAME = "dog_walk.db"
    const val REQUEST_CODE_LOCATION_PERMISSION = 0
    const val REQUEST_CODE_STORAGE_IMAGE_PERMISSION = 0

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"
    const val ACTION_SHOW_WALK_LIST_FRAGMENT = "ACTION_SHOW_WALK_LIST_FRAGMENT"

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L

    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_SIZE = 8f
    const val MAP_ZOOM = 15f
    const val TIMER_UPDATE_INTERVAL = 50L

    const val TIME_FORMAT_HOUR_AND_MINUTES = "HH:mm"
    const val DATE_FORMAT_DAY_MONTH_YEAR = "dd.MM.yyyy"

    const val NOTIFICATION_TRACKING_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_TRACKING_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_TRACKING_ID = 1

    const val NOTIFICATION_REMINDER_CHANNEL_ID = "reminder_channel"
    const val NOTIFICATION_REMINDER_CHANNEL_NAME = "Reminder"
    const val NOTIFICATION_REMINDER_ID = 2

    const val START_TIME_STAMP = "startTimestamp"
    const val END_TIME_STAMP = "endTimestamp"
    const val MESSAGE = "message"
    const val MESSAGE_GO_TO_WALK = "Wyjdź na spacer"
    const val NOTIFICATION = "notification"
    const val REASON = "reason"
    const val REASON_CUSTOM_NOTIFICATION = 3

    const val TITLE = "title"
    const val TAB_WALK_NOTIFICATION = "Spacery"
    const val TAB_CUSTOM_NOTIFICATION = "Własne"
    const val HOUR_OF_START_CUSTOM_NOTIFICATION = 9L

    const val WALK_SAVE_SUCCESSFULLY_MESSAGE  = "Spacer został zapisany"
    const val FILL_ALL_FIELD_MESSAGE = "Uzupełnij wszystkie pola"
    const val CHANGE_VALUE_OF_GAP_FIELD_MESSAGE = "Przerwa miedzy spacerami powinna byc w zakresie 1-24"
    const val TOO_LONG_NAME_OF_NOTIFICATION = "Nazwa nie powinna przekraczać 30 znaków"
    const val NOTIFICATION_HAS_BEEN_SUCCESSFULLY_ADDED_MESSAGE = "Powiadomienie zostalo dodane"
    const val UNKNOWN_ERROR_MESSAGE = "Nieznany błąd"
    const val WALK_NOTIFICATION_TURN_ON_MESSAGE = "Powiadomienie zostało włączone"
    const val WALK_NOTIFICATION_TURN_OFF_MESSAGE = "Powiadomienie zostało wyłączone"
    const val MY_WALK_ITEM_DELETED_SUCCESSFULLY_MESSAGE = "Usunięto"
    const val PROFILE_SUCCESSFULLY_UPDATE = "Pomyślnie dodano"
    const val PROFILE_EMPTY_FIELD_MESSAGE = "Uzupełnij polę"
    const val PROFILE_TOO_LONG_TEXT_MESSAGE = "Zbyt długa nazwa"
    const val PROFILE_WEIGHT_EQUAL_TO_ZERO_MESSAGE = "Waga nie może wynosić 0"
    const val PROFILE_WEIGHT_LESS_THAN_ZERO_MESSAGE = "Waga nie może być mniejsza od 0"
    const val PROFILE_BIRTH_DATE_BIGGER_THAN_CURRENT_DATE_MESSAGE = "Błędna data urodzenia"
    const val NOTIFICATION_ITEM_DELETED_SUCCESSFULLY_MESSAGE = "Usunięto powiadomienie"
    const val PERMISSION_CAMERA_STORAGE_MESSAGE = "Aby dodać zdjęcie, musisz zaakceptować uprawnienia do przechowywania oraz kamery"
    const val PERMISSION_MESSAGE_LOCATION = "Aby korzystać z tej aplikacji, musisz zaakceptować uprawnienia do lokalizacji"

    const val DATA_STORAGE_TYPE = "image/*"
    const val IMAGE_PICK_CODE = 1000

    const val DEFAULT_FIRST_HOUR = "8:00"
    const val DEFAULT_LAST_HOUR = "22:00"


}
