package dk.dtu.ToDoList.feature

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TaskTag


@Composable
fun FilterSection(
    onFilterChange: (List<Task>) -> Unit,
    tasks: List<Task>
) {
    var expanded by remember { mutableStateOf(false) }
    var showCompleted by remember { mutableStateOf(true) }
    var showFavoriteOnly by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf<TaskTag?>(null) }
    var selectedPriority by remember { mutableStateOf<TaskPriority?>(null) } // Track selected priority

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Filter Tasks",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Dropdown Menu Button
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Filter Options")
        }

        // Dropdown Menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Filter by Tag
            DropdownMenuItem(
                text = { Text("All Tasks") },
                onClick = {
                    selectedTag = null
                    selectedPriority = null // Reset priority filter
                    expanded = false
                    applyFilters(tasks, selectedTag, selectedPriority, showCompleted, showFavoriteOnly, onFilterChange)
                }
            )
            TaskTag.values().forEach { tag ->
                DropdownMenuItem(
                    text = { Text("Tag: ${tag.name}") },
                    onClick = {
                        selectedTag = tag
                        expanded = false
                        applyFilters(tasks, selectedTag, selectedPriority, showCompleted, showFavoriteOnly, onFilterChange)
                    }
                )
            }

            HorizontalDivider()

            // Filter by Priority
            DropdownMenuItem(
                text = { Text("All Priorities") },
                onClick = {
                    selectedPriority = null
                    expanded = false
                    applyFilters(tasks, selectedTag, selectedPriority, showCompleted, showFavoriteOnly, onFilterChange)
                }
            )
            TaskPriority.values().forEach { priority ->
                DropdownMenuItem(
                    text = { Text("Priority: ${priority.name}") },
                    onClick = {
                        selectedPriority = priority
                        expanded = false
                        applyFilters(tasks, selectedTag, selectedPriority, showCompleted, showFavoriteOnly, onFilterChange)
                    }
                )
            }

            HorizontalDivider()

            // Filter by Completion
            DropdownMenuItem(
                text = { Text(if (showCompleted) "Hide Completed Tasks" else "Show Completed Tasks") },
                onClick = {
                    showCompleted = !showCompleted
                    expanded = false
                    applyFilters(tasks, selectedTag, selectedPriority, showCompleted, showFavoriteOnly, onFilterChange)
                }
            )

            // Filter by Favorite
            DropdownMenuItem(
                text = { Text(if (showFavoriteOnly) "Show All Tasks" else "Favorites Only") },
                onClick = {
                    showFavoriteOnly = !showFavoriteOnly
                    expanded = false
                    applyFilters(tasks, selectedTag, selectedPriority, showCompleted, showFavoriteOnly, onFilterChange)
                }
            )

            HorizontalDivider()

            // Reset Filters
            DropdownMenuItem(
                text = { Text("Reset Filters") },
                onClick = {
                    selectedTag = null
                    selectedPriority = null
                    showCompleted = true
                    showFavoriteOnly = false
                    expanded = false
                    applyFilters(tasks, selectedTag, selectedPriority, showCompleted, showFavoriteOnly, onFilterChange)
                }
            )
        }
    }
}

private fun applyFilters(
    tasks: List<Task>,
    selectedTag: TaskTag?,
    selectedPriority: TaskPriority?, // Add priority filter
    showCompleted: Boolean,
    showFavoriteOnly: Boolean,
    onFilterChange: (List<Task>) -> Unit
) {
    val filteredList = tasks.filter { task ->
        val matchesTag = selectedTag?.let { task.tag == it } ?: true
        val matchesPriority = selectedPriority?.let { task.priority == it } ?: true
        val matchesCompletion = if (!showCompleted) !task.completed else true
        val matchesFavorite = if (showFavoriteOnly) task.favorite else true
        matchesTag && matchesPriority && matchesCompletion && matchesFavorite
    }
    onFilterChange(filteredList)
}