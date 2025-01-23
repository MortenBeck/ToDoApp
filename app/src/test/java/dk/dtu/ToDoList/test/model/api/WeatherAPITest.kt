import dk.dtu.ToDoList.model.api.*
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherServiceTest {

    private lateinit var mockWeatherApi: WeatherApi
    private lateinit var weatherService: WeatherService

    @BeforeEach
    fun setup() {
        // Mock the WeatherApi
        mockWeatherApi = mockk()

        // Inject the mock API into WeatherService
        weatherService = WeatherService().apply {
            // Override the private API with the mock (requires reflection or testing-specific class adjustment)
            val field = WeatherService::class.java.getDeclaredField("weatherApi")
            field.isAccessible = true
            field.set(this, mockWeatherApi)
        }
    }

    @Test
    @DisplayName("Should return weather data for a valid city")
    fun `test getWeatherForCity returns correct data`() = runBlocking {
        // Mocked response
        val mockWeatherResponse = WeatherResponse(
            weather = listOf(WeatherInfo(description = "clear sky", icon = "01d")),
            main = MainInfo(temp = 20.0),
            name = "Copenhagen"
        )

        // Mock API behavior
        coEvery {
            mockWeatherApi.getCurrentWeather("Copenhagen", "metric", any())
        } returns mockWeatherResponse

        // Call the method
        val result = weatherService.getWeatherForCity("Copenhagen")

        // Assertions
        assertNotNull(result)
        assertEquals("Copenhagen", result.name)
        assertEquals(20.0, result.main.temp)
        assertEquals("clear sky", result.weather.first().description)

        // Verify API was called
        coVerify(exactly = 1) {
            mockWeatherApi.getCurrentWeather("Copenhagen", "metric", any())
        }
    }

    @Test
    @DisplayName("Should handle API throwing exception for invalid city")
    fun `test getWeatherForCity handles exception`() = runBlocking {
        // Simulate API exception
        coEvery {
            mockWeatherApi.getCurrentWeather("InvalidCity", "metric", any())
        } throws RuntimeException("City not found")

        // Call the method and verify exception is thrown
        val exception = assertThrows<RuntimeException> {
            runBlocking {
                weatherService.getWeatherForCity("InvalidCity")
            }
        }

        // Verify exception message
        assertEquals("City not found", exception.message)

        // Verify API was called
        coVerify(exactly = 1) {
            mockWeatherApi.getCurrentWeather("InvalidCity", "metric", any())
        }
    }

    @Test
    @DisplayName("Should use default city when no city is provided")
    fun `test getWeatherForCity defaults to Copenhagen`() = runBlocking {
        // Mocked response for default city
        val mockWeatherResponse = WeatherResponse(
            weather = listOf(WeatherInfo(description = "clear sky", icon = "01d")),
            main = MainInfo(temp = 15.0),
            name = "Copenhagen"
        )

        coEvery {
            mockWeatherApi.getCurrentWeather("Copenhagen", "metric", any())
        } returns mockWeatherResponse

        // Call the method without specifying a city
        val result = weatherService.getWeatherForCity()

        // Assertions
        assertNotNull(result)
        assertEquals("Copenhagen", result.name)

        // Verify API was called
        coVerify(exactly = 1) {
            mockWeatherApi.getCurrentWeather("Copenhagen", "metric", any())
        }
    }
}
