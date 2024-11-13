package dk.dtu.ToDoList.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TaskTag
import dk.dtu.ToDoList.data.TasksRepository.simpleDateFormat

@Composable
fun HomeScreen(tasks: List<Task>){
    Column (modifier = Modifier.fillMaxSize()) {
        TopBar()
        TaskListScreen(tasks)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen(
        tasks = listOf(
            Task(
                name = "Homework - UX",
                deadline = simpleDateFormat.parse("11-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.SCHOOL,
                completed = false
            ),
            Task(
                name = "Fix project at work",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.WORK,
                completed = true
            )
        )
    )
}