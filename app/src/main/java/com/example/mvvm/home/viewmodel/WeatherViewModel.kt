package com.example.Weathalert.home.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.Weathalert.datalayer.WeatherRepository
import com.example.Weathalert.datalayer.entity.WeatherData
import kotlinx.coroutines.*
import retrofit2.Response
import kotlin.math.log

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

//    val weatherLiveData = MutableLiveData<WeatherData>()
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()

    private val weatherRepository = WeatherRepository(getApplication())

    private val lat = 61.9060
    private val lon = 92.4456

    fun fetchData(): LiveData<WeatherData> {
        loadingLiveData.postValue(false)
        return weatherRepository.getWeatherData(lat, lon)
//        val res = weatherRepository.getWeatherData(lat, lon)
//        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
////            loadingLiveData.postValue(false)
////            errorLiveData.postValue("from ExceptionHandlerr : ${th.message.toString()}")
//        }
//        CoroutineScope(Dispatchers.Main + exceptionHandlerException).launch {
//            loadingLiveData.postValue(false)
//            weatherLiveData.postValue(res)
//        }
    }
//
}