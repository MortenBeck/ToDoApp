package dk.dtu.ToDoList.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dk.dtu.ToDoList.data.Task

@Composable
fun HomeScreen(tasks: List<Task>){
    Column (modifier = Modifier.fillMaxSize()) {
        TopBar()
        TaskListScreen(tasks)
    }
}