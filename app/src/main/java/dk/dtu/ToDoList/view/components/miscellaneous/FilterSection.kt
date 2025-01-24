package dk.dtu.ToDoList.view.components.miscellaneous

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dk.dtu.ToDoList.model.data.task.Task
import dk.dtu.ToDoList.model.data.task.TaskPriority
import dk.dtu.ToDoList.model.data.task.TaskTag
import dk.dtu.ToDoList.view.core.theme.getPrioColor
import dk.dtu.ToDoList.view.core.theme.getTaskColor
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import dk.dtu.ToDoList.viewmodel.TaskListViewModel

@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FilterSection(
    tasks: List<Task>,
    onFilterChange: (List<Task>) -> Unit,
    taskListViewModel: TaskListViewModel,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var dateRange by remember { mutableStateOf<Pair<Date?, Date?>>(null to null) }
    var selectedTag by remember { mutableStateOf<TaskTag?>(null) } // Single tag
    var selectedPriority by remember { mutableStateOf<TaskPriority?>(null) } // Single priority
    var hideCompletedTasks by remember { mutableStateOf(false) }
    var showCalendarPicker by remember { mutableStateOf(false) }

    // Calendar states
    var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEndDate by remember { mutableStateOf<LocalDate?>(null) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var isSelectingStartDate by remember { mutableStateOf(true) }

    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotation"
    )

    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    fun resetFilters() {
        dateRange = null to null
        selectedStartDate = null
        selectedEndDate = null
        selectedTag = null
        selectedPriority = null
        hideCompletedTasks = false
        onFilterChange(tasks)
        taskListViewModel.loadTasks()
    }

    fun applyFilters() {
        val filteredList = tasks.filter { task ->
            val dateMatches = if (dateRange.first != null && dateRange.second != null) {
                val taskDate = task.deadline.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val startDate = dateRange.first!!.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                val endDate = dateRange.second!!.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                !taskDate.isBefore(startDate) && !taskDate.isAfter(endDate)
            } else true

            val tagMatches = selectedTag == null || task.tag == selectedTag
            val priorityMatches = selectedPriority == null || task.priority == selectedPriority
            val completionMatches = !hideCompletedTasks || !task.completed

            dateMatches && tagMatches && priorityMatches && completionMatches
        }

        if (filteredList.isEmpty() && (selectedTag != null || selectedPriority != null)) {
            // If the filtered list is empty, emit an empty list to reflect no results
            onFilterChange(emptyList())
        } else {
            onFilterChange(filteredList)
        }
    }


    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        ElevatedCard(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Filters",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.rotate(rotationState)
                    )
                }

                if (isExpanded) {
                    HorizontalDivider()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Date Range Section
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Date Range", style = MaterialTheme.typography.titleMedium)
                            FilledTonalButton(
                                onClick = {
                                    isSelectingStartDate = true
                                    showCalendarPicker = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = "Select date"
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = when {
                                        selectedStartDate != null && selectedEndDate != null ->
                                            "${selectedStartDate!!.format(dateFormatter)} - ${selectedEndDate!!.format(dateFormatter)}"
                                        selectedStartDate != null ->
                                            "From ${selectedStartDate!!.format(dateFormatter)}"
                                        selectedEndDate != null ->
                                            "Until ${selectedEndDate!!.format(dateFormatter)}"
                                        else -> "Select dates"
                                    }
                                )
                            }
                        }

                        // Tags Section
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Tags", style = MaterialTheme.typography.titleMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TaskTag.entries.forEach { tag ->
                                    val tagColor = getTaskColor(tag)
                                    FilterChip(
                                        selected = selectedTag == tag,
                                        onClick = {
                                            selectedTag = if (selectedTag == tag) null else tag
                                            taskListViewModel.applyFilters(
                                                dateRange = dateRange,
                                                selectedTag = selectedTag,
                                                selectedPriority = selectedPriority,
                                                hideCompletedTasks = hideCompletedTasks
                                            )
                                        },
                                        label = {
                                            Text(
                                                tag.name,
                                                color = if (selectedTag == tag) Color.White else tagColor
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = tagColor,
                                            containerColor = tagColor.copy(alpha = 0.08f)
                                        )
                                    )
                                }
                            }
                        }

                        // Priority Section
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Priority", style = MaterialTheme.typography.titleMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TaskPriority.entries.forEach { priority ->
                                    val priorityColor = getPrioColor(priority)
                                    FilterChip(
                                        selected = selectedPriority == priority,
                                        onClick = {
                                            selectedPriority = if (selectedPriority == priority) null else priority
                                            taskListViewModel.applyFilters(
                                                dateRange = dateRange,
                                                selectedTag = selectedTag,
                                                selectedPriority = selectedPriority,
                                                hideCompletedTasks = hideCompletedTasks
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = priority.name,
                                                color = if (selectedPriority == priority) Color.White else priorityColor
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = priorityColor,
                                            containerColor = priorityColor.copy(alpha = 0.08f)
                                        )
                                    )
                                }
                            }
                        }

                        // Hide Completed Tasks Switch
                        ListItem(
                            headlineContent = { Text("Hide Completed Tasks") },
                            trailingContent = {
                                Switch(
                                    checked = hideCompletedTasks,
                                    onCheckedChange = {
                                        hideCompletedTasks = it
                                        applyFilters()
                                    }
                                )
                            }
                        )

                        // Reset Filters Button
                        FilledTonalButton(
                            onClick = { resetFilters() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Reset Filters",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reset", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
