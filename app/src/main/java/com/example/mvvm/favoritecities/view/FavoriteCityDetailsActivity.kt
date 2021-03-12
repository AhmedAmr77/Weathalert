package com.example.mvvm.favoritecities.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Weathalert.R
import com.example.Weathalert.databinding.ActivityFavoriteCityDetailsBinding
import com.example.mvvm.datalayer.entity.weather.Daily
import com.example.mvvm.datalayer.entity.weather.Hourly
import com.example.mvvm.datalayer.entity.weather.WeatherData
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
//    private var hoursListAdapter = HoursAdapter(arrayListOf())
//    private var daysListAdapter = DaysAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteCityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FavoriteCityDetailsViewModel::class.java)

//        initUI()
        hourlyDailyListener()
        Log.i("test", "welcome FavDetails")
        // Get the Intent that started this activity and extract the string
        val lat = intent.getStringExtra("LAT")
        val lon = intent.getStringExtra("LON")

        if (lat != null && lon != null) {   //  8-3
            Log.i("test", "welcome FavDetails lat $lat")
            Log.i("test", "welcome FavDetails lon $lon")
            viewModel.getCity(lat, lon)
        }

    }

    override fun onStart() {
        super.onStart()
        observeViewModel(viewModel)
    }

//    private fun initUI() {
//        binding.hourlyRecyclerView.apply {
//            layoutManager =
//                LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
//            adapter = hoursListAdapter
//        }
//        binding.dailyRecyclerView.apply {
//            layoutManager =
//                LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
//            adapter = daysListAdapter
//        }
//    }

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

    private fun observeViewModel(viewModel: FavoriteCityDetailsViewModel) {
        viewModel.loadingLiveData.observe(this, { showLoading(it) })
        viewModel.errorLiveData.observe(this, { showError(it) })
        viewModel.cityLiveData.observe(this, { updateUI(it) })
    }

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
        } else {
            binding.listError.visibility = View.GONE
            binding.homeDataContainer.visibility = View.VISIBLE
            binding.homeHoursAndDailyContainer.visibility = View.VISIBLE
        }
    }
    private fun updateUI(it: WeatherData) {
        val cityTime = it.current?.dt
        binding.homeMainCityNameTV.text = it.timezone
        binding.homeMainDescTV.text = it.current?.weather?.get(0)?.description
        binding.homeMainDateTV.text = cityTime?.let { it1 -> convertLongToDateString(it1, "EEE, d MMM") }
        binding.homeMainTempTV.text = it.current?.temp?.toInt().toString().plus("°")
        binding.homeMainHumidityTVVal.text = it.current?.humidity.toString().plus(" %")
        binding.homeMainPressureTVVal.text = it.current?.pressure.toString().plus(" ${resources.getString(R.string.hPa)}")
        binding.homeMainWindTVVal.text = it.current?.wind_speed?.toInt().toString().plus(" ${resources.getString(R.string.met_per_sec)}")
        binding.homeMainCloudsTVVal.text = it.current?.clouds.toString().plus(" %")
//        hoursListAdapter.updateHours(it.hourly as List<Hourly>)
//        daysListAdapter.updateDays(it.daily as List<Daily>)

        binding.loadingView.visibility = View.GONE//                              why


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