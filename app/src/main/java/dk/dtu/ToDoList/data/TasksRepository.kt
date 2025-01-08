package dk.dtu.ToDoList.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentReference

object TasksRepository {

    val db = FirebaseFirestore.getInstance()

    // A helper method to convert a Firestore document into a Task object
    private fun documentToTask(document: DocumentSnapshot): Task? {
        return document.toObject(Task::class.java)
    }

    // Add a new task to Firestore
    fun addTask(task: Task, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("tasks")
            .add(task)
            .addOnSuccessListener { documentReference ->
                onSuccess(documentReference.id) // return the task ID
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Get all tasks for a user (you can add filtering based on userId or other criteria)
    fun getTasks(userId: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("tasks")
            .whereEqualTo("userId", userId) // filter by user ID (assuming each task is tied to a user)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.documents.mapNotNull { document -> documentToTask(document) }
                onSuccess(tasks)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Update an existing task in Firestore
    fun updateTask(taskId: String, updatedTask: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("tasks")
            .document(taskId)
            .set(updatedTask)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Delete a task from Firestore
    fun deleteTask(taskId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("tasks")
            .document(taskId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Fetch today's tasks
    fun getTodayTasks(userId: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
        val today = System.currentTimeMillis()
        db.collection("tasks")
            .whereEqualTo("userId", userId)
            .whereLessThan("deadline", today)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.documents.mapNotNull { document -> documentToTask(document) }
                onSuccess(tasks)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Fetch future tasks (tasks with a deadline in the future)
    fun getFutureTasks(userId: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
        val today = System.currentTimeMillis()
        db.collection("tasks")
            .whereEqualTo("userId", userId)
            .whereGreaterThan("deadline", today)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.documents.mapNotNull { document -> documentToTask(document) }
                onSuccess(tasks)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Fetch favorite tasks
    fun getFavoriteTasks(userId: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("tasks")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isFavorite", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.documents.mapNotNull { document -> documentToTask(document) }
                onSuccess(tasks)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
