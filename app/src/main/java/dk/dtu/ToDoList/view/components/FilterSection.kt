package dk.dtu.ToDoList.view.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskPriority
import dk.dtu.ToDoList.model.data.TaskTag
import dk.dtu.ToDoList.model.data.TaskTag.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    onFilterChange: (List<Task>) -> Unit,
    tasks: List<Task>
) {
    var isExpanded by remember { mutableStateOf(false) }
    var dateRange by remember { mutableStateOf<Pair<Date?, Date?>>(null to null) }
    var selectedTags by remember { mutableStateOf<Set<TaskTag>>(emptySet()) }
    var selectedPriorities by remember { mutableStateOf<Set<TaskPriority>>(emptySet()) }
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
        selectedTags = emptySet()
        selectedPriorities = emptySet()
        hideCompletedTasks = false
        applyFilters(tasks, dateRange, selectedTags, selectedPriorities, false, onFilterChange)
    }

    // Quick date selection helper function
    fun selectQuickDate(daysOffset: Int) {
        val date = LocalDate.now().plusDays(daysOffset.toLong())
        selectedStartDate = date
        selectedEndDate = date
        val selectedDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
        dateRange = selectedDate to selectedDate
        applyFilters(tasks, dateRange, selectedTags, selectedPriorities, hideCompletedTasks, onFilterChange)
    }

    if (showCalendarPicker) {
        AlertDialog(
            onDismissRequest = { showCalendarPicker = false },
            title = { Text("Select Date Range") },
            text = {
                Column {
                    // Toggle buttons for start/end date selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { isSelectingStartDate = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelectingStartDate)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("Start Date")
                        }
                        Button(
                            onClick = { isSelectingStartDate = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isSelectingStartDate)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("End Date")
                        }
                    }

                    // Show selected dates
                    Text(
                        text = "Start: ${selectedStartDate?.format(dateFormatter) ?: "Not set"}",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "End: ${selectedEndDate?.format(dateFormatter) ?: "Not set"}",
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Calendar
                    Calendar(
                        selectedDate = if (isSelectingStartDate)
                            selectedStartDate ?: LocalDate.now()
                        else
                            selectedEndDate ?: LocalDate.now(),
                        currentMonth = currentMonth,
                        onDateSelected = { date ->
                            if (isSelectingStartDate) {
                                selectedStartDate = date
                                isSelectingStartDate = false
                            } else {
                                selectedEndDate = date
                            }
                        },
                        onMonthChanged = { month ->
                            currentMonth = month
                        },
                        tasks = tasks
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (selectedStartDate != null && selectedEndDate != null) {
                            // Convert LocalDate to Date
                            val startDate = Date.from(
                                selectedStartDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                            )
                            val endDate = Date.from(
                                selectedEndDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                            )
                            dateRange = startDate to endDate
                            applyFilters(
                                tasks,
                                dateRange,
                                selectedTags,
                                selectedPriorities,
                                hideCompletedTasks,
                                onFilterChange
                            )
                        }
                        showCalendarPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCalendarPicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header with expansion arrow and reset button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Tasks Filter",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.rotate(rotationState),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Reset button
            if (isExpanded && (dateRange.first != null || dateRange.second != null ||
                        selectedTags.isNotEmpty() || selectedPriorities.isNotEmpty() ||
                        hideCompletedTasks)
            ) {
                IconButton(
                    onClick = { resetFilters() },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Reset Filters",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (isExpanded) {
            // Date Range Selection
            Text(
                text = "Date Range:",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // Quick date selection buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStartDate?.equals(LocalDate.now().minusDays(1)) == true,
                    onClick = { selectQuickDate(-1) },
                    label = { Text("Yesterday") }
                )
                FilterChip(
                    selected = selectedStartDate?.equals(LocalDate.now()) == true,
                    onClick = { selectQuickDate(0) },
                    label = { Text("Today") }
                )
                FilterChip(
                    selected = selectedStartDate?.equals(LocalDate.now().plusDays(1)) == true,
                    onClick = { selectQuickDate(1) },
                    label = { Text("Tomorrow") }
                )
            }

            // Date picker button
            Button(
                onClick = { showCalendarPicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    when {
                        selectedStartDate != null && selectedEndDate != null -> {
                            "${selectedStartDate!!.format(dateFormatter)} - ${selectedEndDate!!.format(dateFormatter)}"
                        }
                        selectedStartDate != null -> {
                            "From ${selectedStartDate!!.format(dateFormatter)}"
                        }
                        selectedEndDate != null -> {
                            "Until ${selectedEndDate!!.format(dateFormatter)}"
                        }
                        else -> "Select Date Range"
                    },
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            // Tags Section
            Text(
                text = "Tags:",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 3
            ) {
                TaskTag.entries.forEach { tag ->
                    val isSelected = selectedTags.contains(tag)
                    val tagColor = when (tag) {
                        TaskTag.WORK -> Color(0xFF6d8FFF)
                        TaskTag.SCHOOL -> Color(0xFFFF9c6d)
                        TaskTag.SPORT -> Color(0xFFd631bb)
                        TaskTag.TRANSPORT -> Color(0xFFFFF86d)
                        TaskTag.PET -> Color(0xFF6dFF6d)
                        TaskTag.HOME -> Color(0xFFd16dFF)
                        TaskTag.PRIVATE -> Color(0xFFff6D6D)
                        TaskTag.SOCIAL -> Color(0xFF80DEEA)
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedTags = if (isSelected) {
                                selectedTags - tag
                            } else {
                                selectedTags + tag
                            }
                            applyFilters(
                                tasks,
                                dateRange,
                                selectedTags,
                                selectedPriorities,
                                hideCompletedTasks,
                                onFilterChange
                            )
                        },
                        label = { Text(tag.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (isSelected) tagColor else tagColor.copy(alpha = 0.3f),
                            labelColor = Color.Black
                        )
                    )
                }
            }

            // Priority Section
            Text(
                text = "Priority:",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskPriority.entries.forEach { priority ->
                    val isSelected = selectedPriorities.contains(priority)
                    val priorityColor = when (priority) {
                        TaskPriority.HIGH -> Color(0xFFff6D6D)
                        TaskPriority.MEDIUM -> Color(0xFFFFF86d)
                        TaskPriority.LOW -> Color(0xFF6d8FFF)
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedPriorities = if (isSelected) {
                                selectedPriorities - priority
                            } else {
                                selectedPriorities + priority
                            }
                            applyFilters(
                                tasks,
                                dateRange,
                                selectedTags,
                                selectedPriorities,
                                hideCompletedTasks,
                                onFilterChange
                            )
                        },
                        label = { Text(priority.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (isSelected) priorityColor else priorityColor.copy(alpha = 0.3f),
                            labelColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Hide Completed Tasks Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Hide Completed Tasks")
                Switch(
                    checked = hideCompletedTasks,
                    onCheckedChange = { checked ->
                        hideCompletedTasks = checked
                        applyFilters(
                            tasks,
                            dateRange,
                            selectedTags,
                            selectedPriorities,
                            hideCompletedTasks,
                            onFilterChange
                        )
                    }
                )
            }
        }
    }
}

private fun applyFilters(
    tasks: List<Task>,
    dateRange: Pair<Date?, Date?>,
    selectedTags: Set<TaskTag>,
    selectedPriorities: Set<TaskPriority>,
    hideCompletedTasks: Boolean,
    onFilterChange: (List<Task>) -> Unit
) {
    val filteredList = tasks.filter { task ->
        val dateMatches = if (dateRange.first != null && dateRange.second != null) {
            val taskCal = Calendar.getInstance().apply { time = task.deadline }
            val startCal = Calendar.getInstance().apply { time = dateRange.first!! }
            val endCal = Calendar.getInstance().apply { time = dateRange.second!! }

            // Reset time components to compare only dates
            listOf(taskCal, startCal, endCal).forEach { cal ->
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
            }

            // Check if task date is within range (inclusive)
            !taskCal.before(startCal) && !taskCal.after(endCal)
        } else true

        val tagMatches = selectedTags.isEmpty() || task.tag in selectedTags
        val priorityMatches = selectedPriorities.isEmpty() || task.priority in selectedPriorities
        val completionMatches = !hideCompletedTasks || !task.completed

        dateMatches && tagMatches && priorityMatches && completionMatches
    }

    onFilterChange(filteredList)
}