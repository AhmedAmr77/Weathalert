package com.example.mvvm.datalayer.entity.weather

import androidx.room.Embedded
import androidx.room.TypeConverters
import com.example.Weathalert.datalayer.entity.coverters.WeatherConverter

@TypeConverters(WeatherConverter::class)
data class Hourly(
    val clouds: Int? = null,
    val dew_point: Double? = null,
    val dt: Int? = null,
    val feels_like: Double? = null,
    val humidity: Int? = null,
    val pop: Double? = null,
    val pressure: Int? = null,
    @Embedded(prefix = "rain_")
    val rain: Rain? = null,
    val temp: Double? = null,
    val uvi: Double? = null,
    val visibility: Int? = null,
    val weather: List<Weather?>? = null,
    val wind_deg: Int? = null,
    val wind_speed: Double? = null
)