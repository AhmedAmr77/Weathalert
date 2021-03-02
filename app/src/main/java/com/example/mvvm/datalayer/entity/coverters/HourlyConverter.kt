package com.example.Weathalert.datalayer.entity.coverters

import androidx.room.TypeConverter
import com.example.Weathalert.datalayer.entity.Hourly
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HourlyConverter {

    companion object{
        @TypeConverter
        @JvmStatic
        fun fromAlertItemList(value: MutableList<Hourly?>?): String? {
            val gson = Gson()
            val type = object : TypeToken<MutableList<Hourly>>() {}.type
            return gson.toJson(value, type)
        }

        @TypeConverter
        @JvmStatic
        fun toAlertItemList(value: String?): MutableList<Hourly?>? {
            if (value == null) {
                return null
            }
            val gson = Gson()
            val type = object : TypeToken<MutableList<Hourly>>() {}.type
            return gson.fromJson(value, type)
        }
    }
}