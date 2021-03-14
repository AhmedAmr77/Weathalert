package com.example.mvvm.presentation.favoritecities.view

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
import java.text.SimpleDateFormat
import java.util.*

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
        binding.homeMainBackground.setImageResource(getModeRes(it))
        Log.i("daay", "icon before => ${it.current?.weather?.get(0)?.icon}")
        binding.homeMainIcon.setImageResource(getResId(it.current?.weather?.get(0)?.icon))
        Log.i("daay", "icon after => ${getResId(it.current?.weather?.get(0)?.icon)}")
        binding.homeMainCityNameTV.text = it.timezone
        binding.homeMainDescTV.text = it.current?.weather?.get(0)?.description
        binding.homeMainDateTV.text = cityTime?.let { it1 -> dateConverter(it1, "EEE, d MMM") }
        binding.homeMainTempTV.text = it.current?.temp?.toInt().toString().plus("°")
        binding.homeMainHumidityTVVal.text = it.current?.humidity.toString().plus(" %")
        binding.homeMainPressureTVVal.text = it.current?.pressure.toString().plus(" ${resources.getString(R.string.hPa)}")
        binding.homeMainWindTVVal.text = it.current?.wind_speed?.toInt().toString().plus(" ${resources.getString(R.string.met_per_sec)}")
        binding.homeMainCloudsTVVal.text = it.current?.clouds.toString().plus(" %")
        hoursListAdapter.updateHours(it.hourly as List<Hourly>)
        daysListAdapter.updateDays(it.daily as List<Daily>)

        binding.loadingView.visibility = View.GONE     //      why
    }

    private fun dateConverter(dt: Int, pattern: String): String {
        val calender = Calendar.getInstance()
        calender.timeInMillis = (dt)*1000L
        val dateFormat = SimpleDateFormat(pattern)
        return dateFormat.format(calender.time)
    }

    private fun getModeRes(weatherData: WeatherData): Int {
        if(weatherData.current?.weather?.get(0)?.icon?.get(2).toString() == "n"){
            return R.drawable.night_wall
        }
        return R.drawable.day_wall
    }

    private fun getResId(icon: String?): Int {
        Log.i("icon", "startHomeGET ${icon}")
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