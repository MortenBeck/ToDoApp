import dk.dtu.ToDoList.model.api.*
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Unit tests for [WeatherService].
 *
 * This class tests the integration between the [WeatherService] and its dependency, [WeatherApi],
 * by mocking API responses and verifying the behavior of [WeatherService].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherServiceTest {

    private lateinit var mockWeatherApi: WeatherApi
    private lateinit var weatherService: WeatherService

    /**
     * Sets up the test environment by initializing and injecting mocked dependencies.
     */
    @BeforeEach
    fun setup() {
        // Mock the WeatherApi
        mockWeatherApi = mockk()

        // Inject the mock API into WeatherService
        weatherService = WeatherService().apply {
            val field = WeatherService::class.java.getDeclaredField("weatherApi")
            field.isAccessible = true
            field.set(this, mockWeatherApi)
        }
    }

    /**
     * Tests that [WeatherService.getWeatherForCity] returns correct data
     * for a valid city by mocking the API response.
     */
    @Test
    @DisplayName("Should return weather data for a valid city")
    fun `test getWeatherForCity returns correct data`() = runBlocking {
        // Mocked response
        val mockWeatherResponse = WeatherResponse(
            weather = listOf(WeatherInfo(description = "clear sky", icon = "01d")),
            main = MainInfo(temp = 20.0),
            name = "Copenhagen"
        )

        coEvery {
            mockWeatherApi.getCurrentWeather("Copenhagen", "metric", any())
        } returns mockWeatherResponse

        val result = weatherService.getWeatherForCity("Copenhagen")

        assertNotNull(result)
        assertEquals("Copenhagen", result.name)
        assertEquals(20.0, result.main.temp)
        assertEquals("clear sky", result.weather.first().description)

        coVerify(exactly = 1) {
            mockWeatherApi.getCurrentWeather("Copenhagen", "metric", any())
        }
    }

    /**
     * Tests that [WeatherService.getWeatherForCity] properly handles exceptions
     * thrown by the API when an invalid city is requested.
     */
    @Test
    @DisplayName("Should handle API throwing exception for invalid city")
    fun `test getWeatherForCity handles exception`() = runBlocking {
        coEvery {
            mockWeatherApi.getCurrentWeather("InvalidCity", "metric", any())
        } throws RuntimeException("City not found")

        val exception = assertThrows<RuntimeException> {
            runBlocking {
                weatherService.getWeatherForCity("InvalidCity")
            }
        }

        assertEquals("City not found", exception.message)

        coVerify(exactly = 1) {
            mockWeatherApi.getCurrentWeather("InvalidCity", "metric", any())
        }
    }

    /**
     * Tests that [WeatherService.getWeatherForCity] uses the default city ("Copenhagen")
     * when no city is explicitly specified.
     */
    @Test
    @DisplayName("Should use default city when no city is provided")
    fun `test getWeatherForCity defaults to Copenhagen`() = runBlocking {
        val mockWeatherResponse = WeatherResponse(
            weather = listOf(WeatherInfo(description = "clear sky", icon = "01d")),
            main = MainInfo(temp = 15.0),
            name = "Copenhagen"
        )

        coEvery {
            mockWeatherApi.getCurrentWeather("Copenhagen", "metric", any())
        } returns mockWeatherResponse

        val result = weatherService.getWeatherForCity()

        assertNotNull(result)
        assertEquals("Copenhagen", result.name)

        coVerify(exactly = 1) {
            mockWeatherApi.getCurrentWeather("Copenhagen", "metric", any())
        }
    }
}
