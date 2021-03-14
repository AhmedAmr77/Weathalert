package com.example.mvvm.presentation.alarm.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.Weathalert.databinding.ActivityAlarmBinding
import com.example.mvvm.presentation.alarm.viewmodel.AlarmViewModel

class AddAlarmActivity : AppCompatActivity() {

    lateinit var binding: ActivityAlarmBinding
    private lateinit var viewModel: AlarmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)


    }
}