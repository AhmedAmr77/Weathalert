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
    private fun updateUI(it: WeatherData) {
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
        binding.loadingView.visibility = View.GONE
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