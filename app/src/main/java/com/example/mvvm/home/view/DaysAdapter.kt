package com.example.Weathalert.home.view

import android.content.Context
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.Weathalert.R
import com.example.Weathalert.databinding.DaysCellBinding
import com.example.Weathalert.datalayer.entity.Daily
import com.example.Weathalert.datalayer.entity.Hourly
import java.io.Serializable
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
       //     binding.daysCellIcon.setImageResource(R.drawable.ic_baseline_cloud_24)  //(hours.weather[0].id)
            binding.daysDayTVVal.text = (days.dt?.let { convert(it.toLong()) })?.get(2)
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
        private fun convert(time: Long): ArrayList<String>? {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                var ldt: LocalDateTime  = LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault())
                return arrayListOf(ldt.getMonth().toString(),ldt.getDayOfMonth().toString(), ldt.dayOfWeek.toString())
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
    }
}






