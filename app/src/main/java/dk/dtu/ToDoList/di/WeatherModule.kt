package dk.dtu.ToDoList.di


import dk.dtu.ToDoList.data.api.WeatherApi
import dk.dtu.ToDoList.data.repository.WeatherRepository
import dk.dtu.ToDoList.data.repository.WeatherRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherProvider {
    private const val API_KEY = "f062e109162abcf31ec182583cae89e0"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val weatherApi: WeatherApi = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    val repository: WeatherRepository by lazy {
        WeatherRepositoryImpl(weatherApi, API_KEY)
    }
}