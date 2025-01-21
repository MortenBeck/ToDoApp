package dk.dtu.ToDoList.model.api

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WeatherServiceTest {

    private lateinit var weatherApi: WeatherApi
    private lateinit var weatherService: WeatherService

    @BeforeEach
    fun setUp() {
        // Mock the WeatherApi
        weatherApi = mockk()

        // Inject the mocked WeatherApi into WeatherService
        weatherService = WeatherService().apply {
            val privateWeatherApiField = WeatherService::class.java.getDeclaredField("weatherApi")
            privateWeatherApiField.isAccessible = true
            privateWeatherApiField.set(this, weatherApi)
        }
    }

    @Test
    fun `getWeatherForCity returns expected WeatherResponse`() = runBlocking {
        // Arrange
        val city = "Copenhagen"
        val mockResponse = WeatherResponse(
            weather = listOf(WeatherInfo(description = "clear sky", icon = "01d")),
            main = MainInfo(temp = 15.5),
            name = "Copenhagen"
        )

        // Mock the API call
        coEvery { weatherApi.getCurrentWeather(city, "metric", any()) } returns mockResponse

        // Act
        val result = weatherService.getWeatherForCity(city)

        // Assert
        assertEquals(mockResponse, result)
        assertEquals("clear sky", result.weather.first().description)
        assertEquals(15.5, result.main.temp)
        assertEquals("Copenhagen", result.name)
    }

    @Test
    fun `getWeatherForCity handles empty city name`() = runBlocking {
        // Arrange
        val emptyCity = ""
        val mockResponse = WeatherResponse(
            weather = listOf(WeatherInfo(description = "unknown", icon = "50d")),
            main = MainInfo(temp = 0.0),
            name = "Unknown"
        )

        coEvery { weatherApi.getCurrentWeather(emptyCity, "metric", any()) } returns mockResponse

        // Act
        val result = weatherService.getWeatherForCity(emptyCity)

        // Assert
        assertEquals(mockResponse, result)
        assertEquals("Unknown", result.name)
    }

    @Test
    fun `getWeatherForCity handles API failure gracefully`() = runBlocking {
        // Arrange
        val city = "Copenhagen"
        coEvery { weatherApi.getCurrentWeather(city, "metric", any()) } throws RuntimeException("Network error")

        // Act & Assert
        try {
            weatherService.getWeatherForCity(city)
            assert(false) { "Expected exception was not thrown" }
        } catch (e: Exception) {
            assert(e is RuntimeException)
            assertEquals("Network error", e.message)
        }
    }

    @Test
    fun `getWeatherForCity handles extreme temperatures`() = runBlocking {
        // Arrange
        val city = "Death Valley"
        val mockResponse = WeatherResponse(
            weather = listOf(WeatherInfo(description = "hot", icon = "01d")),
            main = MainInfo(temp = 56.7), // Record-breaking high temperature
            name = city
        )

        coEvery { weatherApi.getCurrentWeather(city, "metric", any()) } returns mockResponse

        // Act
        val result = weatherService.getWeatherForCity(city)

        // Assert
        assertEquals(mockResponse, result)
        assertEquals(56.7, result.main.temp)
    }

    @Test
    fun `getWeatherForCity handles multiple weather conditions`() = runBlocking {
        // Arrange
        val city = "London"
        val mockResponse = WeatherResponse(
            weather = listOf(
                WeatherInfo(description = "rainy", icon = "09d"),
                WeatherInfo(description = "windy", icon = "50d")
            ),
            main = MainInfo(temp = 12.3),
            name = city
        )

        coEvery { weatherApi.getCurrentWeather(city, "metric", any()) } returns mockResponse

        // Act
        val result = weatherService.getWeatherForCity(city)

        // Assert
        assertEquals(mockResponse, result)
        assertEquals(2, result.weather.size)
        assertEquals("rainy", result.weather[0].description)
        assertEquals("windy", result.weather[1].description)
    }

    @Test
    fun `getWeatherForCity handles invalid API key`() = runBlocking {
        // Arrange
        val city = "Copenhagen"
        coEvery { weatherApi.getCurrentWeather(city, "metric", any()) } throws RuntimeException("Invalid API key")

        // Act & Assert
        try {
            weatherService.getWeatherForCity(city)
            assert(false) { "Expected exception was not thrown" }
        } catch (e: Exception) {
            assert(e is RuntimeException)
            assertEquals("Invalid API key", e.message)
        }
    }





}
