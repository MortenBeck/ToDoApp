package dk.dtu.ToDoList.data.mapper.events

import dk.dtu.ToDoList.domain.model.RecurrencePattern
import dk.dtu.ToDoList.domain.model.TaskPriority
import dk.dtu.ToDoList.domain.model.TaskTag
import java.time.LocalDate
import java.time.YearMonth

sealed class AddTaskEvent {
    data class UpdateTaskName(val name: String) : AddTaskEvent()
    data class UpdatePriority(val priority: TaskPriority) : AddTaskEvent()
    data class UpdateTag(val tag: TaskTag) : AddTaskEvent()
    data class UpdateRecurrence(val pattern: RecurrencePattern?) : AddTaskEvent()
    data class UpdateDate(val date: LocalDate) : AddTaskEvent()
    data class UpdateMonth(val month: YearMonth) : AddTaskEvent()
    object ToggleDatePicker : AddTaskEvent()
    object SaveTask : AddTaskEvent()
    object Dismiss : AddTaskEvent()
}