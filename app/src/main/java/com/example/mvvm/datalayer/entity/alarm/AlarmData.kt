package com.example.mvvm.datalayer.entity.alarm

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_data_table")
data class AlarmData(
    @PrimaryKey(autoGenerate = true)
    var id: Int=0,
    @Embedded(prefix = "day_")
    val days: Days,
    val Type: String,
    val reapeat: Int,
    val value: Int,
    val isGreater: Boolean,
    val time: Long
)