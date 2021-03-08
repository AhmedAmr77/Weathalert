package com.example.Weathalert.settings.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Weathalert.R
import com.example.Weathalert.databinding.ActivitySettingsBinding
import com.example.Weathalert.home.view.HomeActivity
import com.example.mvvm.utils.Constants
import java.util.*

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
        langBtnListener()
    }

    private fun langBtnListener() {
        binding.EnglishTV.setOnClickListener {
            binding.EnglishTV.setBackgroundResource(R.drawable.btn_pressed)
            binding.arabicTV.setBackgroundResource(R.drawable.btn_unpressed)
            sharedPref.edit().putString(Constants.LANGUAGE_SETTINGS, "en").apply()
        }
        binding.arabicTV.setOnClickListener {
            binding.arabicTV.setBackgroundResource(R.drawable.btn_pressed)
            binding.EnglishTV.setBackgroundResource(R.drawable.btn_unpressed)
            sharedPref.edit().putString(Constants.LANGUAGE_SETTINGS, "ar").apply()
        }
        binding.standardBtn.setOnClickListener {
            binding.standardBtn.setBackgroundResource(R.drawable.btn_pressed)
            binding.metricBtn.setBackgroundResource(R.drawable.btn_unpressed)
            binding.imperialBtn.setBackgroundResource(R.drawable.btn_unpressed)
            sharedPref.edit().putString(Constants.UNIT_SETTINGS, "standard").apply()
        }
        binding.metricBtn.setOnClickListener {
            binding.standardBtn.setBackgroundResource(R.drawable.btn_unpressed)
            binding.metricBtn.setBackgroundResource(R.drawable.btn_pressed)
            binding.imperialBtn.setBackgroundResource(R.drawable.btn_unpressed)
            sharedPref.edit().putString(Constants.UNIT_SETTINGS, "metric").apply()
        }
        binding.imperialBtn.setOnClickListener {
            binding.standardBtn.setBackgroundResource(R.drawable.btn_unpressed)
            binding.metricBtn.setBackgroundResource(R.drawable.btn_unpressed)
            binding.imperialBtn.setBackgroundResource(R.drawable.btn_pressed)
            sharedPref.edit().putString(Constants.UNIT_SETTINGS, "imperial").apply()
        }


        binding.saveTV.setOnClickListener {
            Toast.makeText(this, "saveeeeeeeeeee", Toast.LENGTH_LONG).show()
            saveNewLang(sharedPref.getString(Constants.LANGUAGE_SETTINGS, "en").toString())
            saveNewUnit(sharedPref.getString(Constants.UNIT_SETTINGS, "standard").toString())
            restartApp()
        }
        binding.cancelTV.setOnClickListener {
            this.finish()
        }
    }

    private fun saveNewUnit(newUnit: String) {
        Toast.makeText(this, "out intent unit", Toast.LENGTH_LONG).show()
        sharedPref.edit().putString(Constants.UNIT_SETTINGS, newUnit).apply()

    }

    private fun saveNewLang(newLang: String) {
        Toast.makeText(this, "out intent lang", Toast.LENGTH_LONG).show()
//        if (sharedPref.getString(Constants.LANGUAGE_SETTINGS, "en").toString() != newLang){//if language does NOT change, then do NOT restart activitry
            sharedPref.edit().putString(Constants.LANGUAGE_SETTINGS, newLang).apply()
            setAppLocale(newLang)
//        }
    }
    private fun setAppLocale(localeCode: String) {
        val resources = resources
        val dm = resources.getDisplayMetrics()
        val config = resources.getConfiguration()
        config.setLocale(Locale(localeCode.toLowerCase()))
        resources.updateConfiguration(config, dm)
    }

    fun restartApp(){
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        Toast.makeText(this, "INTENT", Toast.LENGTH_LONG).show()
    }




}