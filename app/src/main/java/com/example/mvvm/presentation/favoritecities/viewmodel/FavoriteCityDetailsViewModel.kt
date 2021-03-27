package com.example.mvvm.presentation.favoritecities.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mvvm.domain.repositories.WeatherRepository
import com.example.mvvm.datalayer.entity.weather.WeatherData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteCityDetailsViewModel (application: Application) : AndroidViewModel(application)  {

    private val weatherRepository = WeatherRepository(getApplication())

    val cityLiveData = MutableLiveData<WeatherData>()
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()


    fun getCity(lat: String, lon: String) {
        loadingLiveData.postValue(true)
        val exceptionHandlerException = CoroutineExceptionHandler { _, _ ->
            loadingLiveData.postValue(false)
//            errorLiveData.postValue("Please, try again")  // if user become disconnected // handle it
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            cityLiveData.postValue(weatherRepository.getCity(lat, lon))
        }
    }
}