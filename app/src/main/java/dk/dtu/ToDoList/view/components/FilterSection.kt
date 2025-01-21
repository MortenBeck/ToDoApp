package dk.dtu.ToDoList.view.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskPriority
import dk.dtu.ToDoList.model.data.TaskTag
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import dk.dtu.ToDoList.view.theme.getTaskColor
import dk.dtu.ToDoList.view.theme.getPrioColor

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    onFilterChange: (List<Task>) -> Unit,
    tasks: List<Task>,
    modifier: Modifier = Modifier
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

    fun selectQuickDate(daysOffset: Int) {
        val date = LocalDate.now().plusDays(daysOffset.toLong())
        selectedStartDate = date
        selectedEndDate = date
        val selectedDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
        dateRange = selectedDate to selectedDate
        applyFilters(tasks, dateRange, selectedTags, selectedPriorities, hideCompletedTasks, onFilterChange)
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
                                    // Convert LocalDate to Date and update dateRange
                                    val startDate = Date.from(
                                        selectedStartDate!!.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                    )
                                    val endDate = Date.from(
                                        date.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                    )
                                    dateRange = startDate to endDate
                                    showCalendarPicker = false
                                    // Apply filters with new date range
                                    applyFilters(
                                        tasks,
                                        dateRange,
                                        selectedTags,
                                        selectedPriorities,
                                        hideCompletedTasks,
                                        onFilterChange
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

                    // Action buttons
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
                            onClick = { showCalendarPicker = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filters",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            "Filters",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.rotate(rotationState)
                    )
                }

                if (isExpanded) {
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Date Range Section
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                    onClick = { resetFilters() },
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

                            // Quick Date Selection
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(
                                    Triple("Yesterday", Icons.Outlined.KeyboardArrowLeft, -1),
                                    Triple("Today", Icons.Default.CalendarToday, 0),
                                    Triple("Tomorrow", Icons.Outlined.KeyboardArrowRight, 1)
                                ).forEach { (label, icon, offset) ->
                                    val isSelected = when (offset) {
                                        -1 -> selectedStartDate?.equals(LocalDate.now().minusDays(1)) == true
                                        0 -> selectedStartDate?.equals(LocalDate.now()) == true
                                        1 -> selectedStartDate?.equals(LocalDate.now().plusDays(1)) == true
                                        else -> false
                                    }

                                    FilledTonalButton(
                                        onClick = { selectQuickDate(offset) },
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

                        // Tags Section
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Tags",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TaskTag.entries.forEach { tag ->
                                    val isSelected = selectedTags.contains(tag)
                                    val tagColor = getTaskColor(tag)

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
                                        label = {
                                            Text(
                                                tag.name,
                                                color = if (isSelected) Color.White else tagColor
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
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Priority",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TaskPriority.entries.forEach { priority ->
                                    val isSelected = selectedPriorities.contains(priority)
                                    val priorityColor = getPrioColor(priority)

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
                                        label = {
                                            Text(
                                                text = priority.name,
                                                color = if (isSelected) Color.White else priorityColor,
                                                style = MaterialTheme.typography.labelSmall,
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = when (priority) {
                                                    TaskPriority.HIGH -> Icons.Outlined.KeyboardDoubleArrowUp
                                                    TaskPriority.MEDIUM -> Icons.Outlined.DragHandle
                                                    TaskPriority.LOW -> Icons.Outlined.KeyboardDoubleArrowDown
                                                },
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else priorityColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = priorityColor,
                                            containerColor = priorityColor.copy(alpha = 0.08f)
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Hide Completed Tasks Switch
                        ListItem(
                            headlineContent = {
                                Text(
                                    "Hide Completed Tasks",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            trailingContent = {
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
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun applyFilters(
    tasks: List<Task>,
    dateRange: Pair<Date?, Date?>,
    selectedTags: Set<TaskTag>,
    selectedPriorities: Set<TaskPriority>,
    hideCompletedTasks: Boolean,
    onFilterChange: (List<Task>) -> Unit
) {
    val filteredList = tasks.filter { task ->
        // Date range filter
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

            // Inclusive date range check
            !taskDate.isBefore(startDate) && !taskDate.isAfter(endDate)
        } else true

        // Tag filter
        val tagMatches = selectedTags.isEmpty() || task.tag in selectedTags

        // Priority filter
        val priorityMatches = selectedPriorities.isEmpty() || task.priority in selectedPriorities

        // Completion status filter
        val completionMatches = !hideCompletedTasks || !task.completed

        dateMatches && tagMatches && priorityMatches && completionMatches
    }

    onFilterChange(filteredList)
}

