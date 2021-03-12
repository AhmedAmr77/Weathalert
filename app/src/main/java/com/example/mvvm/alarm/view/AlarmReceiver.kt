package com.example.mvvm.alarm.view

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.Weathalert.datalayer.WeatherRepository
import com.example.mvvm.datalayer.AlarmRepository
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import com.example.mvvm.datalayer.entity.weather.Daily
import com.example.mvvm.datalayer.entity.weather.WeatherData
import com.example.mvvm.utils.Constants
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class AlarmReceiver : BroadcastReceiver() {

    lateinit var context: Context
    var alarmTimeId by Delegates.notNull<Int>()
    lateinit var weatherRepository: WeatherRepository
    lateinit var alarmRepository: AlarmRepository
    lateinit var shPref: SharedPreferences
    lateinit var lat: String
    lateinit var lon: String
    lateinit var currWeatherData: WeatherData
    lateinit var currAlarmData: AlarmData

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        this.context = context
        alarmTimeId = intent.getExtras()!!.getInt(Constants.ALARM_ID)

        getData()



        checkNotificationList(currAlarmData, currWeatherData)


    }

    private fun pushNotification(message: String) {
        val msg = "Be careful, $message"

        TODO("Not yet implemented")//PUSH NOTIFI
    }

    private fun checkNotificationList(currAlarmData: AlarmData, currWeatherData: WeatherData) {
        val notificationList = checkAlrm(currAlarmData, currWeatherData)
        if (notificationList.isNullOrEmpty()){
            for(notify in notificationList){
                pushNotification(notify)
            }
        }
    }

    private fun checkAlrm(currAlarmData: AlarmData, currWeatherData: WeatherData): MutableList<String> {
        val type = currAlarmData.Type
        val days = currAlarmData.days
        val value = currAlarmData.value
        val isGreater = currAlarmData.isGreater
        var resList = mutableListOf<String>()

        for (day in days.getDays()){                    //Fri & Mon
            Log.i("day", "day=> $day")
            var res = ""
            when(day){
                "sat" -> { res = checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                    if (!!res.isNullOrEmpty()){
                        res.plus(" on Saturday")
                        resList.add(res)
                    }}

                "sun" -> { res = checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                if (!!res.isNullOrEmpty()){
                    res.plus(" on Sunday")
                    resList.add(res)
                }}

                "mon" -> { res = checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                            if (!!res.isNullOrEmpty()){
                                res.plus(" on Monday")
                                resList.add(res)
                            }}

                "tue" -> { res = checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                    if (!!res.isNullOrEmpty()){
                        res.plus(" on Tuesday")
                        resList.add(res)
                    }}

                "wed" -> { res = checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                    if (!!res.isNullOrEmpty()){
                        res.plus(" on Wednesday")
                        resList.add(res)
                    }}

                "thu" -> { res = checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                    if (!!res.isNullOrEmpty()){
                        res.plus(" on Thursday")
                        resList.add(res)
                    }}

                "fri" -> { res = checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                    if (!!res.isNullOrEmpty()){
                        res.plus(" on Friday")
                        resList.add(res)
                    }}
            }
        }

        return resList
    }

    private fun checkDay(day: String, type: String, isGreater: Boolean, value: Int, currWeatherDataa: WeatherData): String {
        for (d in currWeatherDataa.daily!!){
            if (day == convertToDay(d?.dt!!)){                       //TO HANDLE LATER //what if user choose first day in DailyObj(there are 8 days, first day is duplicated), now there are two days of this day
                return getMatchType(type, d, isGreater, value)
           }
        }
        return ""
    }

    private fun getMatchType(type: String, d: Daily, isGreater: Boolean, value: Int): String {
        return when(type){
            "Temp" -> checkTemp(d, isGreater, value)
            "Wind" -> checkWind(d, isGreater, value)
            "Clouds" -> checkClouds(d, isGreater, value)
            "Fog", "Haze", "Mist" -> checkFog(d)
            "Storm" -> checkStorm(d)
            "Snow" -> checkSnow(d)
            "Rain" -> checkRain(d)
            else -> ""
        }
    }

    private fun checkRain(d: Daily): String {
        if (d.weather?.get(0)?.main == "Rain")
            return "There is Rain"
        return ""
    }

    private fun checkSnow(d: Daily): String {
        if (d.weather?.get(0)?.main == "Snow")
            return "There is Snow"
        return ""
    }

    private fun checkStorm(d: Daily): String {
        if (d.weather?.get(0)?.main == "Thunderstorm")
            return "There is Thunderstorm"
        return ""
    }

    private fun checkFog(d: Daily): String {
        if (d.weather?.get(0)?.main == "Fog")
            return "There is Fog"
        if (d.weather?.get(0)?.main == "Mist")
            return "There is Mist"
        if (d.weather?.get(0)?.main == "Haze")
            return "There is Haze"
        return ""
    }

    private fun checkClouds(d: Daily, greater: Boolean, value: Int): String {
        if (greater){
            if (d.clouds!! > value)
                return "Clouds is $value"
        } else {
            if (d.clouds!! < value)
                return "Clouds is $value"
        }
        return ""
    }

    private fun checkWind(d: Daily, greater: Boolean, value: Int): String {
        if (greater){
            if (d.wind_speed!! > value)
                return "Wind is $value"
        } else {
            if (d.temp?.day!! < value)
                return "Wind is $value"
        }
        return ""
    }

    private fun checkTemp(d: Daily, greater: Boolean, value: Int): String {
        if (greater){
            if (d.temp?.day!! > value)
                return "Temperture is $value"
        } else {
            if (d.temp?.day!! < value)
                return "Temperture is $value"
        }
        return ""
    }

    private fun convertToDay(dt: Int): String {
        val calender = Calendar.getInstance()
        calender.timeInMillis = (dt)*1000L
        val dateFormat = SimpleDateFormat("EE");
        return dateFormat.format(calender.time).toLowerCase()
    }

    //-----------------------------------GETDATA--------------------------------------------------------
    private fun getData(){
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            Log.i("test", "AlrmRec getCurrWeathData from ExceptionHandlerr ${th.message}")
        }
        runBlocking(Dispatchers.IO+exceptionHandlerException){
            launch {
                currWeatherData = getCurrentWeatherData()
                Log.i("test", "AlrmRec getCurrWeathData success")
                currAlarmData =  getCurrentAlarmData(alarmTimeId)
            }
        }
    }
    private fun initVar(app: Application) {
        shPref = app.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        lat = shPref.getString(Constants.LATITUDE,"0").toString()
        lon = shPref.getString(Constants.LONGITUDE,"0").toString()
        Log.i("db", "init: $lat and $lon ")
        Log.i("db", "${shPref.getString(Constants.LATITUDE,"0").toString()}\n${shPref.getString(Constants.LONGITUDE,"0").toString()}")
    }
    private suspend fun getCurrentWeatherData(): WeatherData{
        initVar(context as Application)
        return weatherRepository.getCity(lat, lon)
    }
    private suspend fun getCurrentAlarmData(id: Int): AlarmData{
        return alarmRepository.getAlarm(id)
    }
}

