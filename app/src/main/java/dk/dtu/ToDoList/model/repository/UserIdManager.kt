package dk.dtu.ToDoList.util

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * A utility object for managing user authentication and ID retrieval from Firebase.
 */
object UserIdManager {

    /** A reference to the Firebase Authentication service. */
    private val auth = FirebaseAuth.getInstance()

    /**
     * Retrieves the unique user ID of the currently signed-in user.
     *
     * @param context An Android [Context] (not used internally, but may be required for additional checks or configurations).
     * @return The UID of the authenticated user.
     * @throws IllegalStateException if the user is not authenticated.
     */
    fun getUserId(context: Context): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User not authenticated")
    }

    /**
     * Signs the user in anonymously using Firebase Authentication.
     *
     * @return The UID of the newly signed-in anonymous user.
     * @throws IllegalStateException if the sign-in fails or a UID cannot be retrieved.
     */
    suspend fun signInAnonymously(): String {
        return try {
            val result = auth.signInAnonymously().await()
            result.user?.uid ?: throw IllegalStateException("Failed to get user ID")
        } catch (e: Exception) {
            throw IllegalStateException("Authentication failed", e)
        }
    }

    /**
     * Retrieves the email address associated with the currently signed-in user.
     *
     * @return The email address if available, or `null` if the user is not signed in or doesn't have an email.
     */
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    /**
     * Checks whether a user is currently signed in.
     *
     * @return `true` if a user is signed in, `false` otherwise.
     */
    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Signs out the currently signed-in user (if any) from Firebase.
     */
    fun signOut() {
        auth.signOut()
    }
}
