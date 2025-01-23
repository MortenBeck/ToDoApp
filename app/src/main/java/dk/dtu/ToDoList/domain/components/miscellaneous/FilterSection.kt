package dk.dtu.ToDoList.domain.components.miscellaneous

import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.domain.model.Task
import dk.dtu.ToDoList.domain.model.TaskPriority
import dk.dtu.ToDoList.domain.model.TaskTag
import dk.dtu.ToDoList.presentation.viewmodel.FilterViewModel

/**
 * A composable function that displays a collapsible filter panel for filtering a list of [Task] objects.
 * It supports filtering by:
 * - Date range (with a calendar picker and quick-select options)
 * - Tags ([TaskTag])
 * - Priorities ([TaskPriority])
 * - Completion status (hide completed tasks)
 *
 * When any filter is changed, the filtered list is emitted via [onFilterChange].
 *
 * @param onFilterChange A callback that returns the filtered list of [Task] objects.
 * @param tasks The original (unfiltered) list of [Task] objects to be filtered.
 * @param modifier An optional [Modifier] for styling the container of this composable.
 */
@Composable
fun FilterSection(
    viewModel: FilterViewModel,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filters", style = MaterialTheme.typography.titleMedium)
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess
                    else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                // Date Filter
                FilterChip(
                    selected = viewModel.hasDateFilter,
                    onClick = { viewModel.showDatePicker() },
                    label = { Text("Date Range") }
                )

                // Tag Filter
                FilterChip(
                    selected = viewModel.hasTagFilter,
                    onClick = { viewModel.showTagPicker() },
                    label = { Text("Tags") }
                )

                // Priority Filter
                FilterChip(
                    selected = viewModel.hasPriorityFilter,
                    onClick = { viewModel.showPriorityPicker() },
                    label = { Text("Priority") }
                )

                // Hide Completed Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hide Completed")
                    Switch(
                        checked = viewModel.hideCompleted,
                        onCheckedChange = { viewModel.updateHideCompleted(it) }
                    )
                }
            }
        }
    }
}