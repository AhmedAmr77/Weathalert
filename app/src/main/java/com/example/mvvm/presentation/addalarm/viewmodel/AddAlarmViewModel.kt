package com.example.mvvm.presentation.addalarm.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mvvm.domain.repositories.AlarmRepository
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import kotlinx.coroutines.*

class AddAlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val alarmRepository = AlarmRepository(getApplication())

    val lastAlarmLiveData = MutableLiveData<AlarmData>()


    fun addAndgetLastAlarm(alarm: AlarmData) {
        CoroutineScope(Dispatchers.IO).launch {
            alarmRepository.addAlarm(alarm)
            lastAlarmLiveData.postValue(alarmRepository.getLastAlarm())
        }
    }

}