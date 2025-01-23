package dk.dtu.ToDoList.domain.components.miscellaneous

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dk.dtu.ToDoList.domain.model.Task
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale



/**
 * A composable function that renders a monthly calendar view, highlighting days with tasks
 * and allowing selection of a specific date. It also provides month navigation (previous/next).
 *
 * @param selectedDate The currently selected [LocalDate].
 * @param currentMonth The [YearMonth] currently being displayed.
 * @param onDateSelected A callback invoked when the user taps on a specific date.
 * @param onMonthChanged A callback invoked when the user navigates to a different month.
 * @param tasks A list of [Task] objects that determine which dates have associated tasks.
 */
@Composable
fun Calendar(
    selectedDate: LocalDate,
    currentMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    tasks: List<Task>
) {
    // Remember the set of dates (LocalDate) on which there are tasks
    val taskDates = remember(tasks) {
        tasks.map { task ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                task.deadline.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        }.toSet()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Your Calendar",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Month Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMonthChanged(currentMonth.minusMonths(1)) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = { onMonthChanged(currentMonth.plusMonths(1)) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next month",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Day-of-Week Headers
            Row(modifier = Modifier.fillMaxWidth()) {
                val daysOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek.let { firstDay ->
                    (0..6).map { firstDay.plus(it.toLong()) }
                }

                daysOfWeek.forEach { dayOfWeek ->
                    Text(
                        text = dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calculate the dates to display in the grid
            val days = calculateDaysInMonth(currentMonth)

            // Days in a 7-column grid
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
                        hasTask = taskDates.contains(date),
                        onDateSelected = onDateSelected
                    )
                }
            }
        }
    }
}


/**
 * A composable function representing a single day cell in the calendar grid.
 *
 * It displays the day number, indicates if the day is selected or if it has an associated task,
 * and detects user clicks for date selection.
 *
 * @param date The [LocalDate] represented by this cell.
 * @param isSelected Whether the date is currently selected.
 * @param isCurrentMonth Whether the date belongs to the currently displayed month.
 * @param hasTask Whether this date has an associated task.
 * @param onDateSelected A callback invoked when the user taps on this day cell.
 */
@Composable
fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    hasTask: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val isToday = date == LocalDate.now()

    // Determine background and text colors based on state
    val cellColor = when {
        isSelected -> MaterialTheme.colorScheme.outline
        isToday -> MaterialTheme.colorScheme.outlineVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        !isCurrentMonth -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(cellColor)
            .clickable { onDateSelected(date) }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )

            // Small indicator circle if there's a task on this date
            if (hasTask) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                )
            }
        }
    }
}


/**
 * Calculates a list of [LocalDate] values to display in a 6-row calendar grid for the given [YearMonth].
 *
 * This includes:
 * - The last few days of the previous month (if needed) so the first row starts on the correct day of the week.
 * - All days of the current month.
 * - The first few days of the next month to fill the remaining cells (up to 42 total).
 *
 * @param currentMonth The [YearMonth] for which days should be generated.
 * @return A list of [LocalDate] values containing up to 42 days for display in a 6-row grid.
 */
private fun calculateDaysInMonth(currentMonth: YearMonth): List<LocalDate> {
    val days = mutableListOf<LocalDate>()
    val firstOfMonth = currentMonth.atDay(1)
    val lastOfMonth = currentMonth.atEndOfMonth()

    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var current = firstOfMonth

    // Add days from the previous month to align the first row
    while (current.dayOfWeek != firstDayOfWeek) {
        current = current.minusDays(1)
        days.add(0, current)
    }

    // Add all days of the current month
    current = firstOfMonth
    while (!current.isAfter(lastOfMonth)) {
        days.add(current)
        current = current.plusDays(1)
    }

    // Fill in the remaining days until we reach 42 cells (6 rows)
    current = lastOfMonth.plusDays(1)
    while (days.size < 42) {
        days.add(current)
        current = current.plusDays(1)
    }

    return days
}
