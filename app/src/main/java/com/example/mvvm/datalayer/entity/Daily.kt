package com.example.Weathalert.datalayer.entity

import androidx.room.Embedded
import androidx.room.TypeConverters
import com.example.Weathalert.datalayer.entity.coverters.WeatherConverter

@TypeConverters(WeatherConverter::class)
data class Daily(
    val clouds: Int? = null,
    val dew_point: Double? = null,
    val dt: Int? = null,
    @Embedded(prefix = "feelsLike_")
    val feels_like: FeelsLike? = null,
    val humidity: Int? = null,
    val pop: Double? = null,
    val pressure: Int? = null,
    val rain: Double? = null,
    val sunrise: Int? = null,
    val sunset: Int? = null,
    @Embedded(prefix = "temp_")
    val temp: Temp? = null,
    val uvi: Double? = null,
    val weather: List<Weather?>? = null,
    val wind_deg: Int? = null,
    val wind_speed: Double? = null
)