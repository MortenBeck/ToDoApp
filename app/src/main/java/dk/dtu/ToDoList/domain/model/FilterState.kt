package dk.dtu.ToDoList.domain.model

import dk.dtu.ToDoList.domain.model.TaskPriority
import dk.dtu.ToDoList.domain.model.TaskTag
import java.time.LocalDate
import java.time.YearMonth
import java.util.Date

data class FilterState(
    val dateRange: Pair<Date?, Date?> = null to null,
    val selectedTags: Set<TaskTag> = emptySet(),
    val selectedPriorities: Set<TaskPriority> = emptySet(),
    val hideCompletedTasks: Boolean = false,
    val selectedStartDate: LocalDate? = null,
    val selectedEndDate: LocalDate? = null
)

data class CalendarState(
    val isSelectingStartDate: Boolean = true,
    val showCalendarPicker: Boolean = false,
    val currentMonth: YearMonth = YearMonth.now()
)