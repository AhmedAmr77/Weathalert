package com.example.Weathalert.datalayer.local

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.Weathalert.datalayer.entity.WeatherData

object LocalDataSource {

    lateinit var dao :WeatherDao

    fun getInstance(application: Application): WeatherDao{
        return Room.databaseBuilder(application, WeatherDatabase::class.java, "WeatherData").fallbackToDestructiveMigration().build().weatherDao()
    }

    suspend fun insert(city: WeatherData){
        dao.insert(city)
    }

    suspend fun update(city: WeatherData){
        dao.update(city)
    }

    fun getCityData(lat: Double, lon: Double): LiveData<WeatherData>{
        return dao.getCityData(lat, lon)
    }

    fun getAllCities(): LiveData<List<WeatherData>>{
        return dao.getAllCities()
    }

    suspend fun deleteCityData(lat: Double, lon: Double){
        dao.deleteCityData(lat, lon)
    }

    suspend fun deleteAll(){
        dao.deleteAll()
    }

}