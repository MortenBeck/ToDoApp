package dk.dtu.ToDoList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dk.dtu.ToDoList.model.api.WeatherService

class WeatherViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(WeatherService()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}