package com.example.Weathalert.favoritecities.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Weathalert.databinding.ActivityFavoriteBinding
import com.example.Weathalert.datalayer.entity.Daily
import com.example.Weathalert.datalayer.entity.Hourly
import com.example.Weathalert.datalayer.entity.WeatherData
import com.example.Weathalert.home.viewmodel.WeatherViewModel
import com.example.mvvm.favoritecities.view.FavoriteAdapter
import com.example.mvvm.favoritecities.viewmodel.FavoriteViewModel

class FavoriteActivity : AppCompatActivity() {

    private lateinit var viewModel: FavoriteViewModel
    lateinit var binding: ActivityFavoriteBinding
    private var citisListAdapter = FavoriteAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        initUI()
        observeViewModel(viewModel)

    }

    private fun initUI() {
        binding.citiesRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            adapter = citisListAdapter
        }
    }

    private fun observeViewModel(viewModel: FavoriteViewModel) {
//        viewModel.loadingLiveData.observe(this, { showLoading(it) })
//        viewModel.errorLiveData.observe(this, { showError(it) })
        viewModel.fetchData().observe(this, Observer {
            if (it != null) {
                updateUI(it)
            }
        })
    }

    private fun updateUI(it: WeatherData) {
        val citiesList = listOf(it)
        citisListAdapter.updateHours(citiesList)
//        daysListAdapter.updateDays(it.daily as List<Daily>)
    }
}
