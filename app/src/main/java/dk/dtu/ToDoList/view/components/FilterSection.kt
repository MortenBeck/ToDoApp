package dk.dtu.ToDoList.view.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
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
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskPriority
import dk.dtu.ToDoList.model.data.TaskTag
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

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

    fun selectQuickDate(daysOffset: Int) {
        val date = LocalDate.now().plusDays(daysOffset.toLong())
        selectedStartDate = date
        selectedEndDate = date
        val selectedDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
        dateRange = selectedDate to selectedDate
        applyFilters(tasks, dateRange, selectedTags, selectedPriorities, hideCompletedTasks, onFilterChange)
    }

    ElevatedCard(
        onClick = { isExpanded = !isExpanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                            onClick = { showCalendarPicker = true },
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        label,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
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
                                val tagColor = when (tag) {
                                    TaskTag.WORK -> Color(0xFF1A73E8)
                                    TaskTag.SCHOOL -> Color(0xFFE65100)
                                    TaskTag.SPORT -> Color(0xFF9C27B0)
                                    TaskTag.TRANSPORT -> Color(0xFFFFB300)
                                    TaskTag.PET -> Color(0xFF2E7D32)
                                    TaskTag.HOME -> Color(0xFF673AB7)
                                    TaskTag.PRIVATE -> Color(0xFFD32F2F)
                                    TaskTag.SOCIAL -> Color(0xFF0097A7)
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
                                val priorityColor = when (priority) {
                                    TaskPriority.HIGH -> Color(0xFFD32F2F)
                                    TaskPriority.MEDIUM -> Color(0xFFF57F17)
                                    TaskPriority.LOW -> Color(0xFF1565C0)
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
                                    label = {
                                        Text(
                                            priority.name,
                                            color = if (isSelected) Color.White else priorityColor
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
                                            tint = if (isSelected) Color.White else priorityColor
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

@Composable
fun SingleLineChipGroup(
    items: List<Pair<String, () -> Unit>>,
    selectedDate: LocalDate?
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { (label, onClick) ->
            val isSelected = when (label) {
                "Yesterday" -> selectedDate?.equals(LocalDate.now().minusDays(1)) == true
                "Today" -> selectedDate?.equals(LocalDate.now()) == true
                "Tomorrow" -> selectedDate?.equals(LocalDate.now().plusDays(1)) == true
                else -> false
            }

            FilterChip(
                selected = isSelected,
                onClick = onClick,
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
private fun SegmentedButton(
    items: List<String>,
    selectedIndex: Int,
    onItemSelection: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEachIndexed { index, item ->
            FilledTonalButton(
                onClick = { onItemSelection(index) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (selectedIndex == index)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Text(item)
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