package dk.dtu.ToDoList.view.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.model.data.Task
import dk.dtu.ToDoList.model.data.TaskPriority
import dk.dtu.ToDoList.model.data.TaskTag
import dk.dtu.ToDoList.view.theme.getPrioColor
import dk.dtu.ToDoList.view.theme.getTaskColor
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowUp
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.DragHandle



/**
 * A composable that displays a list of [Task] items. Each task is wrapped in a swipeable area, allowing
 * horizontal drag gestures for delete actions. The list can also be filtered by a [searchText].
 *
 * @param Tasks The list of [Task] objects to display.
 * @param searchText A string used to filter tasks by name. Matches are case-insensitive.
 * @param modifier A [Modifier] for customizing the layout or behavior of the list.
 * @param onDelete Callback invoked when a task is deleted.
 * @param onCompleteToggle Callback invoked when a task’s completion state is toggled.
 * @param onUpdateTask Callback invoked when a task is updated (e.g., after editing in a details dialog).
 * @param onDeleteRequest An additional callback for initiating delete actions (can be used for confirmations).
 */
@Composable
fun TaskList(
    Tasks: List<Task>,
    searchText: String,
    modifier: Modifier = Modifier,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onDeleteRequest: (Task) -> Unit  // Add this parameter to forward delete requests
) {
    val filteredTasks = if (searchText.isNotEmpty()) {
        Tasks.filter { it.name.contains(searchText, ignoreCase = true) }
    } else {
        Tasks
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(filteredTasks) { _, task ->
            SwipeableTaskItem(
                task = task,
                searchText = searchText,
                onDelete = onDelete,
                onCompleteToggle = onCompleteToggle,
                onUpdateTask = onUpdateTask,
                onDeleteRequest = onDeleteRequest  // Forward the delete request to parent
            )
        }
    }
}


/**
 * A composable that displays a small badge with a text label and an icon. Used for task metadata
 * such as dates or categories (tags).
 *
 * @param badgeText The text displayed within the badge.
 * @param badgeColor The background color of the badge.
 * @param badgeIcon A drawable resource ID for the badge icon.
 */
@Composable
private fun BadgeItem(
    badgeText: String,
    badgeColor: Color,
    badgeIcon: Int
) {
    Surface(
        color = badgeColor,
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(id = badgeIcon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = badgeText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


/**
 * A composable representing a single [Task] item card. It displays task priority, name,
 * deadline, tag, and completion state. Tapping the card opens a dialog to edit or delete the task.
 *
 * @param task The [Task] to be displayed.
 * @param searchText A string used to highlight matching parts of the task name.
 * @param onDelete Callback invoked when the task is deleted (single occurrence).
 * @param onCompleteToggle Callback invoked when the user toggles the task’s completion state.
 * @param onUpdateTask Callback invoked when the user saves updates to the task from the details dialog.
 * @param modifier A [Modifier] for customizing the layout or behavior of the card.
 */
@Composable
fun TaskItem(
    task: Task,
    searchText: String,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDetails by remember { mutableStateOf(false) }

    // Highlights part of the text matching the search query
    fun buildHighlightedText(text: String, query: String): AnnotatedString {
        val builder = AnnotatedString.Builder()
        val startIndex = text.indexOf(query, ignoreCase = true)
        if (startIndex != -1) {
            builder.append(text.substring(0, startIndex))
            builder.pushStyle(SpanStyle(background = Color.LightGray))
            builder.append(text.substring(startIndex, startIndex + query.length))
            builder.pop()
            builder.append(text.substring(startIndex + query.length))
        } else {
            builder.append(text)
        }
        return builder.toAnnotatedString()
    }

    val highlightedName = buildHighlightedText(task.name, searchText)

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { showDetails = true }, // Show TaskDetails dialog
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onCompleteToggle(task) }
            ) {
                Icon(
                    imageVector = if (task.completed) Icons.Default.CheckCircle
                    else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (task.completed) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = when (task.priority) {
                            TaskPriority.HIGH -> Icons.Outlined.KeyboardDoubleArrowUp
                            TaskPriority.MEDIUM -> Icons.Outlined.DragHandle
                            TaskPriority.LOW -> Icons.Outlined.KeyboardDoubleArrowDown
                        },
                        contentDescription = "Priority ${task.priority}",
                        tint = getPrioColor(task.priority),
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = highlightedName,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (task.completed)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (task.completed)
                            TextDecoration.LineThrough
                        else TextDecoration.None,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically

                    // Deadline badge
                ) {
                    BadgeItem(
                        badgeText = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(task.deadline),
                        badgeColor = when {
                            isTaskToday(task) -> MaterialTheme.colorScheme.errorContainer
                            isTaskTomorrow(task) -> MaterialTheme.colorScheme.primaryContainer
                            isTaskExpired(task) -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        badgeIcon = R.drawable.calender_black
                    )

                    // Tag badge
                    BadgeItem(
                        badgeText = task.tag.name,
                        badgeColor = getTaskColor(task.tag),
                        badgeIcon = when (task.tag) {
                            TaskTag.WORK -> R.drawable.work
                            TaskTag.SCHOOL -> R.drawable.school
                            TaskTag.PET -> R.drawable.pet
                            TaskTag.SPORT -> R.drawable.sport
                            TaskTag.HOME -> R.drawable.home_black
                            TaskTag.TRANSPORT -> R.drawable.transport
                            TaskTag.PRIVATE -> R.drawable.lock
                            TaskTag.SOCIAL -> R.drawable.social
                        }
                    )
                }
            }
        }
    }

    // Displays a details dialog for editing or deleting the task
    if (showDetails) {
        TaskDetails(
            task = task,
            onDismiss = { showDetails = false },
            onUpdateTask = onUpdateTask,
            onDeleteTask = { taskId ->
                // For single task deletion
                onDelete(task)
            },
            onDeleteRecurringGroup = { groupId ->
                // For recurring group deletion - also use onDelete since it will be handled at a higher level
                onDelete(task)
            }
        )
    }
}


