package com.example.Weathalert.datalayer.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherService {
    private const val BASE_URL = "https://api.openweathermap.org"

    fun getWeatherService(): WeatherAPI{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherAPI::class.java)
    }
}