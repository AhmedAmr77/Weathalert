package com.example.Weathalert.datalayer

import android.app.Application
import com.example.mvvm.datalayer.entity.weather.WeatherData
import com.example.Weathalert.datalayer.local.LocalDataSource
import com.example.Weathalert.datalayer.remote.RemoteDataSource
import retrofit2.Response

class WeatherRepository(val application: Application) {

    private val remoteDataSource = RemoteDataSource()
    private val localDataSource = LocalDataSource.getInstance(application)



    suspend fun getWeatherData(lat: String, lon: String, appid: String, units: String, lang: String, execlude: String): Response<WeatherData> { //from Retrofit
        return remoteDataSource.getWeatherData(lat, lon, appid, units, lang, execlude)
    }

    suspend fun deleteOldCurrent(){
        return localDataSource.deleteOldCurrent()
    }

    suspend fun getCity(lat:String, lon:String): WeatherData {  //from local
        return localDataSource.getCityData(lat.toDouble(), lon.toDouble())
    }

    suspend fun getFavCities(): List<WeatherData>{
        return localDataSource.getAllCities()
    }

    suspend fun addCityToLocal(city: WeatherData){
        localDataSource.insert(city)
    }

    suspend fun deleteCityData(lat: Double, lon: Double) {
        localDataSource.deleteCityData(lat, lon)
    }




//    suspend fun getAndAdd(lat: String, lon: String): String{
//        val res = getWeatherData(lat, lon)
//        return if (res.isSuccessful && res != null){
//            addCityToLocal(res.body()!!)
//            "Done"
//        } else {
//            res.message()
//        }
//    }



    //getWeatherData(Constants.LATITUDE, Constants.LONGITUDE, Constants.APP_ID,
    //                                       Constants.UNIT_SETTINGS, Constants.LANGUAGE_SETTINGS, Constants.EXECLUDE)



//    fun addFavCity(lat:String, lon:String){    //   8-3
//        var res:WeatherData
//        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
//            Log.i("test","exception from retrofit${th.message}")
//        }
//        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
//            val response = remoteDataSource.getWeatherData (lat, lon, Constants.APP_ID, )
//            if(response.isSuccessful){
//                val res = response.body()
//                res?.isFavorite = true
//                localDataSource.insert(res)
//                Log.i("test","success")
//            } else {
//                //response ERROR
//            }
//        }
//    }


    /*       wakeup
    fun getFavCities(): LiveData<List<WeatherData>>{
        val response = localDataSource.getAllCities()
        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
            Log.i("test","exception from retrofit${th.message}")
        }
        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
                                                                                                     //What if there is no data (Empty Fav)
            if(response.value.isNullOrEmpty()){
                Log.i("test","fav isNullOrEmpty in DB ") //leha lazma??
            }
        }
        return LocalDataSource.getInstance(application).getAllCities()
    }
     */



    //        val lat = application.getSharedPreferences(Constants.SHARED_PREF_LOCATION, Context.MODE_PRIVATE).getString(
//            Constants.LATITUDE,"0").toString()
//        val lon = application.getSharedPreferences(Constants.SHARED_PREF_LOCATION, Context.MODE_PRIVATE).getString(
//            Constants.LONGITUDE,"0").toString()
//        val exceptionHandlerException = CoroutineExceptionHandler { _, th ->
//            Log.i("test","exception from retrofit${th.message}")
//        }
//        CoroutineScope(Dispatchers.IO+exceptionHandlerException).launch {
//            val response = remoteDataSource.getWeatherData (lat, lon)
//            if(response.isSuccessful){
//                localDataSource.insert(response.body()!!)
//                Log.i("test","success")
//            }
//        }
//        return LocalDataSource.getInstance(application).getCityData(lat.toDouble(), lon.toDouble())  //edit LocalDataSource.getCityData(l,l) ??


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
            loadingLiveData.postValue(false)
            errorLiveData.postValue("from ExceptionHandlerr : ${th.message.toString()}")
        }
        CoroutineScope(Dispatchers.IO + exceptionHandlerException).launch {
            val res: Response<WeatherData> = weatherRepository.getWeatherData("61.90609352577086", "92.44568906858825")
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