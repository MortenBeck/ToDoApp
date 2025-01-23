package dk.dtu.ToDoList.presentation.view.components.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.FilterChip
import dk.dtu.ToDoList.domain.model.TaskTag

@Composable
fun CategorySelector(
    selectedTag: TaskTag,
    onTagSelected: (TaskTag) -> Unit
) {
    Column {
        Text("Category", style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            TaskCategory.values().forEach { category ->
                FilterChip(
                    selected = category == selectedTag,
                    onClick = { onTagSelected(category) },
                    label = { Text(category.name) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}