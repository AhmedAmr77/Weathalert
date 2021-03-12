package com.example.mvvm.datalayer.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import com.example.mvvm.datalayer.entity.weather.WeatherData

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmData?)

    @Query("SELECT * FROM ALARM_DATA_TABLE")
    suspend fun getAllAlarms(): List<AlarmData>

    @Query("SELECT * FROM ALARM_DATA_TABLE WHERE id = :id")
    suspend fun getAlarm(id: Int): AlarmData

    @Query("DELETE FROM ALARM_DATA_TABLE WHERE id = :id")
    suspend fun deleteAlarm(id: Int)

}