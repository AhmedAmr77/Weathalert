package com.example.Weathalert.home.view

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

class HoursAdapter(var hours: ArrayList<Hourly>) : RecyclerView.Adapter<HoursAdapter.HoursVH>() {

    lateinit var binding: HoursCellBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : HoursVH {
        binding = HoursCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HoursVH(binding)
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

    class HoursVH(val binding: HoursCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hours: Hourly) {
            binding.hoursCellHour.text = convert(hours.dt.toLong()).toString()
            binding.hoursCellIcon.setImageResource(R.drawable.ic_baseline_favorite_24)  //(hours.weather[0].id)
            binding.hoursCellTemp.text = (hours.temp-273.15).toInt().toString()
        }

        private fun convert(time: Long): Serializable {
            val dt = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Instant.ofEpochSecond(time)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .hour
            } else {
                "NOT OREO"
            }
            return dt
        }
    }
}