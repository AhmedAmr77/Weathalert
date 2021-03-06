package com.example.Weathalert.favoritecities.view

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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.Weathalert.R
import com.example.Weathalert.databinding.ActivityFavoriteBinding
import com.example.Weathalert.datalayer.entity.WeatherData
import com.example.mvvm.favoritecities.view.FavoriteAdapter
import com.example.mvvm.favoritecities.viewmodel.FavoriteViewModel
import com.example.mvvm.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener
import kotlinx.coroutines.NonCancellable.cancel


class FavoriteActivity : AppCompatActivity() {

    private lateinit var viewModel: FavoriteViewModel
    lateinit var binding: ActivityFavoriteBinding
    private var citisListAdapter = FavoriteAdapter(arrayListOf())
    private lateinit var citiesList : List<WeatherData>

    private var transaction : FragmentTransaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        initUI()
        observeViewModel(viewModel)

        favCitiesFabListener()

        SwipDeleteRecyclerViewCell()

    }

    private fun favCitiesFabListener() {            ///WRITE IT IN VIEWMODEL
        val fab: View = binding.addFavCityFab
        fab.setOnClickListener {         //view ->
            viewModel.showAutoComplete()
        }
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
        /*   wakeup
        viewModel.fetchData().observe(this, Observer {
            if (it != null) {
                citiesList = it
                updateUI(it)
            }
        })
         */
        viewModel.fetchFavCities().observe(this, Observer {
            citiesList = it
            updateUI(it)
        })
        viewModel.searchContainerLiveData.observe(this, Observer {
            showSearchContainer()
        })
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
                viewModel.savaFavCity(carmenFeature.center()?.latitude().toString(), carmenFeature.center()?.longitude().toString())
            }

            override fun onCancel() {
                Log.i("places","cancel")
                supportFragmentManager?.beginTransaction()?.remove(autocompleteFragment)?.commit()
                binding.searchFragmentContainer.visibility= View.GONE
            }
        })
    }

    private fun updateUI(it: List<WeatherData>) {
        citisListAdapter.updateHours(it)
    }

    private fun SwipDeleteRecyclerViewCell() {
        val background =  ColorDrawable(Color.RED)
        val icon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_delete_24);
        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: ViewHolder, target: ViewHolder): Boolean {
                    Log.i("RycVi", "onMove")
                    return false // true if moved, false otherwise
                }

                override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                    //showConfirmationDialog()
                    MaterialAlertDialogBuilder(this@FavoriteActivity)
                        .setTitle(resources.getString(R.string.deleteDialogtitle))
                        .setMessage(resources.getString(R.string.deleteDialogSupportingText))
                        .setNeutralButton(resources.getString(R.string.deleteDialogCancel)) { dialog, which ->
//                            citisListAdapter.notifyDataSetChanged()
                            Log.i("dialog", "Cancel")
                        }
                        .setNegativeButton(resources.getString(R.string.deleteDialogDelete)) { dialog, which ->
                            // remove from adapter
                            viewModel.deleteCity(citiesList[viewHolder.adapterPosition])
                            viewModel.fetchFavCities().observe(this@FavoriteActivity, Observer {
                                if (it != null) {
                                    citiesList = it
                                    updateUI(it)
                                }
                            })
                            Log.i("dialog", "Delete")
                        }
                        .setOnDismissListener { citisListAdapter.notifyDataSetChanged()
                                                Log.i("dialog", "Dismiss Listener") }
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setCancelable(false)
                        .show()
                }
                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: ViewHolder, dX: Float, dY: Float,
                                         actionState: Int, isCurrentlyActive: Boolean) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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

            })
        mIth.attachToRecyclerView(binding.citiesRecyclerView)
    }

}