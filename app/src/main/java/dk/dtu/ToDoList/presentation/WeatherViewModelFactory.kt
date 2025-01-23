package dk.dtu.ToDoList.presentation

import WeatherService
import WeatherViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WeatherViewModelFactory(
    private val weatherService: WeatherService = WeatherService()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(weatherService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}