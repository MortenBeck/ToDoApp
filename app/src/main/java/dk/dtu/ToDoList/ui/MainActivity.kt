package dk.dtu.ToDoList

import dk.dtu.ToDoList.ui.AddTaskActivity
import android.os.Bundle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dk.dtu.ToDoList.ui.theme.ToDoListTheme
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dk.dtu.ToDoList.ui.AddTaskDialogFragment
import dk.dtu.ToDoList.ui.adapters.TaskAdapter
import dk.dtu.ToDoList.ui.models.Task

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: MutableList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.task_list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize task list (this could be from a database or temporary data)
        taskList = mutableListOf()

        // Add sample tasks (temporary)
        taskList.add(Task("Buy groceries", "description here", false)) // example if description is String
        taskList.add(Task("Finish homework", "description here", false)) // example if description is String
        taskList.add(Task("Walk the dog", "description here", false)) // example if description is String


        // Initialize adapter with task list and set it to the RecyclerView
        taskAdapter = TaskAdapter(taskList)
        recyclerView.adapter = taskAdapter

        // Handle Add Task button with dialog fragment
        val addTaskButton: Button = findViewById(R.id.add_task_button)
        addTaskButton.setOnClickListener {
            // Show the AddTaskDialogFragment as a pop-up dialog
            val addTaskDialog = AddTaskDialogFragment()
            addTaskDialog.show(supportFragmentManager, "AddTaskDialogFragment")
        }
    }
}