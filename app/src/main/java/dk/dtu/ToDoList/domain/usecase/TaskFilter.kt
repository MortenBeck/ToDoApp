package dk.dtu.ToDoList.domain.usecase

import dk.dtu.ToDoList.domain.model.Task
import dk.dtu.ToDoList.domain.model.FilterState
import java.time.ZoneId
import javax.inject.Inject

class TaskFilter @Inject constructor() {
    fun filterTasks(tasks: List<Task>, filterState: FilterState): List<Task> {
        return tasks.filter { task ->
            val dateMatches = applyDateFilter(task, filterState.dateRange)
            val tagMatches = filterState.selectedTags.isEmpty() || task.tag in filterState.selectedTags
            val priorityMatches = filterState.selectedPriorities.isEmpty() ||
                    task.priority in filterState.selectedPriorities
            val completionMatches = !filterState.hideCompletedTasks || !task.completed

            dateMatches && tagMatches && priorityMatches && completionMatches
        }
    }

    private fun applyDateFilter(task: Task, dateRange: Pair<Date?, Date?>): Boolean {
        if (dateRange.first == null || dateRange.second == null) return true

        val taskDate = task.deadline.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val startDate = dateRange.first!!.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val endDate = dateRange.second!!.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return !taskDate.isBefore(startDate) && !taskDate.isAfter(endDate)
    }
}