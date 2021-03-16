package com.example.mvvm.presentation.alarm.view

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.Weathalert.R
import com.example.Weathalert.databinding.AlarmsCellBinding
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import com.example.mvvm.datalayer.entity.alarm.Days
import com.example.mvvm.utils.Constants
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AlarmsAdapter(var alarms: ArrayList<AlarmData>): RecyclerView.Adapter<AlarmsAdapter.AlarmsVH>() {

    lateinit var binding: AlarmsCellBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmsVH {
        binding = AlarmsCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmsVH(binding, parent.context)
    }

    override fun onBindViewHolder(holder: AlarmsVH, position: Int) {
        holder.bind(alarms[position])
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    fun updateAlarms(newAlarms: List<AlarmData>){
        alarms.clear()
        alarms.addAll(newAlarms)
        notifyDataSetChanged()
    }

//    fun deleteFromList(index: Int) {
//        alarms.removeAt(index)
////        notifyItemChanged(index)
//        notifyDataSetChanged()
//    }

    class AlarmsVH(val binding: AlarmsCellBinding, val context: Context): RecyclerView.ViewHolder(binding.root) {
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)

        fun bind(alarm: AlarmData) {
            binding.alarmMsgTV.text = alarm.Type.plus(" ${getMsg(alarm)}")
            binding.alarmTimeTV.text = dateConverter(alarm.time)
            binding.alarmDaysTV.text =  getSelectedDays(alarm.days.getDays())
        }

        private fun getMsg(alarm: AlarmData): String {
            return when(alarm.Type){
                context.resources.getString(R.string.add_alrm_temp) -> {
                    "${getReturnMsg(alarm)}°"
                }
                context.resources.getString(R.string.add_alrm_wind) -> {
                    "${getReturnMsg(alarm)}" //${ context.resources.getString(R.string.met_per_sec)}" }
                }
                context.resources.getString(R.string.add_alrm_Clouds) -> {
                    "${getReturnMsg(alarm)} %"
                }
                else -> ""
            }
        }

        private fun getReturnMsg(alarm: AlarmData): String{
            val localeNum = NumberFormat.getInstance(Locale(sharedPreferences.getString(Constants.LANGUAGE_SETTINGS, "en"))).format(alarm.value)
            if (alarm.isGreater)
                return " ${context.resources.getString(R.string.higher_than_alarmChBx).toLowerCase()} ${localeNum}"
            return " ${context.resources.getString(R.string.less_than_alarmChBx).toLowerCase()} ${localeNum}"
        }

        private fun dateConverter(dt: Long): String {
            return SimpleDateFormat("h:mm a", Locale(sharedPreferences.getString(Constants.LANGUAGE_SETTINGS, "en"))).format(dt)
        }

        private fun getSelectedDays(days: List<String>): String {
            var daysStr = ""
            val dayLocale = daysLocale(days)
            for (day in dayLocale){
                daysStr += "$day   "
            }
            return daysStr
        }

        private fun daysLocale(days: List<String>): List<String> {
            var list = mutableListOf<String>()
            for (d in days) {
                if (d == "sat")
                    list.add(context.resources.getString(R.string.days_sat))
                if (d == "sun")
                    list.add(context.resources.getString(R.string.days_sun))
                if (d == "mon")
                    list.add(context.resources.getString(R.string.days_mon))
                if (d == "tue")
                    list.add(context.resources.getString(R.string.days_tue))
                if (d == "wed")
                    list.add(context.resources.getString(R.string.days_wed))
                if (d == "thu")
                    list.add(context.resources.getString(R.string.days_thu))
                if (d == "fri")
                    list.add(context.resources.getString(R.string.days_fri))
            }
            return list
        }
    }

}






