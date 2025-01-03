package dk.dtu.ToDoList.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

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
        // Month Navigation
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

        // Weekday Headers
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

        // Calendar Grid
        val days = mutableListOf<LocalDate>()
        val firstOfMonth = currentMonth.atDay(1)
        val lastOfMonth = currentMonth.atEndOfMonth()

        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var current = firstOfMonth
        while (current.dayOfWeek != firstDayOfWeek) {
            current = current.minusDays(1)
            days.add(0, current)
        }

        current = firstOfMonth
        while (!current.isAfter(lastOfMonth)) {
            days.add(current)
            current = current.plusDays(1)
        }

        current = lastOfMonth.plusDays(1)
        while (days.size < 42) {
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