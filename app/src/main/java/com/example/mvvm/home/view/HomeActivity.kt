package com.example.Weathalert.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Weathalert.R
import com.example.Weathalert.databinding.ActivityHomeBinding
import com.example.Weathalert.datalayer.entity.Daily
import com.example.Weathalert.datalayer.entity.Hourly
import com.example.Weathalert.datalayer.entity.WeatherData
import com.example.Weathalert.favoritecities.view.FavoriteActivity
import com.example.Weathalert.home.viewmodel.HomeViewModel
import com.example.Weathalert.settings.view.SettingsActivity
import com.example.mvvm.utils.Constants
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    lateinit var binding: ActivityHomeBinding
    private var hoursListAdapter = HoursAdapter(arrayListOf())
    private var daysListAdapter = DaysAdapter(arrayListOf())

    lateinit var sharedPreferences: SharedPreferences


    lateinit var flpc: FusedLocationProviderClient
    var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var permissionID = 77

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).getString(Constants.LANGUAGE_SETTINGS,"en")!!)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)

        flpc = LocationServices.getFusedLocationProviderClient(getApplication() )

        //setupNavigation()

        initUI()

        favCitiesFabListener()

    }

    override fun onStart() {
        super.onStart()
        getData()
        observeViewModel(viewModel)   // should call it in  onCreate() or onStart()
    }

    private fun getData() {
        Toast.makeText(this, "${Constants.LANGUAGE_SETTINGS} => ${sharedPreferences.getString(Constants.LANGUAGE_SETTINGS, "def")}", Toast.LENGTH_LONG).show()
//        Log.i("test")
        if (sharedPreferences.getString(Constants.FIRST_USE.toString(), "0") == "0"){
            getLastLoc()
            viewModel.fetchData()
            changeFirstUseStatus()
        } else {
            viewModel.fetchData()
            getLastLoc()
        }
    }

    private fun changeFirstUseStatus() {
        val editor = sharedPreferences.edit()
        editor.putString(Constants.FIRST_USE, "1").apply()
    }


    override fun onResume() {
        super.onResume()
//        getLastLoc()
//        observeViewModel(viewModel)
    }


//-----------------------------------LOCAL---------------------------------------------------------
    private fun setAppLocale(localeCode: String){
        val resources = resources;
        val dm = resources.getDisplayMetrics()
        val config = resources.getConfiguration()
        config.setLocale(Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, dm);
    }

    //----------------------------------MENU------------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)                 ///USE BINDING
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
        }
        return true
    }

//-------------------------------------INIT------------------------------------------------------
    private fun initUI() {
        binding.hoursRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = hoursListAdapter
        }
        binding.daysRecyclerView.apply {
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
//        val cityTime = it.timezone_offset?.plus(it.current?.dt!!)
        val cityTime = it.current?.dt
        binding.cityTV.text = it.timezone
        binding.descriptionTV.text = it.current?.weather?.get(0)?.description
        binding.dateTV.text = cityTime?.let { it1 -> convertLongToDateString(it1, "MM-dd-yyyy") }

//        val test= convertLongToDateString(cityTime, "MM-dd-yyyy")
//        val test2= convertLongToDateString(cityTime, "HH:mm")
//        Toast.makeText(this, "curr => ${it.current.dt}\n" +
//                                          "offs => ${it.timezone_offset}\n" +
//                                          "cu+of=>C  $cityTime\n" +
//                                            "test => $test\n" +
//                                            "test2 => $test2", Toast.LENGTH_LONG).show()

        binding.hourTV.text = cityTime?.let { it1 -> convertLongToDateString(it1, "HH:mm") }
        binding.tempTV.text = it.current?.temp?.toInt().toString().plus("°")
        binding.humidityValTV.text = it.current?.humidity.toString().plus(" %")
        binding.pressureValTV.text = it.current?.pressure.toString()
        binding.windValTVa.text = it.current?.wind_speed.toString().plus(" m/s")
        binding.cloudsValTV.text = it.current?.clouds.toString().plus(" %")
        hoursListAdapter.updateHours(it.hourly as List<Hourly>)
        daysListAdapter.updateDays(it.daily as List<Daily>)
    }

    /*
    @RequiresApi(Build.VERSION_CODES.O)
    fun cinv(time:Long){
        val formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())

    }
     */
    //GOOGLE time converter
    //@SuppressLint("SimpleDateFormat")
    private fun convertLongToDateString(systemTime: Int, pattern: String): String {
//        return SimpleDateFormat(pattern)
//            .format(systemTime).toString()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            var ldt: LocalDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(systemTime.toLong()),
                ZoneId.systemDefault()
            )
            return ldt.format(DateTimeFormatter.ofPattern(pattern))
        } else {
            return "OS lessThan OREO"
        }
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
        if (!it.isNullOrEmpty()) {
            binding.listError.text = it
            binding.listError.visibility = View.VISIBLE
            binding.linearLayoutContainer.visibility = View.GONE
            binding.daysRecyclerView.visibility = View.GONE
            binding.hoursRecyclerView.visibility = View.GONE
        } else {
            binding.listError.visibility = View.GONE
        }
    }

    private fun hideError() {
        binding.listError.visibility = View.GONE
        binding.permissionBtn.visibility = View.GONE
        binding.linearLayoutContainer.visibility = View.VISIBLE
        binding.daysRecyclerView.visibility = View.VISIBLE
        binding.hoursRecyclerView.visibility = View.VISIBLE
    }

    private fun favCitiesFabListener() {            ///WRITE IT IN VIEWMODEL
        val fab: View = binding.favCitiesFab
        fab.setOnClickListener {         //view ->
            startActivity(Intent(applicationContext, FavoriteActivity::class.java))
        }
    }

