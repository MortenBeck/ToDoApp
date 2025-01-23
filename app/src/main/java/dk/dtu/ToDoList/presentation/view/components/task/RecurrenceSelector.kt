package dk.dtu.ToDoList.presentation.view.components.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.domain.model.RecurrencePattern
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.FilterChip

@Composable
fun RecurrenceSelector(
    selectedRecurrence: RecurrencePattern?,
    onRecurrenceSelected: (RecurrencePattern) -> Unit
) {
    Column {
        Text("Repeat", style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            RecurrencePattern.entries.forEach { pattern ->
                FilterChip(
                    selected = pattern == selectedRecurrence,
                    onClick = { onRecurrenceSelected(pattern) },
                    label = { Text(pattern.name) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}