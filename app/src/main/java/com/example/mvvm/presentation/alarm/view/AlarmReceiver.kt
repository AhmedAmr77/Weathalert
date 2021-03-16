package com.example.mvvm.presentation.alarm.view

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.Log
import com.example.Weathalert.R
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import com.example.mvvm.datalayer.entity.weather.Daily
import com.example.mvvm.datalayer.entity.weather.WeatherData
import com.example.mvvm.domain.repositories.AlarmRepository
import com.example.mvvm.domain.repositories.WeatherRepository
import com.example.mvvm.presentation.addalarm.viewmodel.AddAlarmViewModel
import com.example.mvvm.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
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
    lateinit var currLocale: Locale

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
        Log.i(
            "alarm",
            "ON RECEIVE BBBBBBBBBBBBBBBBBBBBBBBBBBBB ${currWeatherData.timezone} &${convertToDay(
                currWeatherData.daily?.get(0)?.dt!!.toInt())} & ${currAlarmData.id} & ${currAlarmData.Type}"
        )
//        checkNotificationList(currAlarmData, currWeatherData, context)
        checkAlrm(currAlarmData, currWeatherData)
//        pushNotification(context, "asdfghjkl")



    }

    private fun sendNotification(res: String) {
        var builder = Notification.Builder(context)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                "Weathalert",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
            builder = Notification.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
        } else {
            builder = Notification.Builder(context)
        }
        builder.setContentTitle(getLocaleStringResource(R.string.app_name))
        builder.setContentText(getLocaleStringResource(R.string.alarm_beCare))
        builder.setSmallIcon(R.drawable.ic_baseline_alarm_on_24)
        builder.setStyle(Notification.BigTextStyle().bigText(res))
        val notification: Notification = builder.build()
        manager.notify(currAlarmData.id, notification)
    }

    private fun checkAlrm(currAlarmData: AlarmData, currWeatherData: WeatherData) {
        Log.i("alarm", "ON checkAlrm EEEEEEEEEEEEEEEEEEEEEEEEE")

        val type = currAlarmData.Type
        val days = currAlarmData.days
        val value = currAlarmData.value
        val isGreater = currAlarmData.isGreater
        var res = ""

        for (day in days.getDays()){                    //Fri & Mon
            Log.i("alarm", "day=> $day")
            var str = ""
            when(day){
                "sat" -> { str = checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                    if (!str.isNullOrEmpty()) {
                        Log.i("alarm", "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ SAT => ${res}")
//                        res = res.plus(" on Saturday\n")
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${
                            getLocaleStringResource(R.string.days_sat)}\n"
                        Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU SAT => ${res}")
//                        resList.add(res)
                    }
                }

                "sun" -> { str = checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                    if (!str.isNullOrEmpty()) {
                        Log.i("alarm", "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ SUN => ${res}")
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${
                            getLocaleStringResource(
                                R.string.days_sun
                            )
                        }\n"
                        Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU SUN => ${res}")
//                    resList.add(res)
                    }
                }

                "mon" -> {
                    str = checkDay(
                        day,
                        type,
                        isGreater,
                        value,
                        currWeatherData
                    )             //match & check with Mon
                    if (!str.isNullOrEmpty()) {
                        Log.i(
                            "alarm",
                            "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ MON => ${res}"
                        )
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${
                            getLocaleStringResource(
                                R.string.days_mon
                            )
                        }\n"
                        Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU MON => ${res}")
//                                resList.add(res)
                    }
                }

                "tue" -> {
                    str = checkDay(
                        day,
                        type,
                        isGreater,
                        value,
                        currWeatherData
                    )             //match & check with Mon
                    if (!str.isNullOrEmpty()) {
                        Log.i("alarm", "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ TUE => ${res}")
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${
                            getLocaleStringResource(
                                R.string.days_tue
                            )
                        }\n"
                        Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU TUE => ${res}")
//                        resList.add(res)
                    }
                }

                "wed" -> {
                    str = checkDay(
                        day,
                        type,
                        isGreater,
                        value,
                        currWeatherData
                    )             //match & check with Mon
                    if (!str.isNullOrEmpty()) {
                        Log.i("alarm", "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ WED => ${res}")
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${
                            getLocaleStringResource(
                                R.string.days_wed
                            )
                        }\n"
                        Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU WED => ${res}")
//                        resList.add(res)
                    }
                }

                "thu" -> {
                    str = checkDay(
                        day,
                        type,
                        isGreater,
                        value,
                        currWeatherData
                    )             //match & check with Mon
                    if (!str.isNullOrEmpty()) {
                        Log.i("alarm", "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ THU => ${res}")
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${
                            getLocaleStringResource(
                                R.string.days_thu
                            )
                        }\n"
                        Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU THU => ${res}")
//                        resList.add(res)
                    }
                }

                "fri" -> {
                    str = checkDay(
                        day,
                        type,
                        isGreater,
                        value,
                        currWeatherData
                    )             //match & check with Mon
                    if (!str.isNullOrEmpty()) {
                        Log.i("alarm", "ON checkAlrm QQQQQQQQQQQQQQQQQQQQQQQQ FRI => ${res}")
//                        res = res.plus(" on Friday\n")
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${
                            getLocaleStringResource(
                                R.string.days_fri
                            )
                        }\n"
                        Log.i("alarm", "ON checkAlrm UUUUUUUUUUUUUUUUUUUUUUU FRI => ${res}")
//                        resList.add(res)
                    }
                }
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
        Log.i("alarm", "ON checkDay FFFFFF day => ${day}")

        for (d in currWeatherDataa.daily!!){
            Log.i("alarm", "ON checkDay FFFFFF dailyDT => ${convertToDay(d?.dt!!)}")
            if (day == convertToDay(d?.dt!!)){                       //TO HANDLE LATER //what if user choose first day in DailyObj(there are 8 days, first day is duplicated), now there are two days of this day
                Log.i("alarm", "ON checkDay IIIIIIIIIIIIIIIIIIIIIIIIIII r => ${type} on day => ${day}")
                val r=  getMatchType(type, d, isGreater, value)
                Log.i("alarm", "ON checkDay OOOOOOOOOOOOOOOOOO r => ${r}")
                return r
            } else{
                Log.i("alarm", "ON checkDay FFFFFF ELSEEEE")

            }
        }
        Log.i("alarm", "ON checkDay PPPPPPPPPPPPPPPPPPPPPPP r => ${type}")
        return ""
    }

    private fun getMatchType(type: String, d: Daily, isGreater: Boolean, value: Int): String {
        val loacleType = getLocaleStringResource(R.string.add_alrm_temp)
        Log.i("alarm", "ON getMatchType GGG0000 $type && $loacleType")
        return when(type){
            getLocaleStringResource(R.string.add_alrm_temp) -> checkTemp(d, isGreater, value)
            getLocaleStringResource(R.string.add_alrm_wind) -> checkWind(d, isGreater, value)
            getLocaleStringResource(R.string.add_alrm_Clouds) -> checkClouds(d, isGreater, value)
            getLocaleStringResource(R.string.add_alrm_Fog) -> checkFog(d)
            getLocaleStringResource(R.string.add_alrm_Thunderstorm) -> checkThunderstorm(d)
            getLocaleStringResource(R.string.add_alrm_Snow) -> checkSnow(d)
            getLocaleStringResource(R.string.add_alrm_Rain) -> checkRain(d)
            else -> ""
        }
    }

    private fun checkRain(d: Daily): String {
        if (d.weather?.get(0)?.main == "Rain")
            return "${getLocaleStringResource(R.string.alarm_msg_thereWillBe)} ${getLocaleStringResource(R.string.add_alrm_Rain)}"
        return ""
    }

    private fun checkSnow(d: Daily): String {
        if (d.weather?.get(0)?.main == "Snow")
            return "${getLocaleStringResource(R.string.alarm_msg_thereWillBe)} ${getLocaleStringResource(R.string.add_alrm_Snow)}"
        return ""
    }

    private fun checkThunderstorm(d: Daily): String {
        if (d.weather?.get(0)?.main == "Thunderstorm")
            return "${getLocaleStringResource(R.string.alarm_msg_thereWillBe)} ${getLocaleStringResource(
                R.string.add_alrm_Thunderstorm
            )}"
        return ""
    }

    private fun checkFog(d: Daily): String {
        if (d.weather?.get(0)?.main == "Fog")
            return "${getLocaleStringResource(R.string.alarm_msg_thereWillBe)} ${getLocaleStringResource(
                R.string.add_alrm_Fog
            )}"
        return ""
    }

    private fun checkClouds(d: Daily, greater: Boolean, value: Int): String {
        Log.i(
            "alarm",
            "ON checkClouds HHHHHHHHHHHHHHHHHHHHH val => ${value} -- grtr => ${greater} -- dtV => ${d.clouds}"
        )
        val localeValueEn = NumberFormat.getInstance(Locale("en")).format(value).toInt() //locale val to en cause it comes from api in en
        if (greater){
            if (d.clouds!! > localeValueEn){
                Log.i("alarm", "ON checkClouds LLLLLLLLLLLLLLLLLLLLLLLL")
                return getReturnMsg(d.clouds.toInt(), R.string.add_alrm_Clouds, "%")
            }
        } else {
            if (d.clouds!! < localeValueEn){
                Log.i("alarm", "ON checkClouds MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM")
                return getReturnMsg(d.clouds.toInt(), R.string.add_alrm_Clouds, "%")
            }
        }
        Log.i("alarm", "ON checkClouds NNNNNNNNNNNNNNNNNNNNNNNNNNNN")
        return ""
    }

    private fun checkWind(d: Daily, greater: Boolean, value: Int): String {
        val localeValueEn = NumberFormat.getInstance(Locale("en")).format(value).toInt() //locale val to en cause it comes from api in en
        if (greater){
            if (d.wind_speed!! > localeValueEn)
                return getReturnMsg(d.wind_speed.toInt(), R.string.add_alrm_wind, "")
        } else {
            if (d.wind_speed!! < localeValueEn)
                return getReturnMsg(d.wind_speed.toInt(), R.string.add_alrm_wind, "")
        }
        return ""
    }

    private fun checkTemp(d: Daily, greater: Boolean, value: Int): String {
        Log.i(
            "alarm",
            "ON checkTemp RRRRRRRRRRRRRRRRR val => ${value} -- grtr => ${greater} -- dtV => ${d.temp?.day}"
        )
        val localeValueEn = NumberFormat.getInstance(Locale("en")).format(value).toInt() //locale val to en cause it comes from api in en
        if (greater){
            if (d.temp?.day!! > localeValueEn) {
                Log.i("alarm", "ON checkTemp SSSSSSSSSSS")
                return getReturnMsg(d.temp?.day?.toInt(), R.string.add_alrm_temp, "°")
            }
        } else {
            if (d.temp?.day!! < localeValueEn){
                Log.i("alarm", "ON checkTemp ELSE 22   TTTTTTTTTTTT")
                return getReturnMsg(d.temp?.day?.toInt(), R.string.add_alrm_temp, "°")
            }
        }
        Log.i("alarm", "ON checkTemp ELSE UUUUUUUUUUUUU")
        return ""
    }

    private fun getReturnMsg(num: Int, resId: Int, symbol: String): String{
        val localeNum = NumberFormat.getInstance(currLocale).format(num)
        return "${getLocaleStringResource(resId)} ${getLocaleStringResource(R.string.alarm_msg_willBe)} ${localeNum}$symbol"
    }

    private fun convertToDay(dt: Int): String {
        val calender = Calendar.getInstance()
        calender.timeInMillis = (dt)*1000L
        val dateFormat = SimpleDateFormat("EE")
        return dateFormat.format(calender.time).toLowerCase()
    }

    fun getLocaleStringResource(resourceId: Int): String {
        val result: String
        val config = Configuration(context.resources.configuration)
        config.setLocale(currLocale)
        result = context.createConfigurationContext(config).getText(resourceId).toString()
        return result
    }

    //-----------------------------------GETDATA--------------------------------------------------------
