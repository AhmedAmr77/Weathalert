package com.example.Weathalert.datalayer.entity.coverters

import androidx.room.TypeConverter
import com.example.mvvm.datalayer.entity.weather.Weather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherConverter {

    companion object{
        @TypeConverter
        @JvmStatic
        fun fromAlertItemList(value: MutableList<Weather?>?): String? {
            val gson = Gson()
            val type = object : TypeToken<MutableList<Weather>>() {}.type
            return gson.toJson(value, type)
        }

        @TypeConverter
        @JvmStatic
        fun toAlertItemList(value: String?): MutableList<Weather?>? {
            if (value == null) {
                return null
            }
            val gson = Gson()
            val type = object : TypeToken<MutableList<Weather>>() {}.type
            return gson.fromJson(value, type)
        }
    }
}