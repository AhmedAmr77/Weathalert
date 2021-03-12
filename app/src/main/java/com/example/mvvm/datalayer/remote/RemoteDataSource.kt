package com.example.Weathalert.datalayer.remote

import com.example.mvvm.datalayer.entity.weather.WeatherData
import retrofit2.Response

class RemoteDataSource {

    private val retrofit: WeatherAPI = WeatherService.getWeatherService()

    suspend fun getWeatherData(lat: String, lon: String, appid: String, units: String, lang: String, execlude: String): Response<WeatherData> {
        return retrofit.getWeatherData(lat, lon, appid, units, lang, execlude)
    }

}
