package com.example.mvvm.datalayer.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mvvm.datalayer.entity.alarm.AlarmData

@Database(entities = [AlarmData::class], version = 1, exportSchema = false)
abstract class AlarmDatabase: RoomDatabase() {

    abstract fun alarmDao(): AlarmDao
}