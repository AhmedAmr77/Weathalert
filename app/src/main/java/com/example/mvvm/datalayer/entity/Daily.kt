package com.example.Weathalert.datalayer.entity

import androidx.room.Embedded
import androidx.room.TypeConverters
import com.example.Weathalert.datalayer.entity.coverters.WeatherConverter

@TypeConverters(WeatherConverter::class)
data class Daily(
    val clouds: Int,
    val dew_point: Double,
    val dt: Int,
    @Embedded(prefix = "feelsLike_")
    val feels_like: FeelsLike,
    val humidity: Int,
    val pop: Double,
    val pressure: Int,
    val rain: Double,
    val sunrise: Int,
    val sunset: Int,
    @Embedded(prefix = "temp_")
    val temp: Temp,
    val uvi: Double,
    val weather: List<Weather>,
    val wind_deg: Int,
    val wind_speed: Double
)