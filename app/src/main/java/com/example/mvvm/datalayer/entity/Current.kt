package com.example.Weathalert.datalayer.entity

import androidx.room.Embedded
import androidx.room.TypeConverters
import com.example.Weathalert.datalayer.entity.coverters.WeatherConverter

@TypeConverters(WeatherConverter::class)
data class Current(
    val clouds: Int,
    val dew_point: Double,
    val dt: Int,
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    @Embedded(prefix = "rain_")
    val rain: Rain,
    val sunrise: Int,
    val sunset: Int,
    val temp: Double,
    val uvi: Double,
    val visibility: Int,
    val weather: List<Weather>,
    val wind_deg: Int,
    val wind_gust: Double,
    val wind_speed: Double
)