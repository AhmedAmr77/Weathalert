package com.example.Weathalert.datalayer.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.Weathalert.datalayer.entity.WeatherData
import com.example.Weathalert.datalayer.entity.coverters.AlertConverter
import com.example.Weathalert.datalayer.entity.coverters.DailyConverter
import com.example.Weathalert.datalayer.entity.coverters.HourlyConverter
import com.example.Weathalert.datalayer.entity.coverters.WeatherConverter

@Database(entities = [WeatherData::class], version = 1, exportSchema = false)
@TypeConverters(AlertConverter::class, DailyConverter::class, HourlyConverter::class)
abstract class WeatherDatabase : RoomDatabase(){

    abstract fun weatherDao(): WeatherDao

}

/*
companion object {

        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Application): WeatherDatabase {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WeatherDatabase::class.java,
                        "WeatherData"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance

        }
    }
 */