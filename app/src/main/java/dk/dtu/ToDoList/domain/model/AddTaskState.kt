package dk.dtu.ToDoList.domain.model

import java.time.LocalDate
import java.time.YearMonth

data class AddTaskState(
    val taskName: String = "",
    val priorityLevel: TaskPriority = TaskPriority.LOW,
    val selectedTag: TaskTag = TaskTag.WORK,
    val selectedRecurrence: RecurrencePattern? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: YearMonth = YearMonth.now(),
    val showDatePicker: Boolean = false
)
