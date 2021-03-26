package com.example.Weathalert.home.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mvvm.domain.repositories.WeatherRepository
import com.example.mvvm.datalayer.entity.weather.WeatherData
import com.example.mvvm.utils.Constants
import kotlinx.coroutines.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val cityLiveData = MutableLiveData<WeatherData>()
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()
    //val showDialog = MutableLiveData<Unit>()

    private val weatherRepository = WeatherRepository(getApplication())

    lateinit var shPref: SharedPreferences
    lateinit var lat: String
    lateinit var lon: String
    lateinit var lang: String
    lateinit var units: String


    fun fetchData() {
        initVar(getApplication())
        loadingLiveData.postValue(true)
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            loadingLiveData.postValue(false)
            errorLiveData.postValue("Please, try again")
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
         val response = weatherRepository.getWeatherData(lat, lon, Constants.APP_ID, units, lang, Constants.EXECLUDE)
            if(response.isSuccessful){
                weatherRepository.deleteOldCurrent()                    //what if there no current (no isFav=0)
                weatherRepository.addCityToLocal(response.body()!!)
                withContext(Dispatchers.Main){
                    loadingLiveData.postValue(false)
                    cityLiveData.postValue(response.body())
                }
            } else {
                withContext(Dispatchers.Main){
                    errorLiveData.postValue("from Retrofit not successful : ${response.message()}")
                }
            }
        }
    }

    fun deleteOldCurrent() {
        initVar(getApplication())
        CoroutineScope(Dispatchers.IO).launch {
            val response = weatherRepository.getWeatherData(lat, lon, Constants.APP_ID, units, lang, Constants.EXECLUDE)
            if(response.isSuccessful){
                weatherRepository.deleteOldCurrent()
                weatherRepository.addCityToLocal(response.body()!!)
                withContext(Dispatchers.Main){
                    cityLiveData.postValue(response.body())
                }
            }
        }
    }

    private fun initVar(app: Application) {
        shPref = app.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        lat = shPref.getString(Constants.LATITUDE,"0").toString()
        lon = shPref.getString(Constants.LONGITUDE,"0").toString()
        lang = shPref.getString(Constants.LANGUAGE_SETTINGS,"en").toString()
        units = shPref.getString(Constants.UNIT_SETTINGS,"standard").toString()
    }

}