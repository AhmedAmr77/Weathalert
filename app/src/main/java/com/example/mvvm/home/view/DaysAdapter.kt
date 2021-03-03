package com.example.Weathalert.home.view

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
        return DaysVH(binding)
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



    class DaysVH(val binding: DaysCellBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(days: Daily) {
            binding.daysCellIcon.setImageResource(R.drawable.ic_baseline_favorite_24)  //(hours.weather[0].id)
            binding.daysCellDay.text = (days.dt?.let { convert(it.toLong()) })?.get(2)?.subSequence(0,3).toString()
            binding.daysCellDate.text = (days.dt?.let { convert(it.toLong()) })?.get(0)?.subSequence(0,3).toString().plus(" ${(days.dt?.let {
                convert(it.toLong())
            })?.get(1) ?: "NULL"}")
            binding.daysCellTempGreat.text = (days.temp?.max)?.toInt().toString()
            binding.daysCellTempSmall.text = (days.temp?.min)?.toInt().toString()
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






