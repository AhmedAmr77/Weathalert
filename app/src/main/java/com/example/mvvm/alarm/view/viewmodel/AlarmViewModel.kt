package com.example.mvvm.alarm.view.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.Weathalert.datalayer.WeatherRepository
import com.example.mvvm.datalayer.AlarmRepository
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import com.example.mvvm.utils.Constants
import kotlinx.coroutines.*

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val alarmRepository = AlarmRepository(getApplication())


    fun addAlarm(alarm: AlarmData) {
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            Log.i("alarm", "Alarm VM addAlarm exception  ${th.message}")
        }
        CoroutineScope(Dispatchers.IO + exceptionHandlerException).launch {
            Log.i("alarm", "addAlarm start coro")
            alarmRepository.addAlarm(alarm)
            Log.i("alarm", "Alarm VM addAlarm success add to local")
        }
    }

}