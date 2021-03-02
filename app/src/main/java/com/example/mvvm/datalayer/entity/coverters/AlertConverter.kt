package com.example.Weathalert.datalayer.entity.coverters

import androidx.room.TypeConverter
import com.example.Weathalert.datalayer.entity.Alert
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

/*
// example converter for java.util.Date
 public static class Converters {
   @TypeConverter
   public Date fromTimestamp(Long value) {
     return value == null ? null : new Date(value);
   }

   @TypeConverter
   public Long dateToTimestamp(Date date) {
     if (date == null) {
       return null;
     } else {
       return date.getTime();
     }
   }
 */