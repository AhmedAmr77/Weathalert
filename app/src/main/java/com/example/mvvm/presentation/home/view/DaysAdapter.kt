package com.example.Weathalert.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.Weathalert.R
import com.example.Weathalert.databinding.DaysCellBinding
import com.example.mvvm.datalayer.entity.weather.Daily
import com.example.mvvm.utils.Constants
import java.text.NumberFormat
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
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF,
            Context.MODE_PRIVATE
        )
        fun bind(days: Daily) {
            binding.daysCellIcon.setImageResource(getResId(days.weather?.get(0)?.icon))
            binding.daysDayTVVal.text = (days.dt?.let { dateConverter(it.toLong()) })
            binding.daysGrtTempTVVal.text = "${numLocale(days.temp?.max!!)}°"
            binding.daysSmlTempTVVal.text = "${numLocale(days.temp?.min!!)}°"
            binding.daysDescTVVal.text = days.weather?.get(0)?.description
            binding.daysHumidityTVVal.text = "${numLocale((days.humidity)!!.toDouble())} ${context.resources.getString(R.string.percent_sign)}"
            binding.daysCloudsTVVal.text = "${numLocale((days.clouds)!!.toDouble())} ${context.resources.getString(R.string.percent_sign)}"
            binding.daysPressureTVVal.text = "${numLocale((days.pressure)!!.toDouble())} ${context.resources.getString(R.string.hPa)}"
            binding.daysWindTVVal.text = "${numLocale((days.wind_speed)!!.toDouble())} ${context.resources.getString(R.string.met_per_sec)}"
        }

        private fun dateConverter(dt: Long): String {
            return SimpleDateFormat("EEEE", Locale(sharedPreferences.getString(Constants.LANGUAGE_SETTINGS, "en"))).format(dt*1000)
        }

        private fun numLocale(num: Double): String {
            return NumberFormat.getInstance(Locale
                (sharedPreferences.getString(Constants.LANGUAGE_SETTINGS, "en"))).format(num.toInt())
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






