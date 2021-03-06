package com.example.mvvm.favoritecities.viewmodel

import android.app.Application
import android.content.Intent
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

class FavoriteViewModel(application: Application) : AndroidViewModel(application)  {


    private val weatherRepository = WeatherRepository(getApplication())

    val searchContainerLiveData = MutableLiveData<Boolean>()
//    val startCityDetailsActivityLiveData = MutableLiveData<WeatherData>()


//    fun startCityDetailsActivity(lat: String, lon: String) {
////        startCityDetailsActivityLiveData.value = city
//        val intent = Intent(getApplication(), FavoriteCityDetailsActivity::class.java).apply {
//            putExtra("LAT", lat)
//            putExtra("LON", lon)
//        }
//        startActivity(intent)
//
//    }

    fun fetchData(): LiveData<List<WeatherData>> {
        return weatherRepository.getFavCities()
    }            //wakeup
    fun fetchFavCities(): LiveData<List<WeatherData>> {
        return weatherRepository.getFavCities()
    }


    fun showAutoComplete() {
        searchContainerLiveData.value = true
    }

    fun savaFavCity(lat:String, lon:String) {
        weatherRepository.addFavCity(lat, lon)
    }

    fun deleteCity(city: WeatherData) {
        weatherRepository.deleteCityData(city.lat, city.lon)
    }

}