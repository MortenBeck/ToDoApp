package dk.dtu.ToDoList.feature

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Data classes for the API response
data class WeatherResponse(
    val weather: List<WeatherInfo>,
    val main: MainInfo,
    val name: String
)

data class WeatherInfo(val description: String)
data class MainInfo(val temp: Double)

// Retrofit API interface
interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): WeatherResponse
}

// Provide the WeatherApi instance
fun provideWeatherApi(): WeatherApi {
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

// Fetch weather data and update periodically
suspend fun fetchWeatherPeriodically(
    onWeatherUpdated: (String, Int) -> Unit
) {
    val weatherApi = provideWeatherApi()
    val apiKey = "f062e109162abcf31ec182583cae89e0"
    val city = "Copenhagen"

    while (true) {
        try {
            val response = weatherApi.getCurrentWeather(city, "metric", apiKey)
            val temperature = "${response.main.temp}°C"
            val iconRes = dk.dtu.ToDoList.R.drawable.weather // Replace with logic to map weather description to icons
            withContext(Dispatchers.Main) {
                onWeatherUpdated(temperature, iconRes)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onWeatherUpdated("Error fetching weather", dk.dtu.ToDoList.R.drawable.priority)
            }
        }
        delay(30 * 60 * 1000) // Update every 30 minutes
    }
}
