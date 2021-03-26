package com.example.mvvm.presentation.alarm.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Weathalert.R
import com.example.Weathalert.databinding.ActivityAlarmsBinding
import com.example.Weathalert.favoritecities.view.FavoriteActivity
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import com.example.mvvm.datalayer.entity.weather.WeatherData
import com.example.mvvm.presentation.addalarm.view.AddAlarmActivity
import com.example.mvvm.presentation.addalarm.viewmodel.AddAlarmViewModel
import com.example.mvvm.presentation.alarm.viewmodel.AlarmsViewModel
import com.example.mvvm.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class AlarmsActivity : AppCompatActivity() {

    lateinit var binding: ActivityAlarmsBinding
    private lateinit var viewModel: AlarmsViewModel
    private var alarmsListAdapter = AlarmsAdapter(arrayListOf())
    private lateinit var alarmsList : List<AlarmData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(
            getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).getString(
                Constants.LANGUAGE_SETTINGS,
                "en"
            )!!
        )
        supportActionBar?.title = resources.getString(R.string.menu_alarm)
        binding = ActivityAlarmsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AlarmsViewModel::class.java)

        initUI()

        addAlarmFabListener()
        observeViewModel(viewModel)
        SwipDeleteRecyclerViewCell()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getAllAlarms()
    }

    private fun initUI() {
        binding.alarmsRecyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            adapter = alarmsListAdapter
        }
    }

    private fun observeViewModel(viewModel: AlarmsViewModel) {
        viewModel.alarmsListLiveData.observe(this){
            alarmsList = it
            alarmsListAdapter.updateAlarms(it)}
    }

    private fun addAlarmFabListener() {            ///WRITE IT IN VIEWMODEL
        binding.alarmsFab.setOnClickListener {         //view ->
            startActivity(Intent(applicationContext, AddAlarmActivity::class.java))
        }
    }

    private fun setAppLocale(localeCode: String) {
        val resources = resources;
        val dm = resources.getDisplayMetrics()
        val config = resources.configuration
        config.setLocale(Locale(localeCode.toLowerCase()))
        resources.updateConfiguration(config, dm)
    }

    private fun SwipDeleteRecyclerViewCell() {
        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false // true if moved, false otherwise
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    //showConfirmationDialog()
                    MaterialAlertDialogBuilder(this@AlarmsActivity, R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog_Delete)
                        .setTitle(resources.getString(R.string.deleteDialogtitle))
                        .setMessage(resources.getString(R.string.deleteDialogSupportingText))
                        .setPositiveButton(resources.getString(R.string.deleteDialogDelete)){ _, _ ->
                            viewModel.deleteAlarm(alarmsList[viewHolder.adapterPosition].id, viewHolder.adapterPosition)
                            unregisterAlarm(alarmsList[viewHolder.adapterPosition].id)
                        }
                        .setNegativeButton(resources.getString(R.string.deleteDialogCancel)) { _, _ ->
                        }
                        .setOnDismissListener {
                            alarmsListAdapter.notifyDataSetChanged()
                        }
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setCancelable(false)
                        .show()
                }
                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                                         actionState: Int, isCurrentlyActive: Boolean) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    val background =  ColorDrawable(Color.RED)
                    val icon = ContextCompat.getDrawable(this@AlarmsActivity, R.drawable.ic_baseline_delete_24)
                    val itemView = viewHolder.itemView
                    val backgroundCornerOffset = 20
                    val iconMargin: Int = (itemView.height - icon!!.intrinsicHeight) / 2
                    val iconTop: Int = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                    val iconBottom: Int = iconTop + icon.intrinsicHeight

                    val iconLeft: Int = itemView.right - iconMargin - icon.intrinsicWidth
                    val iconRight: Int = itemView.right - iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    background.setBounds(itemView.right + (dX.toInt()) - backgroundCornerOffset,
                        itemView.top, itemView.right, itemView.bottom)
                    background.draw(c)
                    icon.draw(c)
                }
            })
        mIth.attachToRecyclerView(binding.alarmsRecyclerView)
    }

    //------------------------------------------------UNREGISTER----------------------------------------
    private fun unregisterAlarm(id:Int){
        val notifyIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, id,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

}