package dk.dtu.ToDoList.model.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Data class representing the overall structure of the weather response
 * from the OpenWeatherMap API.
 *
 * @property weather A list of [WeatherInfo] containing descriptive details like
 * the weather condition and icon.
 * @property main A [MainInfo] object containing temperature data.
 * @property name The name of the city for which the weather data is relevant.
 */
data class WeatherResponse(
    val weather: List<WeatherInfo>,
    val main: MainInfo,
    val name: String
)


/**
 * Data class representing basic weather description information.
 *
 * @property description A textual description of the weather condition (e.g., "clear sky").
 * @property icon The code representing the weather icon returned by the API.
 */
data class WeatherInfo(
    val description: String,
    val icon: String)


/**
 * Data class holding main weather parameters.
 *
 * @property temp The current temperature in the specified unit (e.g., Celsius).
 */
data class MainInfo(val temp: Double)


/**
 * Retrofit interface defining the OpenWeatherMap endpoints used in this application.
 */
interface WeatherApi {
    /**
     * Retrieves the current weather for a given city.
     *
     * @param city The name of the city for which weather data should be fetched.
     * @param units The unit system for temperature ("metric" for Celsius, "imperial" for Fahrenheit, etc.).
     * @param apiKey The API key to authorize requests to the OpenWeatherMap API.
     * @return A [WeatherResponse] containing all relevant weather information.
     */
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): WeatherResponse
}


/**
 * A service class responsible for setting up and making requests to the
 * OpenWeatherMap API using Retrofit and OkHttp.
 */
class WeatherService {

    /**
     * The API key used for authenticating with the OpenWeatherMap service.
     */
    private val apiKey = "f062e109162abcf31ec182583cae89e0"

    /**
     * A reference to the [WeatherApi] interface for making network requests.
     */
    private val weatherApi: WeatherApi = provideWeatherApi()


    /**
     * Provides a configured [WeatherApi] instance with logging for debug purposes.
     *
     * @return A [WeatherApi] implementation backed by Retrofit.
     */
    private fun provideWeatherApi(): WeatherApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherApi::class.java)
    }

    /**
     * Gets the current weather for a given city.
     *
     * @param city The name of the city. Defaults to "Copenhagen".
     * @return A [WeatherResponse] containing the current weather data.
     */
    suspend fun getWeatherForCity(city: String = "Copenhagen"): WeatherResponse {
        return weatherApi.getCurrentWeather(city, "metric", apiKey)
    }
}