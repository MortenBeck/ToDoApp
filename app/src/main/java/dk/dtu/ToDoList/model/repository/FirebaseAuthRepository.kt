package dk.dtu.ToDoList.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
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
}