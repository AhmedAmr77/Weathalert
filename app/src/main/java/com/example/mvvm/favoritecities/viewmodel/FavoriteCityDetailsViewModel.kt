package com.example.mvvm.favoritecities.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.Weathalert.datalayer.WeatherRepository
import com.example.Weathalert.datalayer.entity.WeatherData

class FavoriteCityDetailsViewModel (application: Application) : AndroidViewModel(application)  {



    private val weatherRepository = WeatherRepository(getApplication())

    fun getCity(lat: String, lon: String): LiveData<WeatherData> {
        return weatherRepository.getCity(lat, lon)
    }

//    val startCityDetailsActivityLiveData = MutableLiveData<WeatherData>()
//
//
//    fun showAutoComplete(city: WeatherData) {
//        startCityDetailsActivityLiveData.value = city
//    }

}