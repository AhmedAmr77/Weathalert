package com.example.Weathalert.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Weathalert.R
import com.example.Weathalert.databinding.ActivityHomeBinding
import com.example.mvvm.datalayer.entity.weather.Daily
import com.example.mvvm.datalayer.entity.weather.Hourly
import com.example.mvvm.datalayer.entity.weather.WeatherData
import com.example.Weathalert.favoritecities.view.FavoriteActivity
import com.example.Weathalert.home.viewmodel.HomeViewModel
import com.example.Weathalert.settings.view.SettingsActivity
import com.example.mvvm.presentation.addalarm.view.AddAlarmActivity
import com.example.mvvm.presentation.alarm.view.AlarmsActivity
import com.example.mvvm.utils.Constants
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    lateinit var binding: ActivityHomeBinding
    private lateinit var hoursListAdapter : HoursAdapter
    private lateinit var daysListAdapter : DaysAdapter

    lateinit var sharedPreferences: SharedPreferences

    lateinit var flpc: FusedLocationProviderClient
    var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var permissionID = 77

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).getString(Constants.LANGUAGE_SETTINGS, "en")!!)

        supportActionBar?.title = resources.getString(R.string.app_name)


        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)

        flpc = LocationServices.getFusedLocationProviderClient(getApplication())

        daysListAdapter = DaysAdapter(arrayListOf())
        hoursListAdapter = HoursAdapter(arrayListOf())
        initUI()

        if (intent.getBooleanExtra(Constants.LANG_CHANGED, false)) {
            finish()
            startActivity(Intent(this, HomeActivity::class.java))
        }


        observeViewModel(viewModel)
        favCitiesFabListener()
        hourlyDailyListener()

    }


    override fun onResume() {
        super.onResume()
        checkPermissionAndLoc()
    }

    private fun checkPermissionAndLoc() {
        if (checkForPermission()) {
            if (locEnabled()) {
                getData()
            } else {
                showEnableBtn(1)
            }
        } else {
            showEnableBtn(0)
        }
    }

    private fun getData() {
        if (sharedPreferences.getString(Constants.FIRST_USE.toString(), "0") == "0") {
            getLastLoc()
            changeFirstUseStatus()
        } else {
            viewModel.fetchData()
        }
    }

    private fun changeFirstUseStatus() {
        val editor = sharedPreferences.edit()
        editor.putString(Constants.FIRST_USE, "1").apply()
    }


    //-----------------------------------LOCAL---------------------------------------------------------
    private fun setAppLocale(localeCode: String) {
        val resources = resources;
        val dm = resources.getDisplayMetrics()
        val config = resources.configuration
        config.setLocale(Locale(localeCode.toLowerCase()))
        resources.updateConfiguration(config, dm)

        val appRes = application.resources
        val dmApp = appRes.getDisplayMetrics()
        val configApp = appRes.configuration
        configApp.setLocale(Locale(localeCode.toLowerCase()))
        appRes.updateConfiguration(configApp, dmApp)
    }

    //----------------------------------MENU------------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> startActivity(
                Intent(
                    applicationContext,
                    SettingsActivity::class.java
                )
            )

            R.id.menu_alarm -> startActivity(
                Intent(
                    applicationContext,
                    AlarmsActivity::class.java
                )
            )

        }
        return true
    }

