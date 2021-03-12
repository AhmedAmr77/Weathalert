package com.example.Weathalert.datalayer.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mvvm.datalayer.entity.weather.WeatherData
import com.example.Weathalert.datalayer.entity.coverters.AlertConverter
import com.example.Weathalert.datalayer.entity.coverters.DailyConverter
import com.example.Weathalert.datalayer.entity.coverters.HourlyConverter

@Database(entities = [WeatherData::class], version = 1, exportSchema = false)
@TypeConverters(AlertConverter::class, DailyConverter::class, HourlyConverter::class)
abstract class WeatherDatabase : RoomDatabase(){

    abstract fun weatherDao(): WeatherDao

}