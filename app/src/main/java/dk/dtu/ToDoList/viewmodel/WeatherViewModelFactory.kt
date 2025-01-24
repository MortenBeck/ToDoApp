package dk.dtu.ToDoList.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dk.dtu.ToDoList.model.api.WeatherService



/**
 * A [ViewModelProvider.Factory] implementation that creates instances of [WeatherViewModel].
 * This factory ensures that a [WeatherService] is provided to the created [WeatherViewModel].
 */
class WeatherViewModelFactory : ViewModelProvider.Factory {

    /**
     * Creates a new instance of [WeatherViewModel] if the requested [modelClass] is assignable
     * to [WeatherViewModel]. Throws [IllegalArgumentException] otherwise.
     *
     * @param modelClass The class of the [ViewModel] to create.
     * @return A new instance of [WeatherViewModel] configured with a [WeatherService].
     * @throws IllegalArgumentException If [modelClass] is not [WeatherViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(WeatherService()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}