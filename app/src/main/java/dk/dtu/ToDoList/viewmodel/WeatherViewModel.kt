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


/**
 * A [ViewModel] responsible for periodically fetching weather information using the
 * [WeatherService]. The weather data is exposed via a [StateFlow] of [WeatherUiState],
 * allowing UI components to react to changes (loading, error, or success).
 *
 * @property weatherService A [WeatherService] instance for making network requests
 * to fetch the latest weather data.
 */
class WeatherViewModel(
    private val weatherService: WeatherService
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)

    /**
     * A [StateFlow] that provides real-time updates on the weather fetch status and data.
     * Subscribers can collect this flow to update their UI according to [WeatherUiState].
     */
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private var updateJob: Job? = null

    /**
     * The interval between weather data refreshes (default: 30 minutes).
     */
    private val updateInterval = 30 * 60 * 1000L // 30 minutes in milliseconds

    init {
        startWeatherUpdates()
    }


    /**
     * Starts a coroutine that repeatedly fetches weather data at [updateInterval].
     * Cancels any existing update job before launching a new one.
     */
    private fun startWeatherUpdates() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            try {
                while (isActive) {
                    fetchWeather()
                    delay(updateInterval)
                }
            } catch (e: CancellationException) {
                // Re-throw cancellation for structured concurrency
                throw e
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }


    /**
     * Public method to re-try fetching the weather once.
     * Typically called after an error state, allowing the UI to request a retry.
     */
    fun retryFetch() {
        viewModelScope.launch {
            fetchWeather()
        }
    }


    /**
     * Suspends while making a network request to fetch the current weather from [WeatherService].
     * Updates [_weatherState] with [WeatherUiState.Loading], [WeatherUiState.Success], or [WeatherUiState.Error].
     */
    private suspend fun fetchWeather() {
        try {
            _weatherState.value = WeatherUiState.Loading
            val response = weatherService.getWeatherForCity()

            // Debug log
            println("weather: $response") //testing by looking in log lmao

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


    /**
     * Maps an OpenWeather icon code (e.g., "01d") to a local drawable resource.
     *
     * @param iconCode The 2- or 3-character code returned by the weather API (e.g., "09d").
     * @return An integer referencing a drawable resource ID.
     */
    private fun getWeatherIconByCode(iconCode: String): Int {
        return when (iconCode) {
            "01d","01n" -> dk.dtu.ToDoList.R.drawable.weather_sun
            "02d","02n" -> dk.dtu.ToDoList.R.drawable.weather // Cloudy
            "03d", "04d","03n","04n" -> dk.dtu.ToDoList.R.drawable.weather // Cloudy
            "09d", "10d","09n","10n" -> dk.dtu.ToDoList.R.drawable.weather_rain_placeholder // Rain (placeholder)
            "11d","11n" -> dk.dtu.ToDoList.R.drawable.weather_thunderstorm
            "13d","13n" -> dk.dtu.ToDoList.R.drawable.weather_snow
            "50d","50n" -> dk.dtu.ToDoList.R.drawable.weather // Mist (placeholder)
            else -> dk.dtu.ToDoList.R.drawable.weather
        }
    }


    /**
     * Cancels the periodic update job on [onCleared], ensuring no background work continues
     * after this [ViewModel] is no longer in use.
     */
    override fun onCleared() {
        super.onCleared()
        updateJob?.cancel()
    }
}


/**
 * Represents the various UI states that the weather view can be in.
 */
sealed class WeatherUiState {

    /**
     * The loading state, indicating that a network request is in progress.
     */
    data object Loading : WeatherUiState()

    /**
     * Represents an error condition, with a [message] describing the failure.
     *
     * @property message A short description of the error encountered.
     */
    data class Error(val message: String) : WeatherUiState()


    /**
     * A successful state containing fetched weather data.
     *
     * @property temperature The current temperature (e.g., "20°C").
     * @property iconRes A drawable resource ID for the current weather condition (sun, rain, etc.).
     * @property lastUpdated A UNIX timestamp (milliseconds) indicating when the data was last fetched.
     */
    data class Success(
        val temperature: String,
        val iconRes: Int,
        val lastUpdated: Long
    ) : WeatherUiState()


    /**
     * Convenience property indicating if this state represents a loading condition.
     */
    val isLoading: Boolean
        get() = this is Loading


    /**
     * Convenience property indicating if this state represents an error condition.
     */
    val isError: Boolean
        get() = this is Error


    /**
     * Convenience property indicating if this state represents a successful condition.
     */
    val isSuccess: Boolean
        get() = this is Success
}
