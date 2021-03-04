package com.example.Weathalert.favoritecities.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.Weathalert.R
import com.example.Weathalert.databinding.ActivityFavoriteBinding
import com.example.Weathalert.datalayer.entity.WeatherData
import com.example.mvvm.favoritecities.view.FavoriteAdapter
import com.example.mvvm.favoritecities.viewmodel.FavoriteViewModel
import com.example.mvvm.utils.Constants
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener

class FavoriteActivity : AppCompatActivity() {

    private lateinit var viewModel: FavoriteViewModel
    lateinit var binding: ActivityFavoriteBinding
    private var citisListAdapter = FavoriteAdapter(arrayListOf())

    private var transaction : FragmentTransaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        initUI()
        observeViewModel(viewModel)

        favCitiesFabListener()

    }

    private fun favCitiesFabListener() {            ///WRITE IT IN VIEWMODEL
        val fab: View = binding.addFavCityFab
        fab.setOnClickListener {         //view ->
            showAutoComplete()
        }
    }

    private fun showAutoComplete() {
        binding.searchFragmentContainer.visibility= View.VISIBLE
        val autocompleteFragment = PlaceAutocompleteFragment.newInstance(Constants.MAPBOX_API_KEY)
        transaction = supportFragmentManager?.beginTransaction()
        transaction?.add(R.id.searchFragmentContainer, autocompleteFragment, Constants.AUTOCOMPLETE_FRAGMENT_TAG)
        transaction?.commit()

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(carmenFeature: CarmenFeature) {
                // TODO: Use the longitude and latitude
                Toast.makeText(applicationContext,"latitude ${carmenFeature.center()?.latitude()} \n longitude ${carmenFeature.center()?.longitude()}"
                    , Toast.LENGTH_LONG).show()
                supportFragmentManager?.beginTransaction()?.remove(autocompleteFragment)?.commit()
                binding.searchFragmentContainer.visibility= View.GONE

                //add lat&long to DB and refresh RecyclerView

            }

            override fun onCancel() {
                Log.i("places","cancel")
                supportFragmentManager?.beginTransaction()?.remove(autocompleteFragment)?.commit()
                binding.searchFragmentContainer.visibility= View.GONE
            }
        })
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
