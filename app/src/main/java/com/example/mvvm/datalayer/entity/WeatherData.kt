package com.example.Weathalert.datalayer.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.Weathalert.datalayer.entity.coverters.AlertConverter
import com.example.Weathalert.datalayer.entity.coverters.DailyConverter
import com.example.Weathalert.datalayer.entity.coverters.HourlyConverter

@Entity(primaryKeys = ["lat", "lon"])
@TypeConverters(AlertConverter::class,DailyConverter::class,HourlyConverter::class)
data class WeatherData(
    val alerts: List<Alert>,
    @Embedded(prefix = "curr_")
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int
)
