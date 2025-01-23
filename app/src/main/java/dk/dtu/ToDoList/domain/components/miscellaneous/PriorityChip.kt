package dk.dtu.ToDoList.domain.components.miscellaneous

import androidx.compose.runtime.Composable
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text

@Composable
fun PriorityChip(
    text: String,
    selectedPriority: String,
    onSelect: () -> Unit
) {
    FilterChip(
        selected = text == selectedPriority,
        onClick = onSelect,
        label = { Text(text) }
    )
}