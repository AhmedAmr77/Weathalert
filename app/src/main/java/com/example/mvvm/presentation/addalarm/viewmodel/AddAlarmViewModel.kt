package com.example.mvvm.presentation.addalarm.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mvvm.domain.repositories.AlarmRepository
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import kotlinx.coroutines.*

class AddAlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val alarmRepository = AlarmRepository(getApplication())

    val lastAlarmLiveData = MutableLiveData<AlarmData>()


    fun addAndgetLastAlarm(alarm: AlarmData) {
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            Log.i("alarm", "AddAlarm VM getAlarm exception  ${th.message}")
        }
        CoroutineScope(Dispatchers.IO + exceptionHandlerException).launch {
            Log.i("alarm", "addAlarm start coro")
            alarmRepository.addAlarm(alarm)
            lastAlarmLiveData.postValue(alarmRepository.getLastAlarm())
            Log.i("alarm", "AddAlarm VM addAlarm success add to local")
        }
    }



}