//---------------------------------------places-----------------------------------------------------
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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

    private fun showEnableBtn(n: Int) {
        binding.permissionBtn.visibility = View.VISIBLE
        binding.permissionBtn.setOnClickListener {
            if (n == 0){
                requestPremession()
            } else {
                enableLocation()
            }
        }
    }

    private fun requestPremession() {                           //dialog for request permissions
        ActivityCompat.requestPermissions(this, permissions, permissionID)
    } // to know if user accept permission or not there is a callback method thats called automatically when user click on permissions button to tell what user answer.
    private fun checkForPermission(): Boolean {
        return !(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
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
        //Toast.makeText(MainActivity.this, "new REQUst", Toast.LENGTH_SHORT).show();
        val locReq = LocationRequest() //LocationRequest.create()
//        locReq.setNumUpdates(1) //get only one udate then stop.
        locReq.setInterval(50000)
//        locReq.setFastestInterval(0)
        locReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        flpc = LocationServices.getFusedLocationProviderClient(this)
        flpc.requestLocationUpdates(locReq, locCallBack, Looper.getMainLooper())
        //Toast.makeText(MainActivity.this, "after REQUst", Toast.LENGTH_SHORT).show();
    }
    private var locCallBack: LocationCallback = object : LocationCallback() {  // like listner
        override fun onLocationResult(locationResult: LocationResult) {
            //super.onLocationResult(locationResult);
            val loc = locationResult.lastLocation
            val latit = BigDecimal(loc.latitude).setScale(4,RoundingMode.HALF_DOWN)
            val longit = BigDecimal(loc.longitude).setScale(4, RoundingMode.HALF_DOWN)
            saveCurrentLocationToSharedPref(latit.toString(), longit.toString())
        }
    }
    private fun getLastLoc() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkForPermission()) {
                Toast.makeText(this, "1111", Toast.LENGTH_SHORT).show()
                if (locEnabled()) {
                    Toast.makeText(this, "2222", Toast.LENGTH_SHORT).show()
                    requestNewLoc()
                } else {
                    Toast.makeText(this, "Please, Enable the Location", Toast.LENGTH_LONG).show()
                    if(Constants.ENABLE_LOCATION == "0") {
                        Constants.ENABLE_LOCATION = "1"
                        enableLocation()
                    } else {
                        showError("Please, Enable the location")
                        showEnableBtn(1)
                    }
                }
            } else {
                Toast.makeText(this, "3333", Toast.LENGTH_SHORT).show()
                requestPremession()
            }
        } else {
            if (locEnabled()) {
                flpc.lastLocation.addOnCompleteListener(OnCompleteListener<Location?> { task ->
                    val loc = task.result
                    requestNewLoc()
                })
            } else {
                enableLocation()
            }
        }
    }

    private fun saveCurrentLocationToSharedPref(latitude: String,longitude: String){
        Log.i("test", "Home save in shPref lat => ${latitude} and lon => ${longitude}")
        val sharedPref = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(Constants.LATITUDE,latitude).apply()
        editor.putString(Constants.LONGITUDE,longitude).apply()
        viewModel.deleteOldCurrent()
    }

//----------------------try to apply mvvm on places and permissions---------------------------------
    /*
    private fun setupNavigation() {
        viewModel.showDialog.observe(this, Observer {
            showPermDialog()
        })

//        viewModel.openTaskEvent.observe(this, Observer {
//            openTaskDetails(it)
//        })
    }
    private fun showPermDialog() {
        val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(i)
    }
     */
}