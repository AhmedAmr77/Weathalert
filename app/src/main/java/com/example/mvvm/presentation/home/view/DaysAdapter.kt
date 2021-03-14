package com.example.Weathalert.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.Weathalert.R
import com.example.Weathalert.databinding.DaysCellBinding
import com.example.mvvm.datalayer.entity.weather.Daily
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList


class DaysAdapter(var days: ArrayList<Daily>) : RecyclerView.Adapter<DaysAdapter.DaysVH>() {

    lateinit var binding: DaysCellBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysVH {
        binding = DaysCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DaysVH(binding, parent.context)
    }

    override fun onBindViewHolder(holder: DaysVH, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount(): Int {
        return days.size
    }

    fun updateDays(newDays: List<Daily>){
        days.clear()
        days.addAll(newDays)
        notifyDataSetChanged()
    }



    class DaysVH(val binding: DaysCellBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(days: Daily) {
            binding.daysCellIcon.setImageResource(getResId(days.weather?.get(0)?.icon))  //(hours.weather[0].id)
            binding.daysDayTVVal.text = (days.dt?.let { dateConverter(it) })
            binding.daysGrtTempTVVal.text = days.temp?.max?.toInt().toString().plus("°")
            binding.daysSmlTempTVVal.text = days.temp?.min?.toInt().toString().plus("°")
            binding.daysDescTVVal.text = days.weather?.get(0)?.main
            binding.daysHumidityTVVal.text = (days.humidity)?.toString().plus(" %")
            binding.daysCloudsTVVal.text = (days.clouds)?.toString().plus(" %")
            binding.daysPressureTVVal.text = (days.pressure)?.toString().plus(" ${ context.resources.getString(R.string.hPa)}")
            binding.daysWindTVVal.text = (days.wind_speed)?.toString().plus(" ${ context.resources.getString(R.string.met_per_sec)}")
        }

        private fun dateConverter(dt: Int): String {
            val calender = Calendar.getInstance()
            calender.timeInMillis = (dt)*1000L
            val dateFormat = SimpleDateFormat("EEEE")
            return getDay(dateFormat.format(calender.time))
        }

        private fun getDay(day: String): String { //for locale
            return when (day){
                "Monday" -> context.resources.getString(R.string.days_mon)
                "Tuesday" -> context.resources.getString(R.string.days_tue)
                "Wednesday" -> context.resources.getString(R.string.days_wed)
                "Thursday" -> context.resources.getString(R.string.days_thu)
                "Friday" -> context.resources.getString(R.string.days_fri)
                "Saturday" -> context.resources.getString(R.string.days_sat)
                "Sunday" -> context.resources.getString(R.string.days_sun)
                else -> "nullly"
            }
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






