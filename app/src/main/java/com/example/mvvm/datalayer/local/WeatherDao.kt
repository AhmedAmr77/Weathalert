package com.example.Weathalert.datalayer.local

import androidx.room.*
import com.example.mvvm.datalayer.entity.weather.WeatherData


@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: WeatherData?)

    @Query("SELECT * FROM WeatherData WHERE lat = :lat and lon = :lon")
    suspend fun getCityData(lat: Double, lon: Double): WeatherData  //no need suspend cause => Room already uses a background thread for that specific @Query which returns LiveData

    @Query("SELECT * from WeatherData WHERE isFavorite = 1")
    suspend fun getAllCities(): List<WeatherData>

    @Query("DELETE FROM WeatherData WHERE lat = :lat and lon = :lon")
    suspend fun deleteCityData(lat: Double, lon: Double)

    @Query("DELETE FROM WeatherData WHERE isFavorite = 0")
    suspend fun deleteOldCurrent()

    @Query("DELETE FROM WeatherData")
    suspend fun deleteAll()
}
