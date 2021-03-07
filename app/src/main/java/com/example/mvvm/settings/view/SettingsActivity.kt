package com.example.Weathalert.settings.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.Weathalert.databinding.ActivitySettingsBinding
import com.example.Weathalert.home.view.HomeActivity
import com.example.mvvm.utils.Constants
import java.util.*

class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setAppLocale(getSharedPreferences(Constants.SHARED_PREF_SETTINGS_LANGUAGE, Context.MODE_PRIVATE).getString(Constants.LANGUAGE,"en")!!)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        langBtnListener()
    }

    private fun langBtnListener() {
        binding.arLangBtn.setOnClickListener {
            saveNewLang("ar")
        }
        binding.enLangBtn.setOnClickListener {
            saveNewLang("en")
        }
    }

    private fun saveNewLang(newLang: String) {
        val sharedPref = getSharedPreferences(Constants.SHARED_PREF_SETTINGS_LANGUAGE, MODE_PRIVATE)
        sharedPref.edit().putString(Constants.LANGUAGE, newLang).apply()
        setAppLocale(newLang)
        val intent = Intent(this, HomeActivity::class.java)
        finish()
        startActivity(intent)
    }

    private fun setAppLocale(localeCode: String) {
        val resources = resources;
        val dm = resources.getDisplayMetrics()
        val config = resources.getConfiguration()
        config.setLocale(Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, dm)
    }




}