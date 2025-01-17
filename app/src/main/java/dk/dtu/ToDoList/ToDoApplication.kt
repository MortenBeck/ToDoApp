package dk.dtu.ToDoList

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dk.dtu.ToDoList.util.UserIdManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Initialize authentication
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            // Sign in anonymously if no user is signed in
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    UserIdManager.signInAnonymously()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}