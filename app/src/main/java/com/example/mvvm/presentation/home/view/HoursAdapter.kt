package com.example.Weathalert.home.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.Weathalert.R
import com.example.Weathalert.databinding.HoursCellBinding
import com.example.mvvm.datalayer.entity.weather.Hourly
import com.example.mvvm.utils.Constants
import java.text.NumberFormat
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
        val sharedPreferences = context.getSharedPreferences(
            Constants.SHARED_PREF,
            Context.MODE_PRIVATE
        )

        fun bind(hours: Hourly) {
            binding.hoursCellHour.text = hours.dt?.let { dateConverter(it) }
            binding.hoursCellIcon.setImageResource(getResId(hours.weather?.get(0)?.icon))
            binding.hoursCellTemp.text = "${numLocale((hours.temp)!!.toDouble())}°"
        }

        private fun dateConverter(dt: Int): String {
            val calender = Calendar.getInstance()
            calender.timeInMillis = (dt)*1000L
            val dateFormat = SimpleDateFormat("hh a", Locale(sharedPreferences.getString(Constants.LANGUAGE_SETTINGS, "en")))
            return dateFormat.format(calender.time)
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
