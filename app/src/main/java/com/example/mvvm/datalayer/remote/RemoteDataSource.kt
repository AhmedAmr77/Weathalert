package com.example.Weathalert.datalayer.remote

import com.example.Weathalert.datalayer.entity.WeatherData
import retrofit2.Response

class RemoteDataSource {

    private val APP_ID = "676da108dea75e4101df9c4c03b3d757"

    private val retrofit: WeatherAPI = WeatherService.getWeatherService()

    suspend fun getWeatherData(lat: String, lon: String): Response<WeatherData> {
        return retrofit.getWeatherData(lat, lon, APP_ID)
    }

}
