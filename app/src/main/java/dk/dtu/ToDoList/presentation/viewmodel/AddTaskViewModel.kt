import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.dtu.ToDoList.domain.model.AddTaskState
import dk.dtu.ToDoList.domain.model.RecurrencePattern
import dk.dtu.ToDoList.domain.model.TaskPriority
import dk.dtu.ToDoList.domain.model.TaskTag
import dk.dtu.ToDoList.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date

class AddTaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AddTaskState())
    val state: StateFlow<AddTaskState> = _state

    fun onEvent(event: AddTaskEvent) {
        when (event) {
            is AddTaskEvent.UpdateTaskName -> _state.update { it.copy(taskName = event.name) }
            is AddTaskEvent.UpdatePriority -> _state.update { it.copy(priorityLevel = event.priority) }
            is AddTaskEvent.UpdateTag -> _state.update { it.copy(selectedTag = event.tag) }
            is AddTaskEvent.UpdateRecurrence -> _state.update { it.copy(selectedRecurrence = event.pattern) }
            is AddTaskEvent.UpdateDate -> _state.update { it.copy(selectedDate = event.date, showDatePicker = false) }
            is AddTaskEvent.UpdateMonth -> _state.update { it.copy(currentMonth = event.month) }
            AddTaskEvent.ToggleDatePicker -> _state.update { it.copy(showDatePicker = !it.showDatePicker) }
            AddTaskEvent.SaveTask -> saveTask()
            AddTaskEvent.Dismiss -> _state.update { AddTaskState() }
        }
    }

    private fun saveTask() {
        val currentState = _state.value
        if (currentState.taskName.isBlank()) return

        val task = Task(
            name = currentState.taskName,
            priority = currentState.priorityLevel,
            deadline = Date.from(currentState.selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
            tag = currentState.selectedTag,
            completed = false,
            recurrence = currentState.selectedRecurrence
        )

        viewModelScope.launch {
            if (currentState.selectedRecurrence != null) {
                taskRepository.addTaskWithRecurrence(task)
            } else {
                taskRepository.addTask(task)
            }
        }
    }
}

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