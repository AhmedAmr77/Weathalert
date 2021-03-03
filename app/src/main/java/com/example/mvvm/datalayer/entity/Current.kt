package com.example.Weathalert.datalayer.entity

import androidx.room.Embedded
import androidx.room.TypeConverters
import com.example.Weathalert.datalayer.entity.coverters.WeatherConverter

@TypeConverters(WeatherConverter::class)
data class Current(
    val clouds: Int? = null,
    val dew_point: Double? = null,
    val dt: Int? = null,
    val feels_like: Double? = null,
    val humidity: Int? = null,
    val pressure: Int? = null,
    @Embedded(prefix = "rain_")
    val rain: Rain? = null,
    val sunrise: Int? = null,
    val sunset: Int? = null,
    val temp: Double? = null,
    val uvi: Double? = null,
    val visibility: Int? = null,
    val weather: List<Weather?>? = null,
    val wind_deg: Int? = null,
    val wind_gust: Double? = null,
    val wind_speed: Double? = null
)