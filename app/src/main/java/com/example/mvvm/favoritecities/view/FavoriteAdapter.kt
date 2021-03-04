package com.example.mvvm.favoritecities.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.Weathalert.R
import com.example.Weathalert.databinding.CitiesCellBinding
import com.example.Weathalert.datalayer.entity.WeatherData

class FavoriteAdapter(var cities: ArrayList<WeatherData>) : RecyclerView.Adapter<FavoriteAdapter.CitiesVH>()  {

    lateinit var binding: CitiesCellBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesVH {
        binding = CitiesCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CitiesVH(binding)
    }

    override fun onBindViewHolder(holder: CitiesVH, position: Int) {
        holder.bind(cities[position])
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    fun updateHours(newHours: List<WeatherData>){
        cities.clear()
        cities.addAll(newHours)
        notifyDataSetChanged()
    }

    class CitiesVH (val binding: CitiesCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: WeatherData) {
            binding.cityCellName.text = city.timezone
            binding.tempTV.text = city.current?.temp.toString()
            binding.cityCellIcon.setImageResource(R.drawable.ic_baseline_favorite_24)  //(hours.weather[0].id)
        }
    }
}