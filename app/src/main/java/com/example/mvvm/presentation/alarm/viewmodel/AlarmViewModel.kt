package com.example.mvvm.presentation.alarm.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mvvm.domain.repositories.AlarmRepository
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import kotlinx.coroutines.*

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val alarmRepository = AlarmRepository(getApplication())

    val alarmsLiveData = MutableLiveData<List<AlarmData>>()
    val lastAlarmLiveData = MutableLiveData<AlarmData>()


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

    fun addAndgetLastAlarm(alarm: AlarmData) {
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            Log.i("alarm", "Alarm VM getAlarm exception  ${th.message}")
        }
        CoroutineScope(Dispatchers.IO + exceptionHandlerException).launch {
            Log.i("alarm", "addAlarm start coro")
            alarmRepository.addAlarm(alarm)
            lastAlarmLiveData.postValue(alarmRepository.getLastAlarm())
            Log.i("alarm", "Alarm VM addAlarm success add to local")
        }
    }

    fun getAllAlarms(){
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            Log.i("alarm", "Alarm VM getAllAlarms exception  ${th.message}")
        }
        CoroutineScope(Dispatchers.IO + exceptionHandlerException).launch {
            Log.i("alarm", "getAllAlarms start coro")
            alarmsLiveData.postValue(alarmRepository.getAllAlarms())
            Log.i("alarm", "Alarm VM getAllAlarms success add to local")
        }
    }



}