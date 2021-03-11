package com.example.Weathalert.home.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.Weathalert.R
import com.example.Weathalert.databinding.HoursCellBinding
import com.example.Weathalert.datalayer.entity.Hourly
import java.io.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HoursAdapter(var hours: ArrayList<Hourly>) : RecyclerView.Adapter<HoursAdapter.HoursVH>() {

    lateinit var binding: HoursCellBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : HoursVH {
        binding = HoursCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HoursVH(binding, parent.context)
    }

    override fun onBindViewHolder(holder: HoursVH, position: Int) {
        holder.bind(hours[position])
    }

    override fun getItemCount(): Int {
        return hours.size
    }

    fun updateHours(newHours: List<Hourly>){
        hours.clear()
        hours.addAll(newHours)
        notifyDataSetChanged()
    }

    class HoursVH(val binding: HoursCellBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hours: Hourly) {
            binding.hoursCellHour.text = hours.dt?.let { convert(it.toLong()) }
      //      binding.hoursCellIcon?.setImageResource(R.drawable.ic_baseline_favorite_24)  //(hours.weather[0].id)
            binding.hoursCellTemp.text = (hours.temp)?.toInt().toString().plus("°")
        }

        private fun convert(time: Long): String {
            var dt = ""
            Log.i("time", "convert start $time")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                dt = Instant.ofEpochSecond(time)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("k"))
                Log.i("time", "convert dt $dt")
                dt = amOrpm(dt)
            } else {
                dt = "NOT OREO"
            }
            return dt
        }

        private fun amOrpm(time: String): String{
            Log.i("time", "amPm start $time")
            var tm = time.toInt()
            if ( tm > 11 && tm<24){
                Log.i("time", "amPm >12 $tm")
                if (tm != 12)
                    tm -= 12
                return tm.toString().plus(" ${context.resources.getString(R.string.home_hourly_pm)}")
            }
            Log.i("time", "else>12 $tm")
            if (tm == 24)
                tm -= 12
            return "${(tm)} ${context.resources.getString(R.string.home_hourly_am)}"
        }
    }
}