package dk.dtu.ToDoList.repository

/**
 * A repository interface for handling authentication-related operations.
 *
 * This interface abstracts common authentication tasks such as determining user state,
 * retrieving user information, and managing user accounts.
 */
interface AuthRepository {

    /**
     * Checks if the current user is authenticated anonymously.
     *
     * @return `true` if the current user is anonymous, `false` otherwise.
     */
    suspend fun isCurrentUserAnonymous(): Boolean

    /**
     * Retrieves the email address of the currently authenticated user.
     *
     * @return A [String] representing the user's email.
     * @throws An exception if the email is unavailable or the user is not authenticated.
     */
    suspend fun getCurrentUserEmail(): String

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * @return A [String] representing the user's username.
     * @throws An exception if the username is unavailable or the user is not authenticated.
     */
    suspend fun getCurrentUsername(): String

    /**
     * Signs out the currently authenticated user.
     *
     * This method will invalidate the current session and clear any cached user data.
     */
    suspend fun signOut()

    /**
     * Deletes the account of the currently authenticated user.
     *
     * @throws An exception if the operation fails or the user cannot be deleted.
     */
    suspend fun deleteCurrentUser()

    /**
     * Retrieves the unique identifier of the currently authenticated user.
     *
     * @return A [String] representing the user's ID, or `null` if no user is authenticated.
     */
    suspend fun getCurrentUserId(): String?
}
