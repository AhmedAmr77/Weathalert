package com.example.Weathalert.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.Weathalert.R
import com.example.Weathalert.databinding.DaysCellBinding
import com.example.mvvm.datalayer.entity.weather.Daily
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


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
            binding.daysDayTVVal.text = (days.dt?.let { convert(it.toLong()) })
            binding.daysGrtTempTVVal.text = days.temp?.max?.toInt().toString().plus("°")
            binding.daysSmlTempTVVal.text = days.temp?.min?.toInt().toString().plus("°")
            binding.daysDescTVVal.text = days.weather?.get(0)?.main
            binding.daysHumidityTVVal.text = (days.humidity)?.toString().plus(" %")
            binding.daysCloudsTVVal.text = (days.clouds)?.toString().plus(" %")
            binding.daysPressureTVVal.text = (days.pressure)?.toString().plus(" ${ context.resources.getString(R.string.hPa)}")
            binding.daysWindTVVal.text = (days.wind_speed)?.toString().plus(" ${ context.resources.getString(R.string.met_per_sec)}")
        }

        /*  GOOGLE time converter --- TRY it
        @SuppressLint("SimpleDateFormat")
        fun convertLongToDateString(systemTime: Long): String {
            return SimpleDateFormat("EEEE MMM-dd-yyyy' Time: 'HH:mm")
                    .format(systemTime).toString()
        }
        */
        private fun convert(time: Long): String? {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val ldt: LocalDateTime  = LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault())
                return getDay(ldt.dayOfWeek.toString())
            } else {
                return null
            }
/*
            val dt = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Instant.ofEpochSecond(time)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .dayOfWeek.toString().subSequence(0,3).toString()


            } else {
                "NOT OREO"
            }
            return dt
*/
        }

        private fun getDay(day: String): String {
            return when (day){
                "MONDAY" -> context.resources.getString(R.string.days_mon)
                "TUESDAY" -> context.resources.getString(R.string.days_tue)
                "WEDNESDAY" -> context.resources.getString(R.string.days_wed)
                "THURSDAY" -> context.resources.getString(R.string.days_thu)
                "FRIDAY" -> context.resources.getString(R.string.days_fri)
                "SATURDAY" -> context.resources.getString(R.string.days_sat)
                "SUNDAY" -> context.resources.getString(R.string.days_sun)
                else -> "nullly"
            }
        }

        private fun getResId(icon: String?): Int {
            return when(icon){
                "01d", "01n" -> R.drawable.d_oneone
                "02d", "02n" -> R.drawable.d_two
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






