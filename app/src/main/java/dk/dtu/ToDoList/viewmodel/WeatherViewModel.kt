package dk.dtu.ToDoList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.dtu.ToDoList.model.api.WeatherService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.round
import kotlinx.coroutines.delay

class WeatherViewModel(
    private val weatherService: WeatherService
) : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState = _weatherState.asStateFlow()

    init {
        startWeatherUpdates()
    }

    private fun startWeatherUpdates() {
        viewModelScope.launch {
            while (true) {
                fetchWeather()
                delay(30 * 60 * 1000) // 30 minutes
            }
        }
    }

    private suspend fun fetchWeather() {
        try {
            val response = weatherService.getWeatherForCity()
            val roundedTemp = round(response.main.temp).toInt()
            _weatherState.value = WeatherUiState.Success(
                temperature = "$roundedTemp°C",
                iconRes = dk.dtu.ToDoList.R.drawable.weather
            )
        } catch (e: Exception) {
            _weatherState.value = WeatherUiState.Error
        }
    }
}

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    object Error : WeatherUiState()
    data class Success(
        val temperature: String,
        val iconRes: Int
    ) : WeatherUiState()
}