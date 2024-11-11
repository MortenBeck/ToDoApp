package dk.dtu.ToDoList.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dk.dtu.ToDoList.R

class AddTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
    }
}