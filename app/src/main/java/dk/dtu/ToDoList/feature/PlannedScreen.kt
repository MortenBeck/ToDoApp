package dk.dtu.ToDoList.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TaskTag
import dk.dtu.ToDoList.data.TasksRepository.simpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun PlannedScreen() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Planned",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Calendar
        Calendar(
            selectedDate = selectedDate,
            currentMonth = currentMonth,
            onDateSelected = { selectedDate = it },
            onMonthChanged = { currentMonth = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tasks for selected date
        TasksForDate(selectedDate)
    }
}

@Composable
fun Calendar(
    selectedDate: LocalDate,
    currentMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChanged(currentMonth.minusMonths(1)) }) {
                Text("←")
            }

            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(onClick = { onMonthChanged(currentMonth.plusMonths(1)) }) {
                Text("→")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weekday headers
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek.let { firstDay ->
                (0..6).map { firstDay.plus(it.toLong()) }
            }

            daysOfWeek.forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        val days = mutableListOf<LocalDate>()
        val firstOfMonth = currentMonth.atDay(1)
        val lastOfMonth = currentMonth.atEndOfMonth()

        // Add padding days from previous month
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var current = firstOfMonth
        while (current.dayOfWeek != firstDayOfWeek) {
            current = current.minusDays(1)
            days.add(0, current)
        }

        // Add all days of current month
        current = firstOfMonth
        while (!current.isAfter(lastOfMonth)) {
            days.add(current)
            current = current.plusDays(1)
        }

        // Add padding days from next month
        current = lastOfMonth.plusDays(1)
        while (days.size < 42) { // 6 rows of 7 days
            days.add(current)
            current = current.plusDays(1)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(days) { date ->
                DayCell(
                    date = date,
                    isSelected = date == selectedDate,
                    isCurrentMonth = date.month == currentMonth.month,
                    onDateSelected = onDateSelected
                )
            }
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val isToday = date == LocalDate.now()

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }
            )
            .clickable { onDateSelected(date) }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                !isCurrentMonth -> MaterialTheme.colorScheme.outline
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
fun TasksForDate(date: LocalDate) {
    // Define tasks for each date (adjust this to your actual data source)
    val allTasks = remember {
        listOf(
            Task(
                name = "Homework - UX",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.SCHOOL,
                completed = false
            ),
            Task(
                name = "Fix project at work",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.WORK,
                completed = true
            ),
            Task(
                name = "Walk the dog",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.PET,
                completed = false
            ),
            // Add more tasks as needed
        )
    }

    // Filter tasks for the selected date
    val tasksForDate = remember(date) {
        allTasks.filter { task ->
            // Compare the task deadline with the selected date (only include tasks with matching dates)
            task.deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() == date
        }
    }

    Column {
        Text(
            text = "Tasks for ${date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Display tasks for the selected date
        if (tasksForDate.isNotEmpty()) {
            TaskList(
                Tasks = tasksForDate,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = "No tasks for this day.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}