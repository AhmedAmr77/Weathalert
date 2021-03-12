package com.example.mvvm.alarm.view

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import com.example.Weathalert.R
import com.example.Weathalert.databinding.ActivityAlarmBinding
import com.example.Weathalert.home.viewmodel.HomeViewModel
import com.example.mvvm.alarm.view.viewmodel.AlarmViewModel
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import com.example.mvvm.datalayer.entity.alarm.Days
import java.text.SimpleDateFormat
import java.util.*

class AlarmActivity : AppCompatActivity() {

    private lateinit var viewModel: AlarmViewModel
    lateinit var binding: ActivityAlarmBinding
    private var calenderEvent = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)


        setListeners()


    }

    private fun addAlarm(alarm: AlarmData) {
//        val alarmData = AlarmData(555L, Days(false, true), "Temp", 1, 7, true)
//        Log.i("alarm", "from Activity ${alarmData}")
        viewModel.addAlarm(alarm)
        Log.i("alarm", "from Activity after call")
    }

    private fun setListeners() {

        var days = Days()
        var typee: String
        var value: Int
        var isGreater: Boolean


        binding.timeTV.setOnClickListener {
            calenderTime(it as TextView,calenderEvent.time.hours,calenderEvent.time.minutes)
        }

        binding.saveBtn.setOnClickListener {
            //TYPE
            var selectedId = binding.typeRadioGroup.checkedRadioButtonId
            if (selectedId == -1){
                Toast.makeText(this, "please select type", Toast.LENGTH_LONG).show()
                typee = ""
            } else {
                typee = (findViewById(selectedId) as RadioButton).text.toString()
                Log.i("chbx", "type = $typee")
            }

            //DAYS
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
                days.sun = true
            if (binding.thuChBx.isChecked)
                days.thu = true
            Log.i("chbx", "days = $days")

            //VALUE
            value = 0
            value = binding.numTextEdit.text.toString().toInt()
            Log.i("chbx", "val = $value")

            //isGREATER
            isGreater = false
            var selecteedId = binding.isGreaterRadioGroup.checkedRadioButtonId
            if (selecteedId == -1){
                Toast.makeText(this, "please select greater or less", Toast.LENGTH_LONG).show()
            } else {
                isGreater = selecteedId == R.id.greatRadio
                Log.i("chbx", "isGreat = $isGreater")
            }


            sendData(days, typee, 1,value, isGreater)

        }
    }

    private fun sendData( days: Days, type: String, reapeat: Int, value:Int, isGreater: Boolean){
        val alarmData = AlarmData(days = days, Type = type, reapeat = reapeat, value = value, isGreater = isGreater)
        val alarmData2 = AlarmData(days = days, Type = type, reapeat = 5, value = 77, isGreater = isGreater)
        addAlarm(alarmData)
        addAlarm(alarmData2)

    }

    private fun calenderTime(textView: TextView, hour:Int, min:Int){
        TimePickerDialog(this, object: TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                calenderEvent = Calendar.getInstance()
                calenderEvent.set(Calendar.HOUR_OF_DAY,p1)
                calenderEvent.set(Calendar.MINUTE,p2)
                calenderEvent.set(Calendar.SECOND,0)
                textView.setText(SimpleDateFormat("HH:mm").format(calenderEvent.time))
                Log.i("chbx","time => ${calenderEvent.timeInMillis}")

            }
        }, hour, min, false).show()
    }






}