package com.example.Weathalert.datalayer.entity.coverters

import androidx.room.TypeConverter
import com.example.Weathalert.datalayer.entity.Daily
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DailyConverter {

    companion object{
        @TypeConverter
        @JvmStatic
        fun fromAlertItemList(value: MutableList<Daily?>?): String? {
            val gson = Gson()
            val type = object : TypeToken<MutableList<Daily>>() {}.type
            return gson.toJson(value, type)
        }

        @TypeConverter
        @JvmStatic
        fun toAlertItemList(value: String?): MutableList<Daily?>? {
            if (value == null) {
                return null
            }
            val gson = Gson()
            val type = object : TypeToken<MutableList<Daily>>() {}.type
            return gson.fromJson(value, type)
        }
    }
}