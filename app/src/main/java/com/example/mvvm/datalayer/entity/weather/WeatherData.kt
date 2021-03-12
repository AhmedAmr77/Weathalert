package com.example.mvvm.datalayer.entity.weather

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.Weathalert.datalayer.entity.coverters.AlertConverter
import com.example.Weathalert.datalayer.entity.coverters.DailyConverter
import com.example.Weathalert.datalayer.entity.coverters.HourlyConverter

@Entity(primaryKeys = ["lat", "lon"])
@TypeConverters(AlertConverter::class,DailyConverter::class,HourlyConverter::class)
data class WeatherData(
    val alerts: List<Alert?>? = null,
    @Embedded(prefix = "curr_")
    val current: Current? = null,
    val daily: List<Daily?>? = null,
    val hourly: List<Hourly?>? = null,
    val lat: Double,
    val lon: Double,
    val timezone: String? = null,
    val timezone_offset: Int? = null,
    var isFavorite: Boolean = false
)
