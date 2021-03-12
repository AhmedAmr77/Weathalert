package com.example.Weathalert.datalayer.entity.coverters

import androidx.room.TypeConverter
import com.example.mvvm.datalayer.entity.weather.Alert
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AlertConverter {

    companion object{
        @TypeConverter
        @JvmStatic
        fun fromAlertItemList(value: MutableList<Alert?>?): String? {
            val gson = Gson()
            val type = object : TypeToken<MutableList<Alert>>() {}.type
            return gson.toJson(value, type)
        }

        @TypeConverter
        @JvmStatic
        fun toAlertItemList(value: String?): MutableList<Alert?>? {
            if (value == null) {
                return null
            }
            val gson = Gson()
            val type = object : TypeToken<MutableList<Alert>>() {}.type
            return gson.fromJson(value, type)
        }
    }
}