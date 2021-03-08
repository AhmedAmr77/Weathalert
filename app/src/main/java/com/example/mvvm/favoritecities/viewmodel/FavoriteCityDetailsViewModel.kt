package com.example.mvvm.favoritecities.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.Weathalert.datalayer.WeatherRepository
import com.example.Weathalert.datalayer.entity.WeatherData
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
        Log.i("test", "FavDet VM getCity")
        loadingLiveData.postValue(true)
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            loadingLiveData.postValue(false)
            errorLiveData.postValue("FavDet VM getCity from ExceptionHandlerr : ${th.message.toString()}")
            Log.i("test", "FavDet VM getCity from ExceptionHandlerr")
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            cityLiveData.postValue(weatherRepository.getCity(lat, lon))
            Log.i("test", "FavDet VM getCity success")
        }
    }

//    val startCityDetailsActivityLiveData = MutableLiveData<WeatherData>()
//
//
//    fun startCityDetailsActivity(city: WeatherData) {
//        startCityDetailsActivityLiveData.value = city
//    }

}