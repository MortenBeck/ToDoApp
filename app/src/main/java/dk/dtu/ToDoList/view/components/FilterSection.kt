package dk.dtu.ToDoList.view.components

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
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSection(
    onFilterChange: (List<Task>) -> Unit,
    tasks: List<Task>
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var selectedTags by remember { mutableStateOf<Set<TaskTag>>(emptySet()) }
    var selectedPriorities by remember { mutableStateOf<Set<TaskPriority>>(emptySet()) }
    var hideCompletedTasks by remember { mutableStateOf(false) }

    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotation"
    )

    // Function to reset all filters
    fun resetFilters() {
        selectedDate = null
        selectedTags = emptySet()
        selectedPriorities = emptySet()
        hideCompletedTasks = false
        applyFilters(tasks, null, emptySet(), emptySet(), false, onFilterChange)
    }

    // Function to toggle date selection
    fun toggleDateSelection(date: Date) {
        selectedDate = if (selectedDate == date) null else date
        applyFilters(
            tasks,
            selectedDate,
            selectedTags,
            selectedPriorities,
            hideCompletedTasks,
            onFilterChange
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
                        "Today's Tasks",
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
            if (isExpanded && (selectedDate != null || selectedTags.isNotEmpty() ||
                        selectedPriorities.isNotEmpty() || hideCompletedTasks)) {
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
            // Date Filter Section
            Text(
                text = "Deadline Date:",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time
                val today = Calendar.getInstance().time
                val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.time

                FilterChip(
                    selected = selectedDate == yesterday,
                    onClick = { toggleDateSelection(yesterday) },
                    label = { Text("Yesterday") }
                )
                FilterChip(
                    selected = selectedDate == today,
                    onClick = { toggleDateSelection(today) },
                    label = { Text("Today") }
                )
                FilterChip(
                    selected = selectedDate == tomorrow,
                    onClick = { toggleDateSelection(tomorrow) },
                    label = { Text("Tomorrow") }
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
                        TaskTag.WORK -> Color(0xFF6d8FFF)      // Work Blue
                        TaskTag.SCHOOL -> Color(0xFFFF9c6d)    // School Orange
                        TaskTag.SPORT -> Color(0xFFd631bb)     // Sport Pink
                        TaskTag.TRANSPORT -> Color(0xFFFFF86d) // Transport Yellow
                        TaskTag.PET -> Color(0xFF6dFF6d)       // Pet Green
                        TaskTag.HOME -> Color(0xFFd16dFF)      // Home Purple
                        TaskTag.PRIVATE -> Color(0xFFff6D6D)   // Private Red
                        TaskTag.SOCIAL -> Color(0xFF80DEEA)    // Social Cyan
                    }
                    val iconResource = when (tag) {
                        TaskTag.WORK -> R.drawable.work
                        TaskTag.SCHOOL -> R.drawable.school
                        TaskTag.PET -> R.drawable.pet
                        TaskTag.SPORT -> R.drawable.sport
                        TaskTag.HOME -> R.drawable.home_black
                        TaskTag.TRANSPORT -> R.drawable.transport
                        TaskTag.PRIVATE -> R.drawable.lock
                        TaskTag.SOCIAL -> R.drawable.social
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
                                selectedDate,
                                selectedTags,
                                selectedPriorities,
                                hideCompletedTasks,
                                onFilterChange
                            )
                        },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = iconResource),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Black
                                )
                                Text(tag.name)
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) tagColor else tagColor.copy(alpha = 0.3f))
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
                                selectedDate,
                                selectedTags,
                                selectedPriorities,
                                hideCompletedTasks,
                                onFilterChange
                            )
                        },
                        label = { Text(priority.name) },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) priorityColor else priorityColor.copy(alpha = 0.3f))
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
                        applyFilters(tasks, selectedDate, selectedTags, selectedPriorities, hideCompletedTasks, onFilterChange)
                    }
                )
            }
        }
    }
}

// Improved filter function with proper date comparison
private fun applyFilters(
    tasks: List<Task>,
    selectedDate: Date?,
    selectedTags: Set<TaskTag>,
    selectedPriorities: Set<TaskPriority>,
    hideCompletedTasks: Boolean,
    onFilterChange: (List<Task>) -> Unit
) {
    val filteredList = tasks.filter { task ->
        val dateMatches = selectedDate?.let { date ->
            val taskCal = Calendar.getInstance().apply { time = task.deadline }
            val selectedCal = Calendar.getInstance().apply { time = date }

            taskCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                    taskCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
                    taskCal.get(Calendar.DAY_OF_MONTH) == selectedCal.get(Calendar.DAY_OF_MONTH)
        } ?: true

        val tagMatches = selectedTags.isEmpty() || task.tag in selectedTags

        val priorityMatches = selectedPriorities.isEmpty() || task.priority in selectedPriorities

        val completionMatches = !hideCompletedTasks || !task.completed

        dateMatches && tagMatches && priorityMatches && completionMatches
    }

    onFilterChange(filteredList)
}
