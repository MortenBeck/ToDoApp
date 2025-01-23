package dk.dtu.ToDoList.data.api

import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val weather: List<WeatherInfo>,
    val main: MainInfo,
    val name: String
)

data class WeatherInfo(
    val description: String,
    val icon: String
)

data class MainInfo(
    val temp: Double
)

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): WeatherResponse
}