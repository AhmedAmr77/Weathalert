package com.example.mvvm.utils

object Constants {

    const val BASE_URL = "https://api.openweathermap.org"
    const val APP_ID = "fa90ecd453772250052b7116b8472152"
    const val EXECLUDE = "minutely"
    const val FIRST_USE = "FIRST_USE"
    var ENABLE_LOCATION = "ENABLE_LOCATION"

    const val SHARED_PREF = "MyShared"
    const val LONGITUDE = "LONGITUDE"
    const val LATITUDE = "LATITUDE"
    const val FAV_LONGITUDE = "FAV_LONGITUDE"
    const val FAV_LATITUDE = "FAV_LATITUDE"

    //    const val SHARED_PREF_SETTINGS = "SETTINGS"
    const val LANGUAGE_SETTINGS = "en"
    const val UNIT_SETTINGS = "standard"
//    const val STANDARD_SETTINGS = "STANDARD"
//    const val METRIC_SETTINGS = "METRIC"
//    const val IMPERIAL_SETTINGS = "IMPERIAL"
//    const val CURRENT_LANGAUGE = "en"

    const val MAPBOX_API_KEY =
        "pk.eyJ1IjoiYWhtZC1hbXI3IiwiYSI6ImNrbHY5bHNlNzJudWwycW4xZWZ4ZnpwdmUifQ.i_uRDlSBTg9n27BabRXWKQ"
    const val AUTOCOMPLETE_FRAGMENT_TAG = "autoCompleteFragment"

    const val ALARM_ID: String = "alarmId"
    const val NOTIFICATION_CHANNEL_ID = "notifyId"
    const val NOTIFICATION_CHANNEL_NAME = "WeatherAlarmChannel"
    const val HOUR_24_IN_SECONDS = 86400000
    const val HOUR_48_IN_SECONDS = 172800000
}