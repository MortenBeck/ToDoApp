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
        // Cancel any existing job before starting a new one
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            try {
                while (isActive) {
                    fetchWeather()
                    delay(updateInterval)
                }
            } catch (e: CancellationException) {
                // Handle coroutine cancellation gracefully
                throw e
            } catch (e: Exception) {
                // Log any unexpected errors
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

            if (response.main != null) {
                val roundedTemp = round(response.main.temp).toInt()
                _weatherState.value = WeatherUiState.Success(
                    temperature = "$roundedTemp°C",
                    iconRes = dk.dtu.ToDoList.R.drawable.weather,
                    lastUpdated = System.currentTimeMillis()
                )
            } else {
                _weatherState.value = WeatherUiState.Error("Invalid weather data received")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _weatherState.value = WeatherUiState.Error(
                e.message ?: "Failed to fetch weather data"
            )
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