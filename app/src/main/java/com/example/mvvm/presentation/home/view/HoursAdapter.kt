package com.example.Weathalert.home.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.Weathalert.R
import com.example.Weathalert.databinding.HoursCellBinding
import com.example.mvvm.datalayer.entity.weather.Hourly
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class HoursAdapter(var hours: ArrayList<Hourly>) : RecyclerView.Adapter<HoursAdapter.HoursVH>() {

    lateinit var binding: HoursCellBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoursVH {
        binding = HoursCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HoursVH(binding, parent.context)
    }

    override fun onBindViewHolder(holder: HoursVH, position: Int) {
        holder.bind(hours[position])
    }

    override fun getItemCount(): Int {
        return hours.size
    }

    fun updateHours(newHours: List<Hourly>) {
        hours.clear()
        hours.addAll(newHours)
        notifyDataSetChanged()
    }

    class HoursVH(val binding: HoursCellBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(hours: Hourly) {
            binding.hoursCellHour.text = hours.dt?.let { dateConverter(it) }
            binding.hoursCellIcon?.setImageResource(getResId(hours.weather?.get(0)?.icon))
            Log.i("res", "binddd")
            binding.hoursCellTemp.text = (hours.temp)?.toInt().toString().plus("°")
        }

        private fun dateConverter(dt: Int): String {
            val calender = Calendar.getInstance()
            calender.timeInMillis = (dt)*1000L
            val dateFormat = SimpleDateFormat("k") //"ka"
            var res = dateFormat.format(calender.time)
            res = amOrpm(res)
            return res
        }

        private fun amOrpm(time: String): String {
            Log.i("time", "amPm start $time")
            var tm = time.toInt()
            if (tm > 11 && tm < 24) {
                Log.i("time", "amPm >12 $tm")
                if (tm != 12)
                    tm -= 12
                return tm.toString()
                    .plus(" ${context.resources.getString(R.string.home_hourly_pm)}")
            }
            Log.i("time", "else>12 $tm")
            if (tm == 24)
                tm -= 12
            return "${(tm)} ${context.resources.getString(R.string.home_hourly_am)}"
        }

        private fun getResId(icon: String?): Int {
            return when(icon){
                "01d" -> R.drawable.d_oneone
                "01n" -> R.drawable.d_one_n
                "02d" -> R.drawable.d_two
                "02n" -> R.drawable.d_two_n
                "03d", "03n", "04d", "04n" -> R.drawable.d_three_four
                "09d", "09n" -> R.drawable.d_nine
                "10d", "10n" -> R.drawable.d_ten
                "11d", "11n" -> R.drawable.d_eleven
                "13d", "13n" -> R.drawable.d_thirteen
                "50d", "50n" -> R.drawable.d_fifty
                else -> R.drawable.d_two
            }
        }
    }
}
