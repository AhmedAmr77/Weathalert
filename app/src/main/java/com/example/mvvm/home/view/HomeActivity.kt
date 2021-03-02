package com.example.Weathalert.home.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Weathalert.R
import com.example.Weathalert.databinding.ActivityHomeBinding
import com.example.Weathalert.datalayer.entity.WeatherData
import com.example.Weathalert.favoritecities.view.FavoriteActivity
import com.example.Weathalert.home.viewmodel.WeatherViewModel
import com.example.Weathalert.settings.view.SettingsActivity
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel
    lateinit var binding: ActivityHomeBinding
    private var hoursListAdapter = HoursAdapter(arrayListOf())
    private var daysListAdapter = DaysAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        observeViewModel(viewModel)
        initUI()

        favCitiesFabListener()

        viewModel.fetchData()
    }

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

    private fun observeViewModel(viewModel: WeatherViewModel) {
        viewModel.loadingLiveData.observe(this, { showLoading(it) })
        viewModel.errorLiveData.observe(this, { showError(it) })
        viewModel.weatherListLiveData.observe(this, { updateUI(it) })
    }

    private fun updateUI(it: WeatherData) {
        val cityTime = it.timezone_offset+it.current.dt
        binding.cityTV.text = it.timezone
        binding.descriptionTV.text = it.current.weather[0].description
        binding.dateTV.text = convertLongToDateString( cityTime,"MM-dd-yyyy")

//        val test= convertLongToDateString(cityTime, "MM-dd-yyyy")
//        val test2= convertLongToDateString(cityTime, "HH:mm")
//        Toast.makeText(this, "curr => ${it.current.dt}\n" +
//                                          "offs => ${it.timezone_offset}\n" +
//                                          "cu+of=>C  $cityTime\n" +
//                                            "test => $test\n" +
//                                            "test2 => $test2", Toast.LENGTH_LONG).show()

        binding.hourTV.text = convertLongToDateString(cityTime, "HH:mm")
        binding.tempTV.text = (it.current.temp- 273.15).toInt().toString()
        binding.humidityValTV.text = it.current.humidity.toString().plus(" %")
        binding.pressureValTV.text = it.current.pressure.toString()
        binding.windValTVa.text = it.current.wind_speed.toString().plus(" m/s")
        binding.cloudsValTV.text = it.current.clouds.toString().plus(" %")
        hoursListAdapter.updateHours(it.hourly)
        daysListAdapter.updateHours(it.daily)
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
            var ldt: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(systemTime.toLong()), ZoneId.systemDefault())
            return ldt.format(DateTimeFormatter.ofPattern(pattern))
        } else {
            return "OS lessThan OREO"
        }
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
        } else {
            binding.listError.visibility = View.GONE
        }
    }

    private fun favCitiesFabListener() {            ///WRITE IT IN VIEWMODEL
        val fab: View = binding.favCitiesFab
        fab.setOnClickListener {         //view ->
            startActivity(Intent(applicationContext, FavoriteActivity::class.java))
        }
    }

}