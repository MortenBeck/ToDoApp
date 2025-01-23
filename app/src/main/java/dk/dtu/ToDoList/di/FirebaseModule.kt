package dk.dtu.ToDoList.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dk.dtu.ToDoList.data.repository.FirebaseTaskRepository
import dk.dtu.ToDoList.domain.repository.TaskRepository


object FirebaseProvider {
    val auth: FirebaseAuth = Firebase.auth
    val firestore: FirebaseFirestore = Firebase.firestore
    val taskRepository: TaskRepository by lazy {
        FirebaseTaskRepository(firestore, auth)
    }
}