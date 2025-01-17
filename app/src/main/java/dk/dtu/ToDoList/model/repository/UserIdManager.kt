package dk.dtu.ToDoList.util

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object UserIdManager {
    private val auth = FirebaseAuth.getInstance()

    fun getUserId(context: Context): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User not authenticated")
    }

    // Add authentication methods
    suspend fun signInAnonymously(): String {
        return try {
            val result = auth.signInAnonymously().await()
            result.user?.uid ?: throw IllegalStateException("Failed to get user ID")
        } catch (e: Exception) {
            throw IllegalStateException("Authentication failed", e)
        }
    }

    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    fun signOut() {
        auth.signOut()
    }
}