package com.example.mvvm.favoritecities.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Weathalert.databinding.ActivityFavoriteCityDetailsBinding
import com.example.Weathalert.datalayer.entity.Daily
import com.example.Weathalert.datalayer.entity.Hourly
import com.example.Weathalert.datalayer.entity.WeatherData
import com.example.Weathalert.home.view.DaysAdapter
import com.example.Weathalert.home.view.HoursAdapter
import com.example.mvvm.favoritecities.viewmodel.FavoriteCityDetailsViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class FavoriteCityDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: FavoriteCityDetailsViewModel
    lateinit var binding: ActivityFavoriteCityDetailsBinding
    private var hoursListAdapter = HoursAdapter(arrayListOf())
    private var daysListAdapter = DaysAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteCityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FavoriteCityDetailsViewModel::class.java)

        initUI()
        Log.i("favo", "welcomw")
        // Get the Intent that started this activity and extract the string
        val lat = intent.getStringExtra("LAT")
        val lon = intent.getStringExtra("LON")
        Log.i("favo", "teteetettee")
        //var city : WeatherData
        if (lat != null) {
            Log.i("favo", lat)
        }
        if (lon != null) {
            Log.i("favo", lon)
        }
        if (lat != null && lon != null) {
            viewModel.getCity(lat, lon).observe(this, Observer {
                if (it != null) {
                    updateUI(it)
                }
            })
        }

    }
    private fun updateUI(it: WeatherData) {
        val cityTime = it.timezone_offset?.plus(it.current?.dt!!)
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
        binding.tempTV.text = (it.current?.temp?.minus(273.15))?.toInt().toString()
        binding.humidityValTV.text = it.current?.humidity.toString().plus(" %")
        binding.pressureValTV.text = it.current?.pressure.toString()
        binding.windValTVa.text = it.current?.wind_speed.toString().plus(" m/s")
        binding.cloudsValTV.text = it.current?.clouds.toString().plus(" %")
        hoursListAdapter.updateHours(it.hourly as List<Hourly>)
        daysListAdapter.updateDays(it.daily as List<Daily>)
        binding.loadingView.visibility = View.GONE
    }

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
}