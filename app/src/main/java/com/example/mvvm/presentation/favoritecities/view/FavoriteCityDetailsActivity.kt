package com.example.mvvm.presentation.favoritecities.view

import android.content.Context
import android.content.SharedPreferences
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
import com.example.mvvm.presentation.favoritecities.viewmodel.FavoriteCityDetailsViewModel
import com.example.mvvm.utils.Constants
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class FavoriteCityDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: FavoriteCityDetailsViewModel
    lateinit var binding: ActivityFavoriteCityDetailsBinding
    private var hoursListAdapter = HoursAdapter(arrayListOf())
    private var daysListAdapter = DaysAdapter(arrayListOf())
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppLocale(getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).getString(Constants.LANGUAGE_SETTINGS, "en")!!)
        supportActionBar?.title = resources.getString(R.string.favCities_label)

        binding = ActivityFavoriteCityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FavoriteCityDetailsViewModel::class.java)

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)

        initUI()
        hourlyDailyListener()
        // Get the Intent that started this activity and extract the string
        val lat = intent.getStringExtra("LAT")
        val lon = intent.getStringExtra("LON")

        if (lat != null && lon != null) {   //  8-3
            viewModel.getCity(lat, lon)
        }

    }

    override fun onStart() {
        super.onStart()
        observeViewModel(viewModel)
    }

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

    private fun setAppLocale(localeCode: String) {
        val resources = resources;
        val dm = resources.getDisplayMetrics()
        val config = resources.configuration
        config.setLocale(Locale(localeCode.toLowerCase()))
        resources.updateConfiguration(config, dm)
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
        binding.textClockHome.timeZone = it.timezone
        val cityTime = it.current?.dt
        binding.homeMainBackground.setImageResource(getModeRes(it))
        binding.homeMainIcon.setImageResource(getResId(it.current?.weather?.get(0)?.icon))
        binding.homeMainCityNameTV.text = it.timezone
        binding.homeMainDescTV.text = it.current?.weather?.get(0)?.description
        binding.homeMainDateTV.text = cityTime?.let { it1 -> dateConverter(it1, "EEE, d MMM") }

        binding.homeMainTempTV.text = "${numLocale(it.current?.temp!!)}°"
        binding.homeMainHumidityTVVal.text = "${numLocale(it.current.humidity?.toDouble()!!)} ${resources.getString(R.string.percent_sign)}"
        binding.homeMainPressureTVVal.text = "${numLocale(it.current.pressure?.toDouble()!!)}  ${resources.getString(R.string.hPa)}"
        binding.homeMainWindTVVal.text = "${numLocale(it.current.wind_speed!!)} ${resources.getString(R.string.met_per_sec)}"
        binding.homeMainCloudsTVVal.text = "${numLocale(it.current.clouds?.toDouble()!!)} ${resources.getString(R.string.percent_sign)}"
        hoursListAdapter.updateHours(it.hourly as List<Hourly>)
        daysListAdapter.updateDays(it.daily as List<Daily>)

        binding.loadingView.visibility = View.GONE     //      why
    }

    private fun numLocale(num: Double): String {
        return NumberFormat.getInstance(Locale
            (sharedPreferences.getString(Constants.LANGUAGE_SETTINGS, "en"))).format(num.toInt())
    }

    private fun dateConverter(dt: Int, pattern: String): String {
        val calender = Calendar.getInstance()
        calender.timeInMillis = (dt) * 1000L
        val dateFormat =
            SimpleDateFormat(pattern, Locale(sharedPreferences.getString(Constants.LANGUAGE_SETTINGS, "en")))
        return dateFormat.format(calender.time)
    }

    private fun getModeRes(weatherData: WeatherData): Int {
        if(weatherData.current?.weather?.get(0)?.icon?.get(2).toString() == "n"){
            return R.drawable.night_wall
        }
        return R.drawable.day_wall
    }

    private fun getResId(icon: String?): Int {
        return  when(icon){
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
    }
}