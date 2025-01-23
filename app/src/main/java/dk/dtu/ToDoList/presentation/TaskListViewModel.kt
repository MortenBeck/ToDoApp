import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.dtu.ToDoList.domain.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class TaskListViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _filteredTasks = MutableStateFlow<List<Task>>(emptyList())
    val filteredTasks: StateFlow<List<Task>> = _filteredTasks.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val categorizedTasks: StateFlow<Map<String, List<Task>>> = tasks.map { tasks ->
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val tomorrowStart = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        mapOf(
            "Expired" to tasks.filter { it.deadline < todayStart && !it.completed }.sortedBy { it.deadline },
            "Today" to tasks.filter { it.deadline >= todayStart && it.deadline < tomorrowStart }.sortedBy { it.deadline },
            "Future" to tasks.filter { it.deadline >= tomorrowStart }.sortedBy { it.deadline },
            "Completed" to tasks.filter { it.deadline < todayStart && it.completed }.sortedBy { it.deadline }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    init {
        loadTasks()
        setupTaskFiltering()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            taskRepository.getTasksFlow().collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    private fun setupTaskFiltering() {
        combine(_tasks, _searchQuery) { tasks, query ->
            tasks.filter { it.name.contains(query, ignoreCase = true) }
        }.onEach { filteredList ->
            _filteredTasks.value = filteredList
        }.launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(completed = !task.completed)
            taskRepository.updateTask(updatedTask)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task.id)
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskRepository.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    val repository: TaskRepository
        get() = taskRepository
}