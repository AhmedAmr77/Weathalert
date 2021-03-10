package com.example.Weathalert.favoritecities.view

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.Weathalert.R
import com.example.Weathalert.databinding.ActivityFavoriteBinding
import com.example.Weathalert.datalayer.entity.WeatherData
import com.example.mvvm.favoritecities.view.FavoriteAdapter
import com.example.mvvm.favoritecities.view.FavoriteCityDetailsActivity
import com.example.mvvm.favoritecities.viewmodel.FavoriteViewModel
import com.example.mvvm.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener
import kotlinx.coroutines.NonCancellable.cancel


class FavoriteActivity : AppCompatActivity() {

    private lateinit var viewModel: FavoriteViewModel
    lateinit var binding: ActivityFavoriteBinding
    private lateinit var citisListAdapter : FavoriteAdapter
    private lateinit var citiesList : List<WeatherData>

    private var transaction : FragmentTransaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        initUI()

        favCitiesFabListener()

    }

    override fun onStart() {
        super.onStart()
                                                                        citisListAdapter.notifyDataSetChanged()  //try 1
//        Log.i("test", "onStart adptr => ${citisListAdapter.cities} \n listyyy => ${citiesList}")
        observeViewModel(viewModel)
        SwipDeleteRecyclerViewCell()

        //                          GET FAV LIST
        //                                   viewModel.fetchFavCities()  // not needed cause fetch in refreshFavCitiesList()
        //                         REFRESH DATA
        viewModel.refreshFavCitiesList()

    }

    override fun onResume() {
        super.onResume()
                                                                        //citisListAdapter.notifyDataSetChanged() //try 2
    }

    private fun favCitiesFabListener() {            ///WRITE IT IN VIEWMODEL
        val fab: View = binding.addFavCityFab
        fab.setOnClickListener {         //view ->
            showSearchContainer()
        }
    }


    private fun initUI() {
        binding.citiesRecyclerView.apply {
            layoutManager =
                GridLayoutManager(applicationContext, 2, GridLayoutManager.VERTICAL, false)
//                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            citisListAdapter = FavoriteAdapter(arrayListOf()){item ->
                Log.i("test", "Fav cities RecyclerView Listener ${item.lon} & ${item.lat} & ${item.isFavorite} & ${item.current?.temp}")
//                recyclerViewListener(item)
                val intent = Intent(this@FavoriteActivity, FavoriteCityDetailsActivity::class.java).apply {
                    putExtra("LAT", item.lat.toString())
                    putExtra("LON", item.lon.toString())
                    Log.i("test", "Fav putExtrasss intent")
                }
                Log.i("test", "Fav before start new FavDet Activity")
                startActivity(intent)
            }
            adapter = citisListAdapter
        }
    }

    private fun observeViewModel(viewModel: FavoriteViewModel) {
        viewModel.loadingLiveData.observe(this, { showLoading(it) })
        viewModel.errorLiveData.observe(this, { showError(it) })
        viewModel.citisListLiveData.observe(this, Observer {
            citiesList = it
            updateUI(it)
        })
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
//            binding.citiesRecyclerView.visibility = View.GONE
        } else {
            binding.listError.visibility = View.GONE
        }
    }

    private fun showSearchContainer() {
        binding.searchFragmentContainer.visibility= View.VISIBLE

        val autocompleteFragment = PlaceAutocompleteFragment.newInstance(Constants.MAPBOX_API_KEY)
        transaction = supportFragmentManager?.beginTransaction()
        transaction?.add(R.id.searchFragmentContainer, autocompleteFragment, Constants.AUTOCOMPLETE_FRAGMENT_TAG) //replace by binding => R.id.searchFragmentContainer
        transaction?.commit()

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(carmenFeature: CarmenFeature) {
                // TODO: Use the longitude and latitude
                Toast.makeText(applicationContext,"latitude ${carmenFeature.center()?.latitude()} \n longitude ${carmenFeature.center()?.longitude()}"
                    , Toast.LENGTH_LONG).show()
                supportFragmentManager?.beginTransaction()?.remove(autocompleteFragment)?.commit()
                binding.searchFragmentContainer.visibility= View.GONE

                //add lat&long to DB and refresh RecyclerView
                //viewModel.savaFavCity(carmenFeature.center()?.latitude().toString(), carmenFeature.center()?.longitude().toString())

                //add to shared then call fetchData()
                saveCurrentLocationToSharedPref(carmenFeature.center()?.latitude().toString(), carmenFeature.center()?.longitude().toString())
                viewModel.fetchData()
            }

            override fun onCancel() {
                Log.i("test","Fav search canceled")
                supportFragmentManager?.beginTransaction()?.remove(autocompleteFragment)?.commit()
                binding.searchFragmentContainer.visibility= View.GONE
            }
        })
    }

    private fun saveCurrentLocationToSharedPref(latitude: String,longitude: String){
        Log.i("test", "Fav save in shPref lat => ${latitude} and lon => ${longitude}")
        val sharedPref = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(Constants.FAV_LATITUDE,latitude).apply()
        editor.putString(Constants.FAV_LONGITUDE,longitude).apply()
    }

    private fun updateUI(it: List<WeatherData>) {
        citisListAdapter.updateHours(it)
    }

    private fun SwipDeleteRecyclerViewCell() {
        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
                    Log.i("test", "Fav swip to dlt recycler view onMove()")
                    return false // true if moved, false otherwise
                }
                override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                    //showConfirmationDialog()
                    MaterialAlertDialogBuilder(this@FavoriteActivity, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog_Delete)
                        .setTitle(resources.getString(R.string.deleteDialogtitle))
                        .setMessage(resources.getString(R.string.deleteDialogSupportingText))
                        .setPositiveButton(resources.getString(R.string.deleteDialogDelete)){ dialog, which ->
                            // remove from adapter
                            Log.i("test", "onSwiped PstvBtn Before adptr => ${citisListAdapter.cities} \n listyyy => ${citiesList}")
                            viewModel.deleteCity(citiesList[viewHolder.adapterPosition])
                            Log.i("test", "onSwiped PstvBtn middle adptr => ${citisListAdapter.cities} \n listyyy => ${citiesList}")
                            viewModel.fetchFavCities()                                                  // remove these two from here
//                            Log.i("test", "Fav swip to dlt recycler view onSwiped() DeleteBtn")
                            Log.i("test", "onSwiped PstvBtn after adptr => ${citisListAdapter.cities} \n listyyy => ${citiesList}")
                        }
                        .setNegativeButton(resources.getString(R.string.deleteDialogCancel)) { dialog, which ->
//                            Log.i("test", "Fav swip to dlt recycler view onSwiped() CancelBtn")
                            Log.i("test", "onSwiped NgtvBtn adptr => ${citisListAdapter.cities} \n listyyy => ${citiesList}")
                        }
                        .setOnDismissListener {
                            citisListAdapter.notifyDataSetChanged()                                  // to here    &&    u can remove this
                            Log.i("test", "onSwiped Dismiss adptr => ${citisListAdapter.cities} \n listyyy => ${citiesList}")
//                            Log.i("test", "Fav swip to dlt recycler view onSwiped() DismissBtn Listener")
                        }  //mlhash lazma

                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setCancelable(false)
                        .show()
                }
                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder, dX: Float, dY: Float,
                                         actionState: Int, isCurrentlyActive: Boolean) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    val background =  ColorDrawable(Color.RED)
                    val icon = ContextCompat.getDrawable(this@FavoriteActivity, R.drawable.ic_baseline_delete_24)
                    val itemView = viewHolder.itemView
                    val backgroundCornerOffset = 20
                    val iconMargin: Int = (itemView.height - icon!!.intrinsicHeight) / 2
                    val iconTop: Int = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                    val iconBottom: Int = iconTop + icon.intrinsicHeight

                    //if (dX < 0) { // Swiping to the left
                    val iconLeft: Int = itemView.right - iconMargin - icon.intrinsicWidth
                    val iconRight: Int = itemView.right - iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    background.setBounds(itemView.right + (dX.toInt()) - backgroundCornerOffset,
                            itemView.top, itemView.right, itemView.bottom)
                   // }
                    background.draw(c)
                    icon.draw(c)
                }

                override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)
                }
                                    // DELETE  onSelectedChanged() if useless
            })
        mIth.attachToRecyclerView(binding.citiesRecyclerView)
    }

}