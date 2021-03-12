package com.example.mvvm.favoritecities.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.Weathalert.databinding.CitiesCellBinding
import com.example.mvvm.datalayer.entity.weather.WeatherData
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class FavoriteAdapter(var cities: ArrayList<WeatherData>,
                      val listener: (WeatherData) -> Unit) : RecyclerView.Adapter<FavoriteAdapter.CitiesVH>()  {

    lateinit var binding: CitiesCellBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesVH {
        binding = CitiesCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CitiesVH(binding, parent.context)
    }

    override fun onBindViewHolder(holder: CitiesVH, position: Int) {
        holder.bind(cities[position])
        holder.itemView.setOnClickListener { listener(cities[position]) }
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    fun updateHours(newHours: List<WeatherData>){
        cities.clear()
        cities.addAll(newHours)
        notifyDataSetChanged()
    }

    class CitiesVH (val binding: CitiesCellBinding, val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: WeatherData) {
            //            binding.cityCellBackground.setImageResource(R.drawable.ic_baseline_favorite_24)  //(hours.weather[0].id)
            binding.favDateTVVal.text =
                city.current?.dt?.let { it1 -> convertLongToDateString(it1, "EEE, d MMM") }.toString()
//            binding.daysTempTVVal.text = city.current?.temp.toString().plus("°")
//            binding.daysDayTVVal.text = (city.current?.let { it.dt?.toLong()?.let { it1 ->
//                convert(it1)}})?.get(2)
            binding.favCityNameTVVal.text = city.timezone
//            binding.daysDescTVVal.text = city.current?.weather?.get(0)?.main.toString()
//            binding.daysHumidityTVVal.text = city.current?.humidity.toString()
//            binding.daysCloudsTVVal.text = city.current?.clouds.toString()
//            binding.daysPressureTVVal.text = city.current?.pressure.toString().plus(" ${ context.resources.getString(R.string.hPa)}")
//            binding.daysWindTVVal.text = city.current?.wind_speed.toString().plus(" ${ context.resources.getString(R.string.met_per_sec)}")
            binding.favTempTVVal.text = city.current?.temp?.toInt().toString().plus("°")

        }

        private fun convertLongToDateString(systemTime: Int, pattern: String): String {
//        return SimpleDateFormat(pattern)
//            .format(systemTime).toString()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                var ldt: LocalDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(systemTime.toLong()),
                    ZoneId.systemDefault()
                )
                return ldt.format(DateTimeFormatter.ofPattern(pattern))
            } else {
                return "OS lessThan OREO"
            }
        }

//        private fun convert(time: Long): ArrayList<String>? {
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                var ldt: LocalDateTime =
//                    LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault())
//                return arrayListOf(
//                    ldt.getMonth().toString(),
//                    ldt.getDayOfMonth().toString(),
//                    ldt.dayOfWeek.toString()
//                )
//            } else {
//                return null
//            }
//        }
    }
}