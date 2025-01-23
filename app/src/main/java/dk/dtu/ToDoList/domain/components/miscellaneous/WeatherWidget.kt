package dk.dtu.ToDoList.domain.components.miscellaneous

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.presentation.WeatherUiState
import dk.dtu.ToDoList.presentation.WeatherViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.dtu.ToDoList.presentation.WeatherViewModelFactory



/**
 * A composable that observes a [WeatherViewModel]'s UI state and displays
 * a weather widget accordingly. It handles three states:
 * - [WeatherUiState.Loading]: Shows a [CircularProgressIndicator].
 * - [WeatherUiState.Error]: Shows an error icon and message.
 * - [WeatherUiState.Success]: Shows the weather icon and temperature.
 *
 * @param viewModel The [WeatherViewModel] instance (defaults to one created by [viewModel]).
 */
@Composable
fun WeatherWidget(
    viewModel: WeatherViewModel = viewModel(factory = WeatherViewModelFactory())
) {
    val weatherState by viewModel.weatherState.collectAsState()

    when (val state = weatherState) {
        is WeatherUiState.Loading -> CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = MaterialTheme.colorScheme.primary
        )
        is WeatherUiState.Error -> ErrorDisplay()
        is WeatherUiState.Success -> WeatherContent(
            temperature = state.temperature,
            iconRes = state.iconRes
        )
    }
}


/**
 * A private composable to display the weather icon and temperature.
 *
 * @param temperature The current temperature as a [String].
 * @param iconRes A drawable resource representing the current weather icon.
 */
@Composable
private fun WeatherContent(
    temperature: String,
    iconRes: Int
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = "Weather icon",
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = temperature,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}


/**
 * A private composable to display an error message when the weather fails to load.
 * It includes an icon and a descriptive text message indicating a fetch error.
 */
@Composable
private fun ErrorDisplay() {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(dk.dtu.ToDoList.R.drawable.priority),
            contentDescription = "Error icon",
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "Error fetching weather",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}