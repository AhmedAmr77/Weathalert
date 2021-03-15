package com.example.mvvm.presentation.alarm.view

import android.R
import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import com.example.mvvm.domain.repositories.WeatherRepository
import com.example.mvvm.presentation.addalarm.viewmodel.AddAlarmViewModel
import com.example.mvvm.domain.repositories.AlarmRepository
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
    lateinit var viewModelAdd: AddAlarmViewModel

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        this.context = context
        alarmTimeId = intent.extras!!.getInt(Constants.ALARM_ID)

        weatherRepository = WeatherRepository(context.applicationContext as Application)
        alarmRepository = AlarmRepository(context.applicationContext as Application)

            Log.i("alarm", "ON RECEIVE AAAAAAAAAAAAAAAAAAAAAAAAAAA ${alarmTimeId}")
        initVar(context)

//        getData()
        runBlocking(Dispatchers.IO){
            launch {
                currWeatherData = getCurrentWeatherData()
                Log.i("alarm", "AlrmRec getCurrWeathData success")
                currAlarmData =  getCurrentAlarmData(alarmTimeId)
            }
        }
        Log.i("alarm", "ON RECEIVE BBBBBBBBBBBBBBBBBBBBBBBBBBBB ${currWeatherData.timezone} & ${currAlarmData.id} & ${currAlarmData.Type}")
//        checkNotificationList(currAlarmData, currWeatherData, context)
        checkAlrm(currAlarmData, currWeatherData)
//        pushNotification(context, "asdfghjkl")



    }

    private fun sendNotification(res: String) {
        var builder = Notification.Builder(context)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, "WeatherTastic", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
            builder = Notification.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
        } else {
            builder = Notification.Builder(context)
        }
        builder.setContentTitle("Weathalert")
        builder.setContentText("Be careful")
        builder.setSmallIcon(R.drawable.ic_menu_add)
        builder.setStyle(Notification.BigTextStyle().bigText(res))
        val notification: Notification = builder.build()
        manager.notify(currAlarmData.id, notification)
    }

//    private fun pushNotification(context: Context, message: String) {
//        Log.i("alarm", "ON pushNotification DDDDDDDDDDDDDDDDDDDDDDDDDDDDD")
//
//        val msg = "Be careful, $message"
//
//        var builder = Notification.Builder(context)
//        var mngr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
//            builder = Notification.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
//        } else {
//            builder = Notification.Builder(context)
//        }
//        builder.setSmallIcon(R.drawable.ic_dialog_alert)
//        builder.setContentTitle("Weathalert")
//        builder.setContentText("Be careful")
//        builder.setStyle(Notification.BigTextStyle().bigText(msg))
//        val notifi = builder.build()
//        mngr.notify(currAlarmData.id, notifi)
////        builder.setContentTitle("WeatherTastic")
////        builder.setContentText("Weather Report")
////        builder.setSmallIcon(R.drawable.ic_menu_add)
////        builder.setStyle(Notification.BigTextStyle().bigText(result))
////        val notification: Notification = builder.build()
////        manager.notify(10, notification)
//    }

//    private fun checkNotificationList(currAlarmData: AlarmData, currWeatherData: WeatherData, context: Context) {
//        Log.i("alarm", "ON checkNotificationList CCCCCCCCCCCCCCCCCCCCCCCCC")
//        val notificationList = checkAlrm(currAlarmData, currWeatherData)
//        if (notificationList.isNullOrEmpty()){
//            for(notify in notificationList){
//                Log.i("alarm", "ON checkNotificationList KKKKKKKKKKKKKKKKKKKKKKKKKKKKKK")
//                pushNotification(context, notify)
//            }
//        }
//    }

    private fun checkAlrm(currAlarmData: AlarmData, currWeatherData: WeatherData) {
        Log.i("alarm", "ON checkAlrm EEEEEEEEEEEEEEEEEEEEEEEEE")

        val type = currAlarmData.Type
        val days = currAlarmData.days
        val value = currAlarmData.value
        val isGreater = currAlarmData.isGreater
        var res = ""

        for (day in days.getDays()){                    //Fri & Mon
            Log.i("alarm", "day=> $day")

            when(day){
                "sat" -> { res += checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                    if (!res.isNullOrEmpty()){
                        Log.i("alarm", "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ SAT => ${res}")
//                        res = res.plus(" on Saturday\n")
                        res += " on Saturday\n"
                        Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU SAT => ${res}")
//                        resList.add(res)
                    }}

                "sun" -> {res += checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                if (!res.isNullOrEmpty()){
                    Log.i("alarm", "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ SUN => ${res}")
                    res = res.plus(" on Sunday\n")
                    Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU SUN => ${res}")
//                    resList.add(res)
                }}

                "mon" -> { res += checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                            if (!res.isNullOrEmpty()){
                                Log.i("alarm", "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ MON => ${res}")
                                res = res.plus(" on Monday\n")
                                Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU MON => ${res}")
//                                resList.add(res)
                            }}

                "tue" -> { res += checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                    if (!res.isNullOrEmpty()){
                        Log.i("alarm", "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ TUE => ${res}")
                        res = res.plus(" on Tuesday\n")
                        Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU TUE => ${res}")
//                        resList.add(res)
                    }}

                "wed" -> { res += checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                    if (!res.isNullOrEmpty()){
                        Log.i("alarm", "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ WED => ${res}")
                        res = res.plus(" on Wednesday\n")
                        Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU WED => ${res}")
//                        resList.add(res)
                    }}

                "thu" -> { res += checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                    if (!res.isNullOrEmpty()){
                        Log.i("alarm", "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ THU => ${res}")
                        res = res.plus(" on Thursday\n")
                        Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU THU => ${res}")
//                        resList.add(res)
                    }}

                "fri" -> { res += checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                    if (!res.isNullOrEmpty()){
                        Log.i("alarm", "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ FRI => ${res}")
//                        res = res.plus(" on Friday\n")
                        res += " on Friday\n"
                        Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU FRI => ${res}")
//                        resList.add(res)
                    }}
            }

        }
        if (res.isNullOrEmpty()){
            Log.i("alarm", "ON checkAlrm TTTTTTTTTTTTTTTTTTTTTT")
        } else{
//            pushNotification(context, res)
            sendNotification(res)
        }
//        Log.i("alarm", "ON checkAlrm JJJJJJJJJJJJJJJJJJJJJJJJJJJJ ${ resList}")
//        return resList
    }

    @SuppressLint("LogNotTimber")
    private fun checkDay(day: String, type: String, isGreater: Boolean, value: Int, currWeatherDataa: WeatherData): String {
        Log.i("alarm", "ON checkDay FFFFFFFFFFFFFFFFFFFFFFF")

        for (d in currWeatherDataa.daily!!){
            if (day == convertToDay(d?.dt!!)){                       //TO HANDLE LATER //what if user choose first day in DailyObj(there are 8 days, first day is duplicated), now there are two days of this day
                Log.i("alarm", "ON checkDay IIIIIIIIIIIIIIIIIIIIIIIIIII r => ${type}")
                val r=  getMatchType(type, d, isGreater, value)
                Log.i("alarm", "ON checkDay OOOOOOOOOOOOOOOOOO r => ${r}")
                return r
            }
        }
        Log.i("alarm", "ON checkDay PPPPPPPPPPPPPPPPPPPPPPP r => ${type}")
        return ""
    }

    private fun getMatchType(type: String, d: Daily, isGreater: Boolean, value: Int): String {
        Log.i("alarm", "ON getMatchType GGGGGGGGGGGGGGGGGGGGG")

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
        Log.i("alarm", "ON checkClouds HHHHHHHHHHHHHHHHHHHHH val => ${value} -- grtr => ${greater} -- dtV => ${d.clouds}")
        if (greater){
            if (d.clouds!! > value){
                Log.i("alarm", "ON checkClouds LLLLLLLLLLLLLLLLLLLLLLLL")
                val res= "Clouds is $value"
                return res
            }
        } else {
            if (d.clouds!! < value){
                Log.i("alarm", "ON checkClouds MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM")
                return "Clouds is $value"
            }
        }
        Log.i("alarm", "ON checkClouds NNNNNNNNNNNNNNNNNNNNNNNNNNNN")
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
        Log.i("alarm", "ON checkTemp RRRRRRRRRRRRRRRRR val => ${value} -- grtr => ${greater} -- dtV => ${d.temp?.day}")
        if (greater){
            if (d.temp?.day!! > value) {
                Log.i("alarm", "ON checkTemp SSSSSSSSSSS")
                return "Temperture is $value"
            }
        } else {
            if (d.temp?.day!! < value){
                Log.i("alarm", "ON checkTemp ELSE 22   TTTTTTTTTTTT")
                return "Temperture is $value"
            }
        }
        Log.i("alarm", "ON checkTemp ELSE UUUUUUUUUUUUU")
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
            Log.i("alarm", "AlrmRec getCurrWeathData from ExceptionHandlerr ${th.message}")
        }
        runBlocking(Dispatchers.IO+exceptionHandlerException){
            launch {
                currWeatherData = getCurrentWeatherData()
                Log.i("alarm", "AlrmRec getCurrWeathData success")
                currAlarmData =  getCurrentAlarmData(alarmTimeId)
            }
        }
    }
    private fun initVar(app: Context) {
        shPref = app.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        lat = shPref.getString(Constants.LATITUDE,"0").toString()
        lon = shPref.getString(Constants.LONGITUDE,"0").toString()
        Log.i("alarm", "init: $lat and $lon ")
        Log.i("alarm", "${shPref.getString(Constants.LATITUDE,"0").toString()}\n${shPref.getString(Constants.LONGITUDE,"0").toString()}")
    }
    private suspend fun getCurrentWeatherData(): WeatherData{

        return weatherRepository.getCity(lat, lon)
    }
    private suspend fun getCurrentAlarmData(id: Int): AlarmData{
        return alarmRepository.getAlarm(id)
    }
}

