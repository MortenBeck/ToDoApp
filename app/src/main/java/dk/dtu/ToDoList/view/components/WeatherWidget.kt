package dk.dtu.ToDoList.view.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.viewmodel.WeatherUiState
import dk.dtu.ToDoList.viewmodel.WeatherViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.dtu.ToDoList.viewmodel.WeatherViewModelFactory

@Composable
fun WeatherWidget(viewModel: WeatherViewModel = viewModel(
    factory = WeatherViewModelFactory())
    ) {
        val weatherState by viewModel.weatherState.collectAsState()

    when (val state = weatherState) {
        is WeatherUiState.Loading -> CircularProgressIndicator()
        is WeatherUiState.Error -> ErrorDisplay()
        is WeatherUiState.Success -> WeatherContent(
            temperature = state.temperature,
            iconRes = state.iconRes
        )
    }
}

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
            contentDescription = "Weather icon"
        )
        Text(
            text = temperature,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun ErrorDisplay() {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(dk.dtu.ToDoList.R.drawable.priority),
            contentDescription = "Error icon"
        )
        Text(
            text = "Error fetching weather",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}