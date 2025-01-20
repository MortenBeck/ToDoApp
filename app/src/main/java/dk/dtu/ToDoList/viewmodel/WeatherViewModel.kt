package dk.dtu.ToDoList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.dtu.ToDoList.model.api.WeatherService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.round
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class WeatherViewModel(
    private val weatherService: WeatherService
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private var updateJob: Job? = null
    private val updateInterval = 30 * 60 * 1000L // 30 minutes in milliseconds

    init {
        startWeatherUpdates()
    }

    fun startWeatherUpdates() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            try {
                while (isActive) {
                    fetchWeather()
                    delay(updateInterval)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun retryFetch() {
        viewModelScope.launch {
            fetchWeather()
        }
    }

    private suspend fun fetchWeather() {
        try {
            _weatherState.value = WeatherUiState.Loading
            val response = weatherService.getWeatherForCity()

            if (response.main != null && response.weather.isNotEmpty()) {
                val roundedTemp = round(response.main.temp).toInt()
                val weatherCondition = response.weather.first()
                val iconRes = getWeatherIconByCode(weatherCondition.icon)

                _weatherState.value = WeatherUiState.Success(
                    temperature = "$roundedTemp°C",
                    iconRes = iconRes,
                    lastUpdated = System.currentTimeMillis()
                )
            } else {
                _weatherState.value = WeatherUiState.Error("Invalid weather data received")
            }
        } catch (e: Exception) {
            _weatherState.value = WeatherUiState.Error(e.message ?: "Failed to fetch weather data")
        }
    }

    fun getWeatherIconByCode(iconCode: String): Int {
        return when (iconCode) {
            "01d" -> dk.dtu.ToDoList.R.drawable.weather_sun
            "02d" -> dk.dtu.ToDoList.R.drawable.weather // Cloudy
            "03d", "04d" -> dk.dtu.ToDoList.R.drawable.weather // Cloudy
            "09d", "10d" -> dk.dtu.ToDoList.R.drawable.weather // Rain (placeholder)
            "11d" -> dk.dtu.ToDoList.R.drawable.weather_thunderstorm
            "13d" -> dk.dtu.ToDoList.R.drawable.weather_snow
            "50d" -> dk.dtu.ToDoList.R.drawable.weather // Mist (placeholder)
            else -> dk.dtu.ToDoList.R.drawable.weather
        }
    }

    override fun onCleared() {
        super.onCleared()
        updateJob?.cancel()
    }
}

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
    data class Success(
        val temperature: String,
        val iconRes: Int,
        val lastUpdated: Long
    ) : WeatherUiState()

    val isLoading: Boolean
        get() = this is Loading

    val isError: Boolean
        get() = this is Error

    val isSuccess: Boolean
        get() = this is Success
}
