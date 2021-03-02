package com.example.Weathalert.favoritecities.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.Weathalert.databinding.ActivityFavoriteBinding

class FavoriteActivity : AppCompatActivity() {

    lateinit var binding: ActivityFavoriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}
