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
    val alarmDeletedLiveData = MutableLiveData<Int>()

    fun getAllAlarms(){
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            Log.i("alarm", "Alarm VM getAllAlarms exception  ${th.message}")
        }
        CoroutineScope(Dispatchers.IO + exceptionHandlerException).launch {
            Log.i("alarm", "getAllAlarms start coro")
            alarmsListLiveData.postValue(alarmRepository.getAllAlarms())
            Log.i("alarm", "Alarm VM getAllAlarms success add to local")
        }
    }

    fun deleteAlarm(id: Int, index: Int){
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            Log.i("alarm", "Alarm VM deleteAlarm exception  ${th.message}")
        }
        CoroutineScope(Dispatchers.IO + exceptionHandlerException).launch {
            Log.i("alarm", "Alarm VM deleteAlarm start coro")
            alarmRepository.deleteAlarm(id)
            alarmDeletedLiveData.postValue(index)
            Log.i("alarm", "Alarm VM deleteAlarm success delete from local")
        }
    }

}