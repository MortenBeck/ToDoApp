package dk.dtu.ToDoList.util

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * A utility object for managing user authentication and user ID retrieval using Firebase Authentication.
 *
 * This utility provides methods to:
 * - Retrieve the user ID of the currently authenticated user.
 * - Sign in users anonymously.
 * - Retrieve the email address of the authenticated user.
 * - Check authentication status.
 * - Sign out the currently authenticated user.
 */
object UserIdManager {

    /** A reference to the Firebase Authentication service. */
    private val auth = FirebaseAuth.getInstance()

    /**
     * Retrieves the unique user ID of the currently signed-in user.
     *
     * @param context An Android [Context] (currently unused, reserved for future enhancements).
     * @return The UID of the authenticated user as a [String].
     * @throws IllegalStateException If no user is currently authenticated.
     */
    fun getUserId(context: Context): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User not authenticated")
    }

    /**
     * Signs the user in anonymously using Firebase Authentication.
     *
     * Anonymous sign-in allows users to interact with the app without creating an account.
     *
     * @return The UID of the newly signed-in anonymous user as a [String].
     * @throws IllegalStateException If the sign-in fails or the UID cannot be retrieved.
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
     * This method is useful for authenticated users who have an email address linked to their account.
     *
     * @return The email address of the user as a [String], or `null` if the user is not signed in or does not have an email.
     */
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    /**
     * Checks whether a user is currently signed in.
     *
     * @return `true` if a user is signed in; `false` otherwise.
     */
    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Signs out the currently signed-in user (if any) from Firebase.
     *
     * This method clears the current authentication session, requiring the user to sign in again
     * if they wish to continue using features that require authentication.
     */
    fun signOut() {
        auth.signOut()
    }
}
