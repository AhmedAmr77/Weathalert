package com.example.mvvm.favoritecities.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.Weathalert.R
import com.example.Weathalert.datalayer.WeatherRepository
import com.example.Weathalert.datalayer.entity.WeatherData
import com.example.mvvm.favoritecities.view.FavoriteCityDetailsActivity
import com.example.mvvm.utils.Constants
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener
import kotlinx.coroutines.*

class FavoriteViewModel(application: Application) : AndroidViewModel(application)  {


    private val weatherRepository = WeatherRepository(getApplication())

    val searchContainerLiveData = MutableLiveData<Boolean>()
//    val startCityDetailsActivityLiveData = MutableLiveData<WeatherData>()
    val citisListLiveData = MutableLiveData<List<WeatherData>>()
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()

    lateinit var shPref: SharedPreferences
    lateinit var lat: String
    lateinit var lon: String
    lateinit var lang: String
    lateinit var units: String

    fun fetchFavCities() {
        Log.i("test", "Fav VM fetchFavCities")
        loadingLiveData.postValue(true)
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            loadingLiveData.postValue(false)
            errorLiveData.postValue("from ExceptionHandlerr fetchFavCities : ${th.message.toString()}")
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            loadingLiveData.postValue(false)
            citisListLiveData.postValue(weatherRepository.getFavCities())
            Log.i("test", "Fav VM fetchFavCities success")
        }
    }

    fun showAutoComplete() {    //listner to open mapbox fragment to search
        searchContainerLiveData.value = true
    }

    fun fetchData() {
        initVar(getApplication())
        loadingLiveData.postValue(true)
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            loadingLiveData.postValue(false)
            errorLiveData.postValue("Fav VM from ExceptionHandlerr : ${th.message.toString()}")
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            val response = weatherRepository.getWeatherData(lat, lon, Constants.APP_ID, units, lang, Constants.EXECLUDE)
            if(response.isSuccessful){
                val res = response.body()
                res?.isFavorite = true
                weatherRepository.addCityToLocal(res!!)
                val favCities = weatherRepository.getFavCities()
                Log.i("test", "Fav VM fetchData success")
                withContext(Dispatchers.Main){
                    loadingLiveData.postValue(false)
                    citisListLiveData.postValue(favCities)
                    Log.i("test","Fav VM fetchData success main scope livedata")
                }
            } else {
                withContext(Dispatchers.Main){
                    errorLiveData.postValue("Fav VM from Retrofit not successful : ${response.message()}")
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

    fun deleteCity(city: WeatherData) {
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            Log.i("test","Fav VM deleteCity exception from database${th.message}")
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            weatherRepository.deleteCityData(city.lat, city.lon)
        }
    }

    fun refreshFavCitiesList() {
        loadingLiveData.postValue(true)
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            loadingLiveData.postValue(false)
            errorLiveData.postValue("Fav VM refreshFavCitiesList from ExceptionHandlerr : ${th.message.toString()}")
            Log.i("test","Fav VM refreshFavCitiesList exception from database${th.message}")
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            var favList = weatherRepository.getFavCities()
            favList.forEach {
                fetchDataForRefresh(it.lat.toString(), it.lon.toString())
            }
            favList = weatherRepository.getFavCities()
            withContext(Dispatchers.Main){
                loadingLiveData.postValue(false)
                citisListLiveData.postValue(favList)
                Log.i("test","Fav VM refreshFavCitiesList success main scope livedata")
            }
        }
    }

    private fun fetchDataForRefresh(latit: String, longit: String) {
        initVar(getApplication())
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            errorLiveData.postValue("Fav VM fetchDataForRefresh from ExceptionHandlerr : ${th.message.toString()}")
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
            val response = weatherRepository.getWeatherData(latit, longit, Constants.APP_ID, units, lang, Constants.EXECLUDE)
            if(response.isSuccessful){
                val res = response.body()
                res?.isFavorite = true
                weatherRepository.addCityToLocal(res!!)
                val favCities = weatherRepository.getFavCities()
                Log.i("test", "Fav VM fetchDataForRefresh success")
            } else {
                withContext(Dispatchers.Main){
                    errorLiveData.postValue("Fav VM fetchDataForRefresh from Retrofit not successful : ${response.message()}")
                }
            }
        }
    }


//    fun savaFavCity(lat:String, lon:String) {
//        weatherRepository.addFavCity(lat, lon)
//    }

//    fun startCityDetailsActivity(lat: String, lon: String) {
////        startCityDetailsActivityLiveData.value = city
//        val intent = Intent(getApplication(), FavoriteCityDetailsActivity::class.java).apply {
//            putExtra("LAT", lat)
//            putExtra("LON", lon)
//        }
//        startActivity(intent)
//
//    }

//    fun fetchFavCities(): LiveData<List<WeatherData>> {
//        return weatherRepository.getFavCities()
//    }            //wakeup
}