package com.example.Weathalert.home.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.Weathalert.datalayer.WeatherRepository
import com.example.Weathalert.datalayer.entity.WeatherData
import com.example.Weathalert.home.view.HomeActivity
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.*
import retrofit2.Response
import kotlin.math.log

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

//    val weatherLiveData = MutableLiveData<WeatherData>()
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()
    val showDialog = MutableLiveData<Unit>()

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
/*
    private fun checkForPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            false
        } else
            true
    }
    private fun locEnabled(): Boolean {        //HHHHHHBBBBBBBBDDDDDD
        val locMngr = getApplication<Application>().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return locMngr.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    @SuppressLint("MissingPermission")
    fun requestNewLoc() {
        //Toast.makeText(MainActivity.this, "new REQUst", Toast.LENGTH_SHORT).show();
        val locReq = LocationRequest() //LocationRequest.create()
        locReq.setNumUpdates(1) //get only one udate then stop.
        locReq.setInterval(0)
        locReq.setFastestInterval(0)
        locReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        flpc = LocationServices.getFusedLocationProviderClient(getApplication())
        flpc.requestLocationUpdates(locReq, locCallBack, Looper.getMainLooper())
        //Toast.makeText(MainActivity.this, "after REQUst", Toast.LENGTH_SHORT).show();
    }
    private var locCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            //super.onLocationResult(locationResult);
            val loc = locationResult.lastLocation
            longit = loc.longitude
            latit = loc.latitude
        }
    } // like listner
    fun requestPremession() {                           //dialog for request permissions
        ActivityCompat.requestPermissions(getApplication(), permissions, permissionID)
    } // to know if user accept permission or not there is a callback method thats called automatically when user click on permissions button to tell what user answer.
    fun getLastLoc() {
        flpc = LocationServices.getFusedLocationProviderClient(getApplication() )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkForPermission()) {
                Log.i("loc","checkForPermission")
                if (locEnabled()) {
                    Log.i("loc","locEnabled")
                    flpc.getLastLocation()
                        .addOnCompleteListener(OnCompleteListener<Location?> { task ->
                            val loc = task.getResult()
                            if (loc != null) {
                                Log.i("loc","loc.latitude${loc.latitude}\nloc.longitude${loc.longitude}")
                            } else {
                                requestNewLoc()
                            }
                        })
                } else {
                    showDialog.value = Unit
                }
            } else {
                Log.i("loc","loc NOT Enabled")
                requestPremession()
            }
        } else {
            if (locEnabled()) {
                flpc.getLastLocation().addOnCompleteListener(OnCompleteListener<Location?> { task ->
                    val loc = task.result
                    requestNewLoc()
                    /*
                                if (loc != null) {
                                    requestNewLoc();
                                    longit = loc.getLongitude()+"";
                                    latit = loc.getLatitude()+"";
                                    Toast.makeText(MainActivity.this, loc.getLatitude()+"\n"+loc.getLongitude(), Toast.LENGTH_LONG).show();
                                } else {
                                    //Toast.makeText(MainActivity.this, "new LOCAAAAAA", Toast.LENGTH_SHORT).show();
                                    requestNewLoc();
                                }*/
                })
            } else {
                showDialog.value = Unit
            }
        }
    }
*/
}
