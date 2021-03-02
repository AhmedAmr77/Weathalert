package com.example.Weathalert.datalayer.remote

import com.example.Weathalert.datalayer.entity.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("/data/2.5/onecall?")
    suspend fun getWeatherData(@Query("lat") lat: String?, @Query("lon") lon: String?, @Query("appid") appid: String?) : Response<WeatherData>

}