package com.example.mvvm.presentation.alarm.view

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
import com.example.Weathalert.databinding.ActivityAlarmBinding
import com.example.mvvm.presentation.alarm.viewmodel.AlarmViewModel
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import com.example.mvvm.datalayer.entity.alarm.Days
import com.example.mvvm.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class AlarmActivity : AppCompatActivity() {

    private lateinit var viewModel: AlarmViewModel
    lateinit var binding: ActivityAlarmBinding
    private lateinit var calenderEvent : Calendar
    private lateinit var alarmList : List<AlarmData>
    private lateinit var lastAlarm : AlarmData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)
        calenderEvent = Calendar.getInstance()


        setListeners()

    }

    override fun onStart() {
        super.onStart()
        observeViewModel(viewModel)

    }



    private fun observeViewModel(viewModel: AlarmViewModel) {
        viewModel.alarmsLiveData.observe(this, { alarmList = it })
        viewModel.lastAlarmLiveData.observe(this, {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                registerNotifi(it)
            } else {
                Log.i("alarm", "nott Marshmello")
                Toast.makeText(this, "NOT MARSH", Toast.LENGTH_LONG).show()
            }
        })
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
            typee = typeCheck()
            days = daysCheck()
//            value = valueCheck(typee)

            if (typee.isNullOrEmpty()){
                Toast.makeText(this, "please select type", Toast.LENGTH_LONG).show()
            }
            else if (days.isAllFalse()){
                Toast.makeText(this, "please select day(s)", Toast.LENGTH_LONG).show()
            }
//            else if(value == 777){
//                Toast.makeText(this, "please enter value", Toast.LENGTH_LONG).show()
//            }



            //VALUE
            value = binding.numTextEdit.text.toString().toInt()

            //isGREATER
            isGreater = false
            var selecteedId = binding.isGreaterRadioGroup.checkedRadioButtonId
            if (selecteedId == -1){
                Toast.makeText(this, "please select greater or less", Toast.LENGTH_LONG).show()
            } else {
                isGreater = selecteedId == R.id.greatRadio
                Log.i("alarm", "isGreat Actv = $isGreater")
            }


            addDataAndRegister(days, typee, 1,value, isGreater)


        }
    }

    private fun typeCheck(): String{
        val selectedId = binding.typeRadioGroup.checkedRadioButtonId
        if (selectedId == -1){
            return ""
        } else {
            Log.i("alarm", "type check Actv")
            return (findViewById(selectedId) as RadioButton).text.toString()

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
        Log.i("alarm", "days in Activity = $days")
        return days
    }

//    private fun valueCheck(type: String): Int {
//        val txt = binding.numTextEdit.text
//        val res : Int
//        if (txt.isNullOrEmpty()) {
//            res = when (type) {
//                "Temp" -> 777
//                "Wind" -> 777
//                "Clouds" -> 777
//                else -> 0
//            }
//            return res
//        }
//        if (txt.isNullOrEmpty())
//            return 777
//        Log.i("chbx", "valuecheck")
//        return txt.toString().toInt()
//    }




    private fun addDataAndRegister( days: Days, type: String, reapeat: Int, value:Int, isGreater: Boolean){
        val alarmData = AlarmData(days = days, Type = type, reapeat = reapeat, value = value, isGreater = isGreater)
//        addAlarm(alarmData)
        addAndGetLastAlarm(alarmData)
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            registerNotifi(lastAlarm)
//        } else {
//            Log.i("alarm", "nott Marshmello")
//            Toast.makeText(this, "NOT MARSH", Toast.LENGTH_LONG).show()
//        }
    }


//    private fun addAlarm(alarm: AlarmData) {
////        val alarmData = AlarmData(555L, Days(false, true), "Temp", 1, 7, true)
////        Log.i("alarm", "from Activity ${alarmData}")
//        viewModel.addAlarm(alarm)
//        Log.i("alarm", "from Activity after call")
//    }
    private fun addAndGetLastAlarm(alarm: AlarmData) {
//        val alarmData = AlarmData(555L, Days(false, true), "Temp", 1, 7, true)
//        Log.i("alarm", "from Activity ${alarmData}")
        viewModel.addAndgetLastAlarm(alarm)
        Log.i("alarm", "from Activity after call")
    }

    private fun calenderTime(textView: TextView, hour:Int, min:Int){
        TimePickerDialog(this, object: TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
                calenderEvent = Calendar.getInstance()
                calenderEvent.set(Calendar.HOUR_OF_DAY,p1)
                calenderEvent.set(Calendar.MINUTE,p2)
                calenderEvent.set(Calendar.SECOND,0)
                textView.setText(SimpleDateFormat("HH:mm").format(calenderEvent.time))
                Log.i("alarm","time => ${calenderEvent.timeInMillis}")
            }
        }, hour, min, false).show()
    }

//------------------------------------NOTIFICATION--------------------------------------------------
    @RequiresApi(Build.VERSION_CODES.M)
    private fun registerNotifi(alarm: AlarmData){
        val notifyIntent = Intent(this,AlarmReceiver::class.java)
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        for(alarm in alarmList){
            if(Calendar.getInstance().timeInMillis >= calenderEvent.timeInMillis){
                notifyIntent.putExtra(Constants.ALARM_ID,alarm.id)
                var time = calenderEvent.timeInMillis
                calenderEvent.timeInMillis = time.plus(Constants.HOUR_24_IN_SECONDS)
                var pendingIntent = PendingIntent.getBroadcast(this,alarm.id,notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis,pendingIntent)
                Log.i("alarm","twentyfour${calenderEvent.timeInMillis}")
            }else{
                notifyIntent.putExtra(Constants.ALARM_ID,alarm.id)
                var pendingIntent = PendingIntent.getBroadcast(this,alarm.id,notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calenderEvent.timeInMillis,pendingIntent)
                Log.i("alarm","twentyfour${calenderEvent.timeInMillis}")
            }
//        }
    }


//------------------------------------------------UNREGISTER----------------------------------------
//    private fun unregisterAlarm(id:Int){
//        val notifyIntent = Intent(context,AlarmReceiver::class.java)
//        var pendingIntent = PendingIntent.getBroadcast(context,id,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT)
//        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        if (alarmManager != null) {
//            alarmManager.cancel(pendingIntent)
//        }
//    }





}