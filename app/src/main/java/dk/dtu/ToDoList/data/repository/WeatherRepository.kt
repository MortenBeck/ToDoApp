package dk.dtu.ToDoList.data.repository

import dk.dtu.ToDoList.data.api.WeatherApi
import dk.dtu.ToDoList.data.api.WeatherResponse
import javax.inject.Inject

interface WeatherRepository {
    suspend fun getWeatherForCity(city: String = "Copenhagen"): WeatherResponse
}

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val apiKey: String
) : WeatherRepository {

    override suspend fun getWeatherForCity(city: String): WeatherResponse {
        return weatherApi.getCurrentWeather(
            city = city,
            units = "metric",
            apiKey = apiKey
        )
    }
}