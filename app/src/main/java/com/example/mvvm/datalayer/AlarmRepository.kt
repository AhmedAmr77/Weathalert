package com.example.mvvm.datalayer

import android.app.Application
import com.example.Weathalert.datalayer.local.LocalDataSource
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import com.example.mvvm.datalayer.entity.weather.WeatherData

class AlarmRepository(val application: Application) {

    private val localDataSource = LocalDataSource.getInstanceOfAlarmTable(application)


    suspend fun addAlarm(alarm: AlarmData){
        localDataSource.insert(alarm)
    }

    suspend fun getAllAlarms(): List<AlarmData>{
        return localDataSource.getAllAlarms()
    }

    suspend fun getAlarm(id: Int): AlarmData{
        return localDataSource.getAlarm(id)
    }

    suspend fun deleteAlarm(id: Int){
        return localDataSource.deleteAlarm(id)
    }
}