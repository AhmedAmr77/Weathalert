package com.example.mvvm.presentation.alarm.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import com.example.mvvm.domain.repositories.AlarmRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmsViewModel (application: Application) : AndroidViewModel(application) {

    private val alarmRepository = AlarmRepository(getApplication())

    val alarmsListLiveData = MutableLiveData<List<AlarmData>>()

    fun getAllAlarms(){
        CoroutineScope(Dispatchers.IO).launch {
            alarmsListLiveData.postValue(alarmRepository.getAllAlarms())
        }
    }

    fun deleteAlarm(id: Int, index: Int){
        CoroutineScope(Dispatchers.IO).launch {
            alarmRepository.deleteAlarm(id)
            getAllAlarms()
        }
    }

}