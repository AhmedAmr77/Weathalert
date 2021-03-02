package com.example.Weathalert.home.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.Weathalert.datalayer.WeatherRepository
import com.example.Weathalert.datalayer.entity.WeatherData
import kotlinx.coroutines.*
import retrofit2.Response
import kotlin.math.log

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    val weatherListLiveData = MutableLiveData<WeatherData>()
    val loadingLiveData = MutableLiveData<Boolean>()
    val errorLiveData = MutableLiveData<String>()

    private val weatherRepository = WeatherRepository(getApplication())

    fun fetchData() {
        loadingLiveData.postValue(true)
        Log.i("test","begin fetch method")
        val exceptionHandlerException = CoroutineExceptionHandler{_, th ->
            Log.i("test","begin excepHandler block")
            loadingLiveData.postValue(false)
            errorLiveData.postValue("from ExceptionHandlerr : ${th.message.toString()}")
            Log.i("test","end excepHandler block")
        }
        Log.i("test","after excepHandler block")
        CoroutineScope(Dispatchers.IO + exceptionHandlerException).launch {
            Log.i("test","begin CoroScope block")
            //val response = WeatherService.getWeatherService().getWeatherData("33.441792", "-94.037689", "676da108dea75e4101df9c4c03b3d757")
            val res: Response<WeatherData> = weatherRepository.getWeatherData("61.90609352577086", "92.44568906858825")
            Log.i("test","after call getWeathData")
            withContext(Dispatchers.Main){
                loadingLiveData.postValue(false)
                if (res.isSuccessful){
                    weatherListLiveData.postValue(res.body())
                } else{
                    errorLiveData.postValue("Resposne => ${res.message()}")
                }
            }
        }
    }
}
/*


        CoroutineScope(Dispatchers.IO + exceptionHandlerException).launch {
            //val response = WeatherService.getWeatherService().getWeatherData("33.441792", "-94.037689", "676da108dea75e4101df9c4c03b3d757")
            val response:WeatherData? = weatherRepository.getWeatherData("33.441792", "-94.037689", "676da108dea75e4101df9c4c03b3d757")
            withContext(Dispatchers.Main){
                loadingLiveData.postValue(false)
                if (response != null) {
                    weatherListLiveData.postValue(response)
                } else {
                    //errorLiveData.postValue(response.message())
                }
            }
        }
    }
}
 */