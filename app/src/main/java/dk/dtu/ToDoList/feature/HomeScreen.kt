package dk.dtu.ToDoList.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dk.dtu.ToDoList.data.Task

@Composable
fun HomeScreen(tasks: List<Task>){
    var searchText by remember { mutableStateOf("") }
    Column (modifier = Modifier.fillMaxSize()) {
        TopBar(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onProfileClick = {}
        )
        TaskListScreen(tasks)
    }
}