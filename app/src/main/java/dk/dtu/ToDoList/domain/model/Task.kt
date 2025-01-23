package dk.dtu.ToDoList.domain.model

import java.util.Date


/**
 * Represents the priority of a task.
 *
 * Possible values:
 * - [HIGH]
 * - [MEDIUM]
 * - [LOW]
 */
enum class TaskPriority {
    HIGH, MEDIUM, LOW
}


/**
 * Represents the tag or category associated with a task.
 *
 * Possible values include:
 * - [WORK]
 * - [SCHOOL]
 * - [SPORT]
 * - [TRANSPORT]
 * - [PET]
 * - [HOME]
 * - [PRIVATE]
 * - [SOCIAL]
 */
enum class TaskTag {
    WORK, SCHOOL, SPORT, TRANSPORT, PET, HOME, PRIVATE, SOCIAL
}


/**
 * Represents a task with various attributes like name, description,
 * deadline, priority, tag, and more.
 *
 * @property id A unique identifier for the task.
 * @property name The title or brief name of the task.
 * @property description A detailed explanation of the task.
 * @property deadline A [Date] object representing when the task is due.
 * @property priority The [TaskPriority] level (HIGH, MEDIUM, LOW).
 * @property tag The [TaskTag] category (e.g., WORK, SCHOOL, etc.).
 * @property completed Indicates whether the task is completed.
 * @property createdAt The [Date] the task was initially created.
 * @property modifiedAt The [Date] the task was last updated.
 * @property subtasks A list of [SubTask] objects associated with this task.
 * @property reminderTime The [Date] at which a reminder should be triggered (if any).
 * @property recurrence The [RecurrencePattern] specifying if the task repeats (daily, weekly, etc.).
 * @property userId The identifier of the user to whom this task belongs.
 * @property recurringGroupId An ID shared by all tasks in the same recurring series (if any).
 * @property isRecurringParent Indicates if this task is the “parent” in a recurring group.
 * @property recurringIndex The position of this task in its recurring sequence (if any).
 */
data class Task(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val deadline: Date = Date(),
    val priority: TaskPriority = TaskPriority.LOW,
    val tag: TaskTag = TaskTag.WORK,
    val completed: Boolean = false,
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val subtasks: List<SubTask> = emptyList(),
    val reminderTime: Date? = null,
    val recurrence: RecurrencePattern? = null,
    val userId: String = "",
    val recurringGroupId: String? = null,
    val isRecurringParent: Boolean = false,
    val recurringIndex: Int = 0
) {
    /**
     * Required empty constructor for Firestore.
     * Initializes only the mandatory fields and sets defaults for optional ones.
     */
    constructor() : this(
        name = "",
        deadline = Date()
    )

    /**
     * Provides a more explicit name for the deadline, if needed.
     */
    val dueDate: Date get() = deadline
}


/**
 * Represents a subtask that is part of a parent [Task].
 *
 * @property name A brief title or description of the subtask.
 * @property completed Indicates whether this subtask is completed.
 */
data class SubTask(
    val name: String = "",
    val completed: Boolean = false
) {
    /**
     * Required empty constructor for Firestore.
     * Initializes the [name] to an empty string by default.
     */
    constructor() : this("")
}


/**
 * Indicates how often a task should recur.
 *
 * Possible values:
 * - [DAILY]
 * - [WEEKLY]
 * - [MONTHLY]
 * - [YEARLY]
 */
enum class RecurrencePattern {
    DAILY, WEEKLY, MONTHLY, YEARLY
}