package com.example.mvvm.presentation.favoritecities.viewmodel

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

class FavoriteViewModel(application: Application) : AndroidViewModel(application)  {


    private val weatherRepository = WeatherRepository(getApplication())

    val citisListLiveData = MutableLiveData<List<WeatherData>>()
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()

    lateinit var shPref: SharedPreferences
    lateinit var lat: String
    lateinit var lon: String
    lateinit var lang: String
    lateinit var units: String

    fun fetchFavCities() {
        loadingLiveData.postValue(true)
        val exceptionHandlerException = CoroutineExceptionHandler { _, _ ->
            loadingLiveData.postValue(false)
            errorLiveData.postValue("Please, try again")
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            loadingLiveData.postValue(false)
            citisListLiveData.postValue(weatherRepository.getFavCities())
        }
    }


    fun fetchData() {
        initVar(getApplication())
        loadingLiveData.postValue(true)
        val exceptionHandlerException = CoroutineExceptionHandler { _, _ ->
            loadingLiveData.postValue(false)
            errorLiveData.postValue("Please, try again")
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            val response = weatherRepository.getWeatherData(lat, lon, Constants.APP_ID, units, lang, Constants.EXECLUDE)
            if(response.isSuccessful){
                val res = response.body()
                res?.isFavorite = true
                weatherRepository.addCityToLocal(res!!)
                val favCities = weatherRepository.getFavCities()
                withContext(Dispatchers.Main){
                    loadingLiveData.postValue(false)
                    citisListLiveData.postValue(favCities)
                }
            } else {
                withContext(Dispatchers.Main){
                    errorLiveData.postValue("Please, try again")
                }
            }
        }
    }


    private fun initVar(app: Application) {
        shPref = app.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        lat = shPref.getString(Constants.FAV_LATITUDE,"0").toString()
        lon = shPref.getString(Constants.FAV_LONGITUDE,"0").toString()
        lang = shPref.getString(Constants.LANGUAGE_SETTINGS,"en").toString()
        units = shPref.getString(Constants.UNIT_SETTINGS,"standard").toString()
    }

    fun deleteCity(city: WeatherData) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherRepository.deleteCityData(city.lat, city.lon)
        }
    }

    fun refreshFavCitiesList() {
        loadingLiveData.postValue(true)
        val exceptionHandlerException = CoroutineExceptionHandler { _, _ ->
            loadingLiveData.postValue(false)
            errorLiveData.postValue("Please, try again")
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            val def = async {
                weatherRepository.getFavCities().forEach {
                    fetchDataForRefresh(it.lat.toString(), it.lon.toString())
                }
                weatherRepository.getFavCities()
            }
            val res = def.await()
            withContext(Dispatchers.Main){
                loadingLiveData.postValue(false)
                citisListLiveData.postValue(res)
            }
        }
    }

    private fun fetchDataForRefresh(latit: String, longit: String) {
        initVar(getApplication())
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            errorLiveData.postValue("Please, try again")
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            val response = weatherRepository.getWeatherData(latit, longit, Constants.APP_ID, units, lang, Constants.EXECLUDE)
            if(response.isSuccessful){
                val res = response.body()
                res?.isFavorite = true
                weatherRepository.addCityToLocal(res!!)
            } else {
                withContext(Dispatchers.Main){
                    errorLiveData.postValue("Please, try again")
                }
            }
        }
    }

}