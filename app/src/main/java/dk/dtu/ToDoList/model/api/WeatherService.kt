package dk.dtu.ToDoList.model.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


data class WeatherResponse(
    val weather: List<WeatherInfo>,
    val main: MainInfo,
    val name: String
)

data class WeatherInfo(
    val description: String,
    val icon: String)

data class MainInfo(val temp: Double)

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): WeatherResponse
}

class WeatherService {
    private val apiKey = "f062e109162abcf31ec182583cae89e0"
    private val weatherApi: WeatherApi = provideWeatherApi()

    private fun provideWeatherApi(): WeatherApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherApi::class.java)
    }

    suspend fun getWeatherForCity(city: String = "Copenhagen"): WeatherResponse {
        return weatherApi.getCurrentWeather(city, "metric", apiKey)
    }
}