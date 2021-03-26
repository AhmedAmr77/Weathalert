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

        initVar(context)

        runBlocking(Dispatchers.IO){
            launch {
                currWeatherData = getCurrentWeatherData()
                currAlarmData =  getCurrentAlarmData(alarmTimeId)
            }
        }
        checkAlrm(currAlarmData, currWeatherData)
    }

    private fun sendNotification(res: String) {
        var builder = Notification.Builder(context)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, "Weathalert", NotificationManager.IMPORTANCE_HIGH)
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
        val type = currAlarmData.Type
        val days = currAlarmData.days
        val value = currAlarmData.value
        val isGreater = currAlarmData.isGreater
        var res = ""

        for (day in days.getDays()){
            var str = ""
            when(day){
                "sat" -> {
                    str = checkDay(day, type, isGreater, value, currWeatherData)
                    if (!str.isNullOrEmpty()) {
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${
                            getLocaleStringResource(R.string.days_sat)}\n"
                    }
                }

                "sun" -> {
                    str = checkDay(day, type, isGreater, value, currWeatherData)             //match & check with Mon
                    if (!str.isNullOrEmpty()) {
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${
                            getLocaleStringResource(R.string.days_sun)}\n"
                       }
                }

                "mon" -> {
                    str = checkDay(day, type, isGreater, value, currWeatherData)
                    if (!str.isNullOrEmpty()) {
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${getLocaleStringResource(R.string.days_mon)}\n"
                    }
                }

                "tue" -> {
                    str = checkDay(day, type, isGreater, value, currWeatherData)
                    if (!str.isNullOrEmpty()) {
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${getLocaleStringResource(R.string.days_tue)}\n"
                    }
                }

                "wed" -> {
                    str = checkDay(day, type, isGreater, value, currWeatherData)
                    if (!str.isNullOrEmpty()) {
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${getLocaleStringResource(R.string.days_wed)}\n"
                    }
                }

                "thu" -> {
                    str = checkDay(day, type, isGreater, value, currWeatherData)
                    if (!str.isNullOrEmpty()) {
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${getLocaleStringResource(R.string.days_thu)}\n"
                    }
                }

                "fri" -> {
                    str = checkDay(day, type, isGreater, value, currWeatherData)
                    if (!str.isNullOrEmpty()) {
                        res += "$str ${getLocaleStringResource(R.string.alarm_msg_on)} ${getLocaleStringResource(R.string.days_fri)}\n"
                    }
                }
            }
        }
        if (!res.isNullOrEmpty()){
            sendNotification(res)
        }
    }

    @SuppressLint("LogNotTimber")
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
            return "${getLocaleStringResource(R.string.alarm_msg_thereWillBe)} ${getLocaleStringResource(R.string.add_alrm_Thunderstorm)}"
        return ""
    }

    private fun checkFog(d: Daily): String {
        if (d.weather?.get(0)?.main == "Fog")
            return "${getLocaleStringResource(R.string.alarm_msg_thereWillBe)} ${getLocaleStringResource(R.string.add_alrm_Fog)}"
        return ""
    }

    private fun checkClouds(d: Daily, greater: Boolean, value: Int): String {
        val localeValueEn = NumberFormat.getInstance(Locale("en")).format(value).toInt()
        if (greater){
            if (d.clouds!! > localeValueEn)
                return getReturnMsg(d.clouds.toInt(), R.string.add_alrm_Clouds, "%")
        } else {
            if (d.clouds!! < localeValueEn)
                return getReturnMsg(d.clouds.toInt(), R.string.add_alrm_Clouds, "%")
        }
        return ""
    }

    private fun checkWind(d: Daily, greater: Boolean, value: Int): String {
        val localeValueEn = NumberFormat.getInstance(Locale("en")).format(value).toInt()
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
        val localeValueEn = NumberFormat.getInstance(Locale("en")).format(value).toInt() //locale val to en cause it comes from api in en
        if (greater){
            if (d.temp?.day!! > localeValueEn) {
                return getReturnMsg(d.temp.day.toInt(), R.string.add_alrm_temp, "°")
            }
        } else {
            if (d.temp?.day!! < localeValueEn){
                return getReturnMsg(d.temp.day.toInt(), R.string.add_alrm_temp, "°")
            }
        }
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

    private fun getLocaleStringResource(resourceId: Int): String {
        val result: String
        val config = Configuration(context.resources.configuration)
        config.setLocale(currLocale)
        result = context.createConfigurationContext(config).getText(resourceId).toString()
        return result
    }

    private fun initVar(app: Context) {
        shPref = app.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        currLocale = Locale(shPref.getString(Constants.LANGUAGE_SETTINGS, "en").toString())
        lat = shPref.getString(Constants.LATITUDE, "0").toString()
        lon = shPref.getString(Constants.LONGITUDE, "0").toString()
    }

    private suspend fun getCurrentWeatherData(): WeatherData{
        return weatherRepository.getCity(lat, lon)
    }

    private suspend fun getCurrentAlarmData(id: Int): AlarmData{
        return alarmRepository.getAlarm(id)
    }
}