package dk.dtu.ToDoList.model.data.state

data class TaskStats(
    val todayTasksCount: Int = 0,
    val completedTodayCount: Int = 0,
    val upcomingTasksCount: Int = 0
) {
    val completionRate: Float
        get() = if (todayTasksCount > 0) {
            completedTodayCount.toFloat() / todayTasksCount
        } else 0f
}