//-------------------------------------INIT------------------------------------------------------

    private fun initUI() {
        binding.hourlyRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = hoursListAdapter
        }
        binding.dailyRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = daysListAdapter
        }
    }

    private fun observeViewModel(viewModel: HomeViewModel) {
        viewModel.loadingLiveData.observe(this, { showLoading(it) })
        viewModel.errorLiveData.observe(this, { showError(it) })
        viewModel.cityLiveData.observe(this, { updateUI(it) })
    }

    private fun updateUI(it: WeatherData) {
        binding.textClockHome.timeZone = it.timezone
        binding.homeMainBackground.setImageResource(getModeRes(it))
        val cityTime = it.current?.dt
        binding.homeMainDateTV.text =
            cityTime?.let { it1 -> dateConverter(it1, "EEE, d MMM") } //"EEE d MMM"
        binding.homeMainCityNameTV.text = it.timezone                                                     //switch case to locale days
        binding.homeMainDescTV.text = it.current?.weather?.get(0)?.description
        binding.homeMainIcon.setImageResource(getResId(it.current?.weather?.get(0)?.icon))
        binding.homeMainTempTV.text = "${numLocale(it.current?.temp!!)}°"
        binding.homeMainHumidityTVVal.text = "${numLocale(it.current.humidity?.toDouble()!!)} ${resources.getString(R.string.percent_sign)}"
        binding.homeMainPressureTVVal.text = "${numLocale(it.current.pressure?.toDouble()!!)}  ${resources.getString(R.string.hPa)}"
        binding.homeMainWindTVVal.text = "${numLocale(it.current.wind_speed!!)} ${resources.getString(R.string.met_per_sec)}"
        binding.homeMainCloudsTVVal.text = "${numLocale(it.current.clouds?.toDouble()!!)} ${resources.getString(R.string.percent_sign)}"

        hoursListAdapter.updateHours(it.hourly as List<Hourly>)
        daysListAdapter.updateDays(it.daily as List<Daily>)
    }

    private fun numLocale(num: Double): String {
        return NumberFormat.getInstance(Locale
                (sharedPreferences.getString(Constants.LANGUAGE_SETTINGS, "en"))).format(num.toInt())
    }

    private fun getModeRes(weatherData: WeatherData): Int {
        if (weatherData.current?.weather?.get(0)?.icon?.get(2).toString() == "n") {
            return R.drawable.night_wall
        }
        return R.drawable.day_wall
    }

    private fun getResId(icon: String?): Int {
        val res = when (icon) {
            "01d" -> R.drawable.d_oneone
            "01n" -> R.drawable.n_one_n
            "02d" -> R.drawable.n_two
            "02n" -> R.drawable.n_two_n
            "03d", "03n", "04d", "04n" -> R.drawable.n_three_four
            "09d", "09n" -> R.drawable.n_nine
            "10d", "10n" -> R.drawable.n_ten
            "11d", "11n" -> R.drawable.n_eleven
            "13d", "13n" -> R.drawable.n_thirteen
            "50d", "50n" -> R.drawable.n_fifty
            else -> R.drawable.n_two
        }
        return res
    }

    private fun dateConverter(dt: Int, pattern: String): String {
        val calender = Calendar.getInstance()
        calender.timeInMillis = (dt) * 1000L
        val dateFormat =
            SimpleDateFormat(pattern, Locale(sharedPreferences.getString(Constants.LANGUAGE_SETTINGS, "en")))
        return dateFormat.format(calender.time)
    }

