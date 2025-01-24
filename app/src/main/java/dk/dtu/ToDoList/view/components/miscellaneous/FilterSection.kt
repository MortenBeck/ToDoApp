package dk.dtu.ToDoList.view.components.miscellaneous

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
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
    var selectedTag by remember { mutableStateOf<TaskTag?>(null) }
    var selectedPriority by remember { mutableStateOf<TaskPriority?>(null) }
    var hideCompletedTasks by remember { mutableStateOf(false) }
    var showCalendarPicker by remember { mutableStateOf(false) }

    var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEndDate by remember { mutableStateOf<LocalDate?>(null) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var isSelectingStartDate by remember { mutableStateOf(true) }

    //This function by ChatGPT
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


    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        ElevatedCard(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Date Range Section
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Date Range",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                FilledTonalButton(
                                    onClick = {
                                        resetFilters()
                                        taskListViewModel.resetToOriginal()
                                    },
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Reset Filters",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Reset",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            // Date Selection Button
                            FilledTonalButton(
                                onClick = {
                                    isSelectingStartDate = true
                                    showCalendarPicker = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = "Select date",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when {
                                        selectedStartDate != null && selectedEndDate != null ->
                                            "${selectedStartDate!!.format(dateFormatter)} - ${selectedEndDate!!.format(dateFormatter)}"
                                        selectedStartDate != null ->

                                            "From ${selectedStartDate!!.format(dateFormatter)}"
                                        selectedEndDate != null ->

                                            "Until ${selectedEndDate!!.format(dateFormatter)}"
                                        else -> "Select dates"
                                    },
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Quick Date Selection (Yesterday, Today, Tomorrow)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(
                                    Triple(
                                        "Yesterday",
                                        Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                                        -1
                                    ),
                                    Triple("Today", Icons.Default.CalendarToday, 0),
                                    Triple(
                                        "Tomorrow",
                                        Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                        1
                                    )
                                ).forEach { (label, icon, offset) ->
                                    val isSelected = when (offset) {
                                        -1 -> selectedStartDate?.equals(LocalDate.now().minusDays(1)) == true
                                        0 -> selectedStartDate?.equals(LocalDate.now()) == true
                                        1 -> selectedStartDate?.equals(LocalDate.now().plusDays(1)) == true
                                        else -> false
                                    }

                                    FilledTonalButton(
                                        onClick = {
                                            val quickDate = LocalDate.now().plusDays(offset.toLong())
                                            selectedStartDate = quickDate
                                            selectedEndDate = quickDate
                                            val quickStartDate = Date.from(quickDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
                                            dateRange = quickStartDate to quickStartDate
                                            taskListViewModel.applyFilters(
                                                dateRange = dateRange,
                                                selectedTag = selectedTag,
                                                selectedPriority = selectedPriority,
                                                hideCompletedTasks = hideCompletedTasks
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = if (isSelected)
                                                MaterialTheme.colorScheme.primaryContainer
                                            else
                                                MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = label,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            else
                                                MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.labelSmall,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }
                        }
                        if (showCalendarPicker) {
                            Dialog(onDismissRequest = { showCalendarPicker = false }) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = if (isSelectingStartDate) "Select Start Date" else "Select End Date",
                                            style = MaterialTheme.typography.titleLarge,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )

                                        Calendar(
                                            selectedDate = selectedStartDate ?: LocalDate.now(),
                                            currentMonth = currentMonth,
                                            onDateSelected = { date ->
                                                if (isSelectingStartDate) {
                                                    selectedStartDate = date
                                                    isSelectingStartDate = false
                                                } else {
                                                    if (date >= selectedStartDate) {
                                                        selectedEndDate = date
                                                        val startDate = Date.from(
                                                            selectedStartDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                                        )
                                                        val endDate = Date.from(
                                                            date.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                                        )
                                                        dateRange = startDate to endDate
                                                        showCalendarPicker = false
                                                        taskListViewModel.applyFilters(
                                                            dateRange = dateRange,
                                                            selectedTag = selectedTag,
                                                            selectedPriority = selectedPriority,
                                                            hideCompletedTasks = hideCompletedTasks
                                                        )
                                                    }
                                                }
                                            },
                                            onMonthChanged = { month ->
                                                currentMonth = month
                                            },
                                            tasks = tasks
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Action Buttons
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    selectedStartDate = null
                                                    selectedEndDate = null
                                                    dateRange = null to null
                                                    showCalendarPicker = false
                                                }
                                            ) {
                                                Text("Clear")
                                            }
                                            TextButton(
                                                onClick = {
                                                    if (selectedStartDate != null && selectedEndDate == null) {
                                                        // If only start date is selected, use it as both start and end
                                                        selectedEndDate = selectedStartDate
                                                    } else if (selectedStartDate == null && selectedEndDate != null) {
                                                        // If only end date is selected, use it as both start and end
                                                        selectedStartDate = selectedEndDate
                                                    }

                                                    if (selectedStartDate != null && selectedEndDate != null) {
                                                        // Ensure the end date is not earlier than the start date
                                                        if (selectedEndDate!!.isBefore(selectedStartDate)) {
                                                            selectedEndDate = selectedStartDate
                                                        }

                                                        // Set the date range
                                                        val startDate = Date.from(
                                                            selectedStartDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                                        )
                                                        val endDate = Date.from(
                                                            selectedEndDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                                        )
                                                        dateRange = startDate to endDate

                                                        // Apply filters with the updated date range
                                                        taskListViewModel.applyFilters(
                                                            dateRange = dateRange,
                                                            selectedTag = selectedTag,
                                                            selectedPriority = selectedPriority,
                                                            hideCompletedTasks = hideCompletedTasks
                                                        )
                                                    }

                                                    // Reset the calendar picker dialog
                                                    showCalendarPicker = false
                                                }
                                            ) {
                                                Text("Confirm")
                                            }
                                        }
                                    }
                                }
                            }
                        }



                        // Tags Section
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                    }
                }
            }
        }
    }
}