/**
 * A composable that wraps [TaskItem] in a horizontally swipeable container. When the drag
 * threshold is exceeded, [onDeleteRequest] is triggered, typically prompting a confirmation
 * or deletion action.
 *
 * @param task The [Task] to be displayed and swiped.
 * @param searchText The text used for highlighting within [TaskItem].
 * @param onDelete Callback invoked when the user confirms deletion of the task.
 * @param onCompleteToggle Callback invoked when the user toggles the task's completion state.
 * @param onUpdateTask Callback invoked when the user updates the task details via the [TaskDetails] dialog.
 * @param onDeleteRequest Callback invoked when a swipe action indicates a delete request (before confirmation).
 */
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun SwipeableTaskItem(
    task: Task,
    searchText: String,
    onDelete: (Task) -> Unit,
    onCompleteToggle: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onDeleteRequest: (Task) -> Unit = {}
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val swipeThreshold = 200f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceAtLeast(0f)
                        change.consume()
                    },
                    onDragEnd = {
                        if (offsetX > swipeThreshold) {
                            onDeleteRequest(task)
                        }
                        offsetX = 0f
                    }
                )
            }
    ) {
        TaskItem(
            task = task,
            searchText = searchText,
            onDelete = onDeleteRequest,  // Change this to use onDeleteRequest instead of onDelete
            onCompleteToggle = onCompleteToggle,
            onUpdateTask = onUpdateTask,
            modifier = Modifier.offset(x = offsetX.dp)
        )
    }
}


/**
 * Determines if the [Task]'s deadline falls on the current day.
 *
 * @param task The [Task] to check.
 * @return `true` if [task]'s deadline is today; `false` otherwise.
 */
@Composable
private fun isTaskToday(task: Task): Boolean {
    val todayCalendar = Calendar.getInstance()
    val taskCalendar = Calendar.getInstance()
    taskCalendar.time = task.deadline

    return todayCalendar.get(Calendar.YEAR) == taskCalendar.get(Calendar.YEAR) &&
            todayCalendar.get(Calendar.DAY_OF_YEAR) == taskCalendar.get(Calendar.DAY_OF_YEAR)
}


/**
 * Determines if the [Task]'s deadline has passed, excluding tasks that are exactly today.
 *
 * @param task The [Task] to check.
 * @return `true` if [task]'s deadline is in the past; `false` otherwise.
 */
@Composable
private fun isTaskExpired(task: Task): Boolean {
    val todayCalendar = Calendar.getInstance()
    val taskCalendar = Calendar.getInstance()
    taskCalendar.time = task.deadline

    return taskCalendar.before(todayCalendar) && !isTaskToday(task)
}


/**
 * Determines if the [Task]'s deadline is tomorrow.
 *
 * @param task The [Task] to check.
 * @return `true` if [task]'s deadline is exactly one day after today; `false` otherwise.
 */
@Composable
private fun isTaskTomorrow(task: Task): Boolean {
    val todayCalendar = Calendar.getInstance()
    val taskCalendar = Calendar.getInstance()
    taskCalendar.time = task.deadline

    todayCalendar.add(Calendar.DAY_OF_YEAR, 1)
    return todayCalendar.get(Calendar.YEAR) == taskCalendar.get(Calendar.YEAR) &&
            todayCalendar.get(Calendar.DAY_OF_YEAR) == taskCalendar.get(Calendar.DAY_OF_YEAR)
}