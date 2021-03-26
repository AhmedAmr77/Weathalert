package com.example.mvvm.presentation.favoritecities.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.Weathalert.R
import com.example.Weathalert.databinding.CitiesCellBinding
import com.example.mvvm.datalayer.entity.weather.WeatherData
import com.example.mvvm.utils.Constants
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
        val sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF,
            Context.MODE_PRIVATE
        )
        fun bind(city: WeatherData) {
            binding.daysCellBackgroud.setImageResource(getModeRes(city))
            binding.favDateTVVal.text = city.current?.dt?.let { it1 -> dateConverter(it1, "EEE, d MMM") }.toString()
            binding.favCityNameTVVal.text = city.timezone
            binding.favTempTVVal.text = "${numLocale((city.current?.temp)!!.toDouble())}°"
        }

        private fun numLocale(num: Double): String {
            return NumberFormat.getInstance(Locale
                (sharedPreferences.getString(Constants.LANGUAGE_SETTINGS, "en"))).format(num.toInt())
        }


        private fun getModeRes(weatherData: WeatherData): Int {
            if(weatherData.current?.weather?.get(0)?.icon?.get(2).toString() == "n"){
                return R.drawable.night_cities
            }
            return R.drawable.day_cit
        }

        private fun dateConverter(dt: Int, pattern: String): String {
            val calender = Calendar.getInstance()
            calender.timeInMillis = (dt)*1000L
            val dateFormat = SimpleDateFormat(pattern)
            return dateFormat.format(calender.time)
        }

    }
}