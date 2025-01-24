package dk.dtu.ToDoList.data.repository

import com.google.firebase.auth.FirebaseAuth
import dk.dtu.ToDoList.repository.AuthRepository
import kotlinx.coroutines.tasks.await

/**
 * A concrete implementation of the [AuthRepository] interface that uses Firebase Authentication.
 *
 * This class provides authentication-related functionalities by interacting with
 * the [FirebaseAuth] API to manage user authentication, retrieval of user details,
 * and account operations.
 *
 * @property auth An instance of [FirebaseAuth], defaulting to the singleton instance.
 */
class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    /**
     * Checks if the current user is authenticated anonymously.
     *
     * @return `true` if the current user is anonymous, or `true` if no user is authenticated.
     */
    override suspend fun isCurrentUserAnonymous(): Boolean =
        auth.currentUser?.isAnonymous ?: true

    /**
     * Retrieves the email address of the currently authenticated user.
     *
     * If the user is anonymous or no user is authenticated, a placeholder email
     * `"anonymous@email.com"` is returned.
     *
     * @return A [String] representing the user's email.
     */
    override suspend fun getCurrentUserEmail(): String =
        if (isCurrentUserAnonymous()) "anonymous@email.com"
        else auth.currentUser?.email ?: "anonymous@email.com"

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * If the user is anonymous or no user is authenticated, `"Anonymous"` is returned.
     * For authenticated users with an email, the username is derived from the email's prefix.
     *
     * @return A [String] representing the user's username.
     */
    override suspend fun getCurrentUsername(): String =
        if (isCurrentUserAnonymous()) "Anonymous"
        else auth.currentUser?.email?.substringBefore('@') ?: "Anonymous"

    /**
     * Signs out the currently authenticated user.
     *
     * This operation clears the current session and invalidates any user-related data.
     */
    override suspend fun signOut() {
        auth.signOut()
    }

    /**
     * Deletes the account of the currently authenticated user.
     *
     * This operation is asynchronous and waits for Firebase to complete the deletion.
     * If no user is authenticated, this method does nothing.
     */
    override suspend fun deleteCurrentUser() {
        auth.currentUser?.delete()?.await()
    }

    /**
     * Retrieves the unique identifier of the currently authenticated user.
     *
     * @return A [String] representing the user's UID, or `null` if no user is authenticated.
     */
    override suspend fun getCurrentUserId(): String? =
        auth.currentUser?.uid
}
