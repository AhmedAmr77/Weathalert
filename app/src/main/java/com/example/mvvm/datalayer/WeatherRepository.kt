package com.example.Weathalert.datalayer

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.example.Weathalert.datalayer.entity.WeatherData
import com.example.Weathalert.datalayer.local.LocalDataSource
import com.example.Weathalert.datalayer.remote.RemoteDataSource
import kotlinx.coroutines.*
import retrofit2.Response

class WeatherRepository(val application: Application) {

//    lateinit var weatherDao: WeatherDao
//    lateinit var cityData: LiveData<WeatherData>
    val remoteDataSource = RemoteDataSource()
    val localDataSource = LocalDataSource.getInstance(application)

    fun add(){

    }

    suspend fun getWeatherData(lat: String, lon: String): Response<WeatherData>{
        val resposne = RemoteDataSource().getWeatherData(lat, lon)
        Log.i("test","in repo => ${resposne.message()}")
        return resposne
    }
}
/*
fun loadCurrentData(): LiveData<WeatherResponse> {
    val lat = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.CURRENT_LATITUDE,"0").toString()
    val long = application.getSharedPreferences(Constants.SHARED_PREF_CURRENT_LOCATION, Context.MODE_PRIVATE).getString(Constants.CURRENT_LONGITUDE,"0").toString()
    val exceptionHandlerException = CoroutineExceptionHandler { _, _ ->
        Log.i(Constants.LOG_TAG,"exception from retrofit")
    }
    CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
        val response = remoteDataSource.getWeatherService().getAllData(lat, long, Constants.EXCLUDE_MINUTELY, "default", "en", Constants.WEATHER_API_KEY)
        if(response.isSuccessful){
            localDataSource.insertDefault(response.body())
            Log.i(Constants.LOG_TAG,"success")
        }
    }
    return localDataSource.getDefault(lat,long)
}
*/
/*
fun fetchData() {
        loadingLiveData.postValue(true)
        val exceptionHandlerException = CoroutineExceptionHandler{_, th ->
            errorLiveData.postValue(th.message.toString())
            loadingLiveData.postValue(false)
        }
        CoroutineScope(Dispatchers.IO + exceptionHandlerException).launch {
            //val response = WeatherService.getWeatherService().getWeatherData("33.441792", "-94.037689", "676da108dea75e4101df9c4c03b3d757")
            val response:WeatherData? = weatherRepository.getWeatherData("61.90609352577086", "92.44568906858825")
            withContext(Dispatchers.Main){
                loadingLiveData.postValue(false)
                if (response != null) {
                    weatherListLiveData.postValue(response)
                } else {
                    errorLiveData.postValue("Response is NULL")
                }
            }
        }
    }
 */
/*
class TasksRepository(application: Application?) {
    var remoteDataSource: RemoteDataSource
    var localDataSource: LocalDataSource
    val tasks: LiveData<List<Task>>
        get() {
            remoteDataSource.tasks.enqueue(object : Callback<List<Task?>?> {
                override fun onResponse(call: Call<List<Task?>?>, response: Response<List<Task?>?>) {
                    localDataSource.insert(response.body())
                }

                override fun onFailure(call: Call<List<Task?>?>, t: Throwable) {}
            })
            return localDataSource.tasks
        }

    init {
        remoteDataSource = RemoteDataSource()
        localDataSource = LocalDataSource(application)
    }
}
*/
/*
public class TasksRepository {
    RemoteDataSource remoteDataSource;
    LocalDataSource localDataSource;
    public TasksRepository(Application application) {
        remoteDataSource = new RemoteDataSource();
        localDataSource = new LocalDataSource(application);
    }
    public LiveData<List<Task>> getTasks() {
        remoteDataSource.getTasks().enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                localDataSource.insert(response.body());
            }
            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
            }
        });
        return localDataSource.getTasks();
    }
}
 */