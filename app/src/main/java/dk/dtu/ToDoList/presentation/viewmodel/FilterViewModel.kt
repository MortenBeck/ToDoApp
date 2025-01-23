package dk.dtu.ToDoList.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dk.dtu.ToDoList.domain.model.FilterState
import dk.dtu.ToDoList.domain.model.Task
import dk.dtu.ToDoList.domain.model.TaskPriority
import dk.dtu.ToDoList.domain.model.TaskTag
import dk.dtu.ToDoList.domain.usecase.TaskFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.ZoneId


class FilterViewModel : ViewModel() {
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState

    private var tasks: List<Task> = emptyList()
    private val _filteredTasks = MutableStateFlow<List<Task>>(emptyList())
    val filteredTasks: StateFlow<List<Task>> = _filteredTasks

    val hasDateFilter get() = _filterState.value.selectedStartDate != null
    val hasTagFilter get() = _filterState.value.selectedTags.isNotEmpty()
    val hasPriorityFilter get() = _filterState.value.selectedPriorities.isNotEmpty()
    val hideCompleted get() = _filterState.value.hideCompletedTasks

    fun setTasks(newTasks: List<Task>) {
        tasks = newTasks
        applyFilters()
    }

    fun updateDateRange(start: LocalDate?, end: LocalDate?) {
        _filterState.update { it.copy(
            selectedStartDate = start,
            selectedEndDate = end
        )}
        applyFilters()
    }

    fun updateSelectedTags(tag: TaskTag) {
        _filterState.update { state ->
            val tags = state.selectedTags.toMutableSet()
            if (tag in tags) tags.remove(tag) else tags.add(tag)
            state.copy(selectedTags = tags)
        }
        applyFilters()
    }

    fun updateSelectedPriorities(priority: TaskPriority) {
        _filterState.update { state ->
            val priorities = state.selectedPriorities.toMutableSet()
            if (priority in priorities) priorities.remove(priority)
            else priorities.add(priority)
            state.copy(selectedPriorities = priorities)
        }
        applyFilters()
    }

    fun updateHideCompleted(hide: Boolean) {
        _filterState.update { it.copy(hideCompletedTasks = hide) }
        applyFilters()
    }

    private fun applyFilters() {
        _filteredTasks.value = TaskFilter().filterTasks(tasks, _filterState.value)
    }
}

class TaskFilter {
    fun filterTasks(tasks: List<Task>, filterState: FilterState): List<Task> {
        return tasks.filter { task ->
            val dateMatches = filterState.selectedStartDate == null ||
                    (task.deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() >= filterState.selectedStartDate!! &&
                            task.deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() <= filterState.selectedEndDate!!)

            val tagMatches = filterState.selectedTags.isEmpty() || task.tag in filterState.selectedTags
            val priorityMatches = filterState.selectedPriorities.isEmpty() || task.priority in filterState.selectedPriorities
            val completionMatches = !filterState.hideCompletedTasks || !task.completed

            dateMatches && tagMatches && priorityMatches && completionMatches
        }
    }
}