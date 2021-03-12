package com.example.Weathalert.datalayer.local

import android.app.Application
import androidx.room.Room
import com.example.mvvm.datalayer.local.AlarmDao
import com.example.mvvm.datalayer.local.AlarmDatabase

object LocalDataSource {

    fun getInstance(application: Application): WeatherDao{
        return Room.databaseBuilder(application, WeatherDatabase::class.java, "WeatherData")
                .fallbackToDestructiveMigration().build().weatherDao()
    }

    fun getInstanceOfAlarmTable(application: Application): AlarmDao{
        return Room.databaseBuilder(application, AlarmDatabase::class.java, "alarm_data_table")
            .fallbackToDestructiveMigration().build().alarmDao()
    }

}