package com.example.mvvm.presentation.alarm.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.Weathalert.databinding.AlarmsCellBinding
import com.example.mvvm.datalayer.entity.alarm.AlarmData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AlarmsAdapter(var alarms: ArrayList<AlarmData>): RecyclerView.Adapter<AlarmsAdapter.AlarmsVH>() {

    lateinit var binding: AlarmsCellBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmsVH {
        binding = AlarmsCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmsVH(binding)
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

    class AlarmsVH(val binding: AlarmsCellBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(alarm: AlarmData) {
            binding.alarmMsgTV.text = alarm.Type.plus(" ${getMsg(alarm)}")
            binding.alarmTimeTV.text = dateConverter(alarm.time)
            binding.alarmDaysTV.text = getSelectedDays(alarm.days.getDays())
        }

        private fun getSelectedDays(days: List<String>): String {
            var daysStr = ""
            for (day in days){
                daysStr += "$day   "
            }
            return daysStr
        }

        private fun getMsg(alarm: AlarmData): String {
            return when(alarm.Type){
                "Temp" -> {
                    "${getReturnMsg(alarm)}°"
                }
                "Wind" -> {
                    "${getReturnMsg(alarm)}" //${ context.resources.getString(R.string.met_per_sec)}" }
                }
                "Clouds" -> {
                    "${getReturnMsg(alarm)} %"
                }
                else -> ""
            }
        }

        private fun getReturnMsg(alarm: AlarmData): String{
            if (alarm.isGreater)
                return " higher than ${alarm.value}"
            return " lower than ${alarm.value}"
        }

        private fun dateConverter(dt: Long): String {
            return SimpleDateFormat("h:mm a").format(dt)
        }
    }

}






