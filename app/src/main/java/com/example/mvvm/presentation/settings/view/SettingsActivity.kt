package com.example.Weathalert.settings.view

import android.content.Context
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
    lateinit var selectedLang : String
    lateinit var selectedUnit : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale(
            getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE).getString(
                Constants.LANGUAGE_SETTINGS,
                "en"
            )!!
        )
        supportActionBar?.title = resources.getString(R.string.menu_settings)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
        selectedLang = sharedPref.getString(Constants.LANGUAGE_SETTINGS, "en").toString()
        selectedUnit = sharedPref.getString(Constants.UNIT_SETTINGS, "standard").toString()

        langBtnListener()
    }

    private fun langBtnListener() {
        binding.EnglishTV.setOnClickListener {
            binding.EnglishTV.setBackgroundResource(R.drawable.btn_pressed)
            binding.arabicTV.setBackgroundResource(R.drawable.btn_unpressed)
            selectedLang = "en"
        }
        binding.arabicTV.setOnClickListener {
            binding.arabicTV.setBackgroundResource(R.drawable.btn_pressed)
            binding.EnglishTV.setBackgroundResource(R.drawable.btn_unpressed)
            selectedLang = "ar"
        }
        binding.standardBtn.setOnClickListener {
            binding.standardBtn.setBackgroundResource(R.drawable.btn_pressed)
            binding.metricBtn.setBackgroundResource(R.drawable.btn_unpressed)
            binding.imperialBtn.setBackgroundResource(R.drawable.btn_unpressed)
            selectedUnit = "standard"
        }
        binding.metricBtn.setOnClickListener {
            binding.standardBtn.setBackgroundResource(R.drawable.btn_unpressed)
            binding.metricBtn.setBackgroundResource(R.drawable.btn_pressed)
            binding.imperialBtn.setBackgroundResource(R.drawable.btn_unpressed)
            selectedUnit = "metric"
        }
        binding.imperialBtn.setOnClickListener {
            binding.standardBtn.setBackgroundResource(R.drawable.btn_unpressed)
            binding.metricBtn.setBackgroundResource(R.drawable.btn_unpressed)
            binding.imperialBtn.setBackgroundResource(R.drawable.btn_pressed)
            selectedUnit =  "imperial"
        }


        binding.saveTV.setOnClickListener {
            saveNewLang(selectedLang)
            saveNewUnit(selectedUnit)
            restartApp()
        }
        binding.cancelTV.setOnClickListener {
            this.finish()
        }
    }

    private fun saveNewUnit(newUnit: String) {
        sharedPref.edit().putString(Constants.UNIT_SETTINGS, newUnit).apply()

    }

    private fun saveNewLang(newLang: String) {
        sharedPref.edit().putString(Constants.LANGUAGE_SETTINGS, newLang).apply()
        setAppLocale(newLang)
    }

    private fun setAppLocale(localeCode: String) {
        val resources = resources
        val dm = resources.getDisplayMetrics()
        val config = resources.getConfiguration()
        config.setLocale(Locale(localeCode.toLowerCase()))
        resources.updateConfiguration(config, dm)

        val appRes = application.resources
        val dmApp = appRes.getDisplayMetrics()
        val configApp = appRes.configuration
        configApp.setLocale(Locale(localeCode.toLowerCase()))
        appRes.updateConfiguration(configApp, dmApp)
    }

    fun restartApp(){
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra(Constants.LANG_CHANGED, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

}