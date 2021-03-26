package com.example.mvvm.presentation.addalarm.view

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.Weathalert.R
import com.example.Weathalert.databinding.ActivityAddAlarmBinding
import com.example.mvvm.presentation.addalarm.viewmodel.AddAlarmViewModel
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import com.example.mvvm.datalayer.entity.alarm.Days
import com.example.mvvm.presentation.alarm.view.AlarmReceiver
import com.example.mvvm.presentation.alarm.view.AlarmsActivity
import com.example.mvvm.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class AddAlarmActivity : AppCompatActivity() {

    private lateinit var viewModelAdd: AddAlarmViewModel
    lateinit var binding: ActivityAddAlarmBinding
    private lateinit var calenderEvent : Calendar
    private lateinit var alarmList : List<AlarmData>
    private lateinit var lastAlarm : AlarmData

    var days = Days()
    lateinit var typee: String
    var value: Int = 0
    var isGreater: Boolean = false
    var time: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(
            getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).getString(
                Constants.LANGUAGE_SETTINGS,
                "en"
            )!!
        )
        supportActionBar?.title = resources.getString(R.string.menu_alarm)
        binding = ActivityAddAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelAdd = ViewModelProvider(this).get(AddAlarmViewModel::class.java)
        calenderEvent = Calendar.getInstance()

        observeViewModel(viewModelAdd)

        setListeners()
    }

    private fun observeViewModel(viewModelAdd: AddAlarmViewModel) {
        viewModelAdd.lastAlarmLiveData.observe(this, {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                registerNotifi(it)
            } else {
                Toast.makeText(this, "Notifications are not working in this version", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setListeners() {
        binding.timeTV.setOnClickListener {
            calenderTime(it as TextView, calenderEvent.time.hours, calenderEvent.time.minutes)
        }

        binding.saveBtn.setOnClickListener {
            if (checkSelections()){
                addDataAndRegister(days, typee, 1,value, isGreater, time)
                finish()
            }
        }
    }

    private fun setAppLocale(localeCode: String) {
        val resources = resources;
        val dm = resources.getDisplayMetrics()
        val config = resources.configuration
        config.setLocale(Locale(localeCode.toLowerCase()))
        resources.updateConfiguration(config, dm)
    }

    private fun checkSelections(): Boolean {

        typee = typeCheck()
        days = daysCheck()

        if (typee.isNullOrEmpty()){
            Toast.makeText(this, resources.getString(R.string.add_alarm_toast_type), Toast.LENGTH_LONG).show()
            return false
        }
        if (days.isAllFalse()){
            Toast.makeText(this, resources.getString(R.string.add_alarm_toast_days), Toast.LENGTH_LONG).show()
            return false
        }
        if (valueCheck(typee).isNullOrEmpty()){
            Toast.makeText(this, resources.getString(R.string.add_alarm_toast_value), Toast.LENGTH_LONG).show()
            return false
        } else {
            value = valueCheck(typee).toInt()
        }
        var selecteedId = binding.isGreaterRadioGroup.checkedRadioButtonId
        if (selecteedId == -1){
            if (when (typee) {resources.getString(R.string.add_alrm_temp),
                              resources.getString(R.string.add_alrm_wind),
                              resources.getString(R.string.add_alrm_Clouds) -> true
                                                  else -> false }){
                Toast.makeText(this, resources.getString(R.string.add_alarm_toast_isGreat), Toast.LENGTH_LONG).show()
                return false
            } else  {
                isGreater = false
            }
        } else {
            isGreater = selecteedId == R.id.greatRadio
        }
        if (binding.timeTV.text == resources.getString(R.string.add_alarm_timePickerTV)){
            Toast.makeText(this, resources.getString(R.string.add_alarm_toast_pickTime), Toast.LENGTH_LONG).show()
            return false
        }
        if (calenderEvent.timeInMillis < Calendar.getInstance().timeInMillis){
            Toast.makeText(this, resources.getString(R.string.add_alarm_toast_validTime), Toast.LENGTH_LONG).show()
            return false
        }
        else {
            time = calenderEvent.timeInMillis
        }

        return true
    }

    private fun typeCheck(): String{
        val selectedId = binding.typeRadioGroup.checkedRadioButtonId
        return if (selectedId == -1){
            ""
        } else {
            (findViewById<RadioButton>(selectedId)).text.toString()
        }
    }

    private fun daysCheck(): Days {
        var days = Days()
        if (binding.friChBx.isChecked)
            days.fri = true
        if (binding.monChBx.isChecked)
            days.mon = true
        if (binding.sunChBx.isChecked)
            days.sun = true
        if (binding.satChBx.isChecked)
            days.sat = true
        if (binding.tueChBx.isChecked)
            days.tue = true
        if (binding.wedChBx.isChecked)
            days.wed = true
        if (binding.thuChBx.isChecked)
            days.thu = true
        return days
    }

    private fun valueCheck(type: String): String {
        val txt = binding.numTextEdit.text
        if (txt.isNullOrEmpty()) {
            if (when (type) {resources.getString(R.string.add_alrm_temp),
                            resources.getString(R.string.add_alrm_wind),
                            resources.getString(R.string.add_alrm_Clouds) -> false
                                                 else -> true }) {
                return txt.toString()
            } else {
                return ""
            }
        }
        return txt.toString()
    }


    private fun addDataAndRegister( days: Days, type: String, reapeat: Int, value:Int, isGreater: Boolean, time: Long){
        val alarmData = AlarmData(days = days, Type = type, reapeat = reapeat, value = value, isGreater = isGreater, time = time)
        addAndGetLastAlarm(alarmData)
    }

   private fun addAndGetLastAlarm(alarm: AlarmData) {
        viewModelAdd.addAndgetLastAlarm(alarm)
    }


    private fun calenderTime(textView: TextView, hour:Int, min:Int){
        TimePickerDialog(this, object: TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                calenderEvent = Calendar.getInstance()
                calenderEvent.set(Calendar.HOUR_OF_DAY,p1)
                calenderEvent.set(Calendar.MINUTE,p2)
                calenderEvent.set(Calendar.SECOND,0)
                textView.setText(SimpleDateFormat("HH:mm", Locale(Constants.LANGUAGE_SETTINGS)).format(calenderEvent.time))
            }
        }, hour, min, false).show()
    }

//------------------------------------NOTIFICATION--------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.M)
    private fun registerNotifi(alarm: AlarmData){
        val notifyIntent = Intent(this, AlarmReceiver::class.java)
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if(Calendar.getInstance().timeInMillis >= calenderEvent.timeInMillis){
            notifyIntent.putExtra(Constants.ALARM_ID,alarm.id)
            var time = calenderEvent.timeInMillis
            calenderEvent.timeInMillis = time.plus(Constants.HOUR_24_IN_SECONDS)
            var pendingIntent = PendingIntent.getBroadcast(this,alarm.id,notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis,pendingIntent)
        }else{
            notifyIntent.putExtra(Constants.ALARM_ID,alarm.id)
            var pendingIntent = PendingIntent.getBroadcast(this,alarm.id,notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis,pendingIntent)
        }
    }

}