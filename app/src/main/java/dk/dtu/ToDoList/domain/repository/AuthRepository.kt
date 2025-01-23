package dk.dtu.ToDoList.domain.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    suspend fun isCurrentUserAnonymous(): Boolean
    suspend fun getCurrentUserEmail(): String?
    suspend fun getCurrentUsername(): String
    suspend fun signOut()
    suspend fun deleteCurrentUser()
    suspend fun getCurrentUserId(): String?
    suspend fun signInAnonymously(): Result<String>
    fun isUserSignedIn(): Boolean
}

class AuthRepositoryImpl(private val auth: FirebaseAuth) : AuthRepository {
    override suspend fun isCurrentUserAnonymous(): Boolean {
        return auth.currentUser?.isAnonymous ?: false
    }

    override suspend fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    override suspend fun getCurrentUsername(): String {
        return auth.currentUser?.displayName ?: "Anonymous User"
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun deleteCurrentUser() {
        auth.currentUser?.delete()?.await()
    }

    override suspend fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun signInAnonymously(): Result<String> {
        return try {
            val result = auth.signInAnonymously().await()
            Result.success(result.user?.uid ?: throw Exception("User ID not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }
}