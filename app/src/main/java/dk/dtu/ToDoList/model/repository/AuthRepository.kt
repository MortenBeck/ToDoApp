package dk.dtu.ToDoList.repository

interface AuthRepository {
    suspend fun isCurrentUserAnonymous(): Boolean
    suspend fun getCurrentUserEmail(): String
    suspend fun getCurrentUsername(): String
    suspend fun signOut()
    suspend fun deleteCurrentUser()
    suspend fun getCurrentUserId(): String?
}