//---------------------------------------VIEW-------------------------------------------------------

    private fun showLoading(it: Boolean) {
        if (it) {
            binding.loadingView.visibility = View.VISIBLE
        } else {
            binding.loadingView.visibility = View.GONE
        }
    }

    private fun showError(it: String) {
        if (it.isNotEmpty()) {
            binding.listError.text = it
            binding.listError.visibility = View.VISIBLE
            binding.loadingView.visibility = View.GONE
            binding.homeDataContainer.visibility = View.GONE
            binding.homeHoursAndDailyContainer.visibility = View.GONE
            binding.favCitiesFab.visibility = View.GONE
        } else {
            binding.listError.visibility = View.GONE
            binding.homeDataContainer.visibility = View.VISIBLE
            binding.homeHoursAndDailyContainer.visibility = View.VISIBLE
            binding.favCitiesFab.visibility = View.VISIBLE
        }
    }

    private fun hideError() {
        binding.listError.visibility = View.GONE
        binding.permissionBtn.visibility = View.GONE
        binding.homeDataContainer.visibility = View.VISIBLE
        binding.homeHoursAndDailyContainer.visibility = View.VISIBLE
        binding.favCitiesFab.visibility = View.VISIBLE
    }

    private fun showEnableBtn(n: Int) {
        showError("Please, allow the location permission")
        binding.permissionBtn.visibility =
            View.VISIBLE                                 //permissionBtn => GONE
        binding.permissionBtn.setOnClickListener {
            if (n == 0) {
                requestPremession()
            } else {
                enableLocation()
            }
        }
    }

    private fun favCitiesFabListener() {            ///WRITE IT IN VIEWMODEL
        val fab: View = binding.favCitiesFab
        fab.setOnClickListener {         //view ->
            startActivity(Intent(applicationContext, FavoriteActivity::class.java))
        }
    }

    private fun hourlyDailyListener() {
        val hourly = binding.homeLabelHourlyTV
        val daily = binding.homeLabelDailyTV
        hourly.setOnClickListener {
            hourly.setBackgroundResource(R.drawable.hourly_daily_pressed)
            daily.setBackgroundResource(R.drawable.table_layout)
            binding.hourlyRecyclerView.visibility = View.VISIBLE
            binding.dailyRecyclerView.visibility = View.GONE
        }
        daily.setOnClickListener {
            daily.setBackgroundResource(R.drawable.hourly_daily_pressed)
            hourly.setBackgroundResource(R.drawable.table_layout)
            binding.dailyRecyclerView.visibility = View.VISIBLE
            binding.hourlyRecyclerView.visibility = View.GONE
        }
    }

    //---------------------------------------places-----------------------------------------------------
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionID) {  //maybe there are many permissions returned. Here we check for the location id that i defined before
            if (permissions.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLoc()
                hideError()
            } else {
                showError("Please, allow the location permission")
                showEnableBtn(0)
            }
        }
    }


    private fun requestPremession() {                           //dialog for request permissions
        ActivityCompat.requestPermissions(this, permissions, permissionID)
    } // to know if user accept permission or not there is a callback method thats called automatically when user click on permissions button to tell what user answer.

    private fun checkForPermission(): Boolean {
        return !(ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED)
    }

    private fun locEnabled(): Boolean {
        val locMngr = getSystemService(LOCATION_SERVICE) as LocationManager
        return locMngr.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun enableLocation() {
        val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(i)
    }

    @SuppressLint("MissingPermission")
    fun requestNewLoc() {
        val locReq = LocationRequest() //LocationRequest.create()
//        locReq.setNumUpdates(1) //get only one udate then stop.
        locReq.setInterval(300000)
//        locReq.setFastestInterval(0)
        locReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        flpc = LocationServices.getFusedLocationProviderClient(this)
        flpc.requestLocationUpdates(locReq, locCallBack, Looper.getMainLooper())
    }

    private var locCallBack: LocationCallback = object : LocationCallback() {  // like listner
        override fun onLocationResult(locationResult: LocationResult) {
            //super.onLocationResult(locationResult);
            val loc = locationResult.lastLocation
            val latit = BigDecimal(loc.latitude).setScale(4, RoundingMode.HALF_DOWN)
            val longit = BigDecimal(loc.longitude).setScale(4, RoundingMode.HALF_DOWN)
            saveCurrentLocationToSharedPref(latit.toString(), longit.toString())
        }
    }

    private fun getLastLoc() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkForPermission()) {
                if (locEnabled()) {
                    requestNewLoc()
                } else {
                    if (Constants.ENABLE_LOCATION == "0") {
                        Constants.ENABLE_LOCATION = "1"
                        enableLocation()
                    } else {
                        showError("Please, Enable the location")
                        showEnableBtn(1)
                    }
                }
            } else {
                requestPremession()
            }
        } else {
            if (locEnabled()) {
                flpc.lastLocation.addOnCompleteListener(OnCompleteListener<Location?> {
                    requestNewLoc()
                })
            } else {
                enableLocation()
            }
        }
    }

    private fun saveCurrentLocationToSharedPref(latitude: String, longitude: String) {
        val sharedPref = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(Constants.LATITUDE, latitude).apply()
        editor.putString(Constants.LONGITUDE, longitude).apply()
        viewModel.deleteOldCurrent()
    }

}

/*
   fun getCityName(context:Context,savedLang: String,lat: Double,lon:Double,timeZone:String):String{
      var locationAddress = ""
      val geocoder = Geocoder(context, Locale(savedLang));
      try {
          if(savedLang=="ar"){
              locationAddress = geocoder.getFromLocation(lat,lon,1)[0].countryName ?: timeZone
          }else{
              locationAddress = geocoder.getFromLocation(lat,lon,1)[0].adminArea ?: timeZone
              locationAddress += ", ${geocoder.getFromLocation(lat,lon,1)[0].countryName ?: timeZone}"}
      } catch (e: IOException){
          e.printStackTrace()
      }
      return locationAddress
  }
*/