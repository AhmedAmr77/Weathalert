package com.example.Weathalert.datalayer.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.Weathalert.datalayer.entity.WeatherData


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
/*
//    @Update         //DLT if not needed
//    suspend fun update(weather: WeatherData)
@Dao
interface SleepDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(weather: SleepNight)

    @Query("SELECT * FROM SleepNight")
    fun getWeatherData(): LiveData<SleepNight>

    @Query("DELETE FROM SleepNight")
    suspend fun deleteAll()
}
 */

/*
    @Insert
    fun insert(night: SleepNight)

    @Update
    fun update(night: SleepNight)

    @Query("SELECT * from daily_sleep_quality_table WHERE nightId = :key")
    fun get(key: Long): SleepNight?

    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight(): SleepNight?

    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights(): LiveData<List<SleepNight>>
 */