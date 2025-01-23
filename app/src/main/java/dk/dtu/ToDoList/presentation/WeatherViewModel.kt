package dk.dtu.ToDoList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.dtu.ToDoList.data.api.WeatherResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.round
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.lifecycle.ViewModelProvider
import dk.dtu.ToDoList.R

class WeatherService {
    suspend fun getWeatherForCity(city: String = "Copenhagen"): WeatherResponse {
        // Implement actual API call logic
        TODO("Implement actual weather API call")
    }
}

class WeatherViewModel(
    private val weatherService: WeatherService
) : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private var updateJob: Job? = null
    private val updateInterval = 30 * 60 * 1000L

    init {
        startWeatherUpdates()
    }

    private fun startWeatherUpdates() {
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

            if (response.weather.isNotEmpty()) {
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

    private fun getWeatherIconByCode(iconCode: String): Int {
        return when (iconCode) {
            "01d","01n" -> R.drawable.weather_sun
            "02d","02n" -> R.drawable.weather
            "03d", "04d","03n","04n" -> R.drawable.weather
            "09d", "10d","09n","10n" -> R.drawable.weather_rain_placeholder
            "11d","11n" -> R.drawable.weather_thunderstorm
            "13d","13n" -> R.drawable.weather_snow
            "50d","50n" -> R.drawable.weather
            else -> R.drawable.weather
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
    ) : WeatherUiState() {

        val isLoading: Boolean get() = this is Loading
        val isError: Boolean get() = this is Error
        val isSuccess: Boolean get() = this is Success
    }
}

class WeatherViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(WeatherService()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}