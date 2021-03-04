package com.example.mvvm.favoritecities.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.Weathalert.datalayer.WeatherRepository
import com.example.Weathalert.datalayer.entity.WeatherData

class FavoriteViewModel(application: Application) : AndroidViewModel(application)  {


    private val weatherRepository = WeatherRepository(getApplication())

    fun fetchData(): LiveData<WeatherData> {
        return weatherRepository.getWeatherData()
    }
}