package dk.dtu.ToDoList.data.repository

import com.google.firebase.auth.FirebaseAuth
import dk.dtu.ToDoList.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {
    override suspend fun isCurrentUserAnonymous(): Boolean =
        auth.currentUser?.isAnonymous ?: true

    override suspend fun getCurrentUserEmail(): String =
        if (isCurrentUserAnonymous()) "anonymous@email.com"
        else auth.currentUser?.email ?: "anonymous@email.com"

    override suspend fun getCurrentUsername(): String =
        if (isCurrentUserAnonymous()) "Anonymous"
        else auth.currentUser?.email?.substringBefore('@') ?: "Anonymous"

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun deleteCurrentUser() {
        auth.currentUser?.delete()?.await()
    }

    override suspend fun getCurrentUserId(): String? =
        auth.currentUser?.uid

    override suspend fun signInAnonymously(): Result<String> = runCatching {
        val result = auth.signInAnonymously().await()
        result.user?.uid ?: throw IllegalStateException("Failed to get user ID")
    }

    override fun isUserSignedIn(): Boolean =
        auth.currentUser != null
}