//    private fun getData(){
//        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
//            Log.i("alarm", "AlrmRec getCurrWeathData from ExceptionHandlerr ${th.message}")
//        }
//        runBlocking(Dispatchers.IO+exceptionHandlerException){
//            launch {
//                currWeatherData = getCurrentWeatherData()
//                Log.i("alarm", "AlrmRec getCurrWeathData success")
//                currAlarmData =  getCurrentAlarmData(alarmTimeId)
//            }
//        }
//    }
    private fun initVar(app: Context) {
        shPref = app.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        currLocale = Locale(shPref.getString(Constants.LANGUAGE_SETTINGS, "en").toString())
        lat = shPref.getString(Constants.LATITUDE, "0").toString()
        lon = shPref.getString(Constants.LONGITUDE, "0").toString()
        Log.i("alarm", "init: $lat and $lon ")
        Log.i(
            "alarm", "${shPref.getString(Constants.LATITUDE, "0").toString()}\n${
                shPref.getString(
                    Constants.LONGITUDE,
                    "0"
                ).toString()
            }"
        )
    }
    private suspend fun getCurrentWeatherData(): WeatherData{

        return weatherRepository.getCity(lat, lon)
    }
    private suspend fun getCurrentAlarmData(id: Int): AlarmData{
        return alarmRepository.getAlarm(id)
    }
}

