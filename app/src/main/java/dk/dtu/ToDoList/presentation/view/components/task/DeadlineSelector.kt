package dk.dtu.ToDoList.presentation.view.components.task

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.material3.OutlinedButton

@Composable
fun DeadlineSelector(
    selectedDate: LocalDate?,
    showDatePicker: Boolean,
    onShowDatePicker: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Column {
        Text("Deadline", style = MaterialTheme.typography.bodyMedium)
        OutlinedButton(
            onClick = onShowDatePicker,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "Select date")
        }
    }
}