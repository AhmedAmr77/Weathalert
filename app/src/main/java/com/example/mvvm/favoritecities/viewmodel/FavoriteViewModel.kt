package com.example.mvvm.favoritecities.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.Weathalert.R
import com.example.Weathalert.datalayer.WeatherRepository
import com.example.Weathalert.datalayer.entity.WeatherData
import com.example.mvvm.utils.Constants
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener

class FavoriteViewModel(application: Application) : AndroidViewModel(application)  {


    private val weatherRepository = WeatherRepository(getApplication())

    val searchContainerLiveData = MutableLiveData<Boolean>()

    fun fetchFavCities(): LiveData<List<WeatherData>> {
        return weatherRepository.getFavCities()
    }

    fun showAutoComplete() {
        searchContainerLiveData.value = true
    }

    fun savaFavCity(lat:String, lon:String) {
        weatherRepository.addFavCity(lat, lon)
    }
}