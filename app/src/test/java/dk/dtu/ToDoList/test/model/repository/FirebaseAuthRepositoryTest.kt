package dk.dtu.ToDoList.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dk.dtu.ToDoList.repository.AuthRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class FirebaseAuthRepositoryTest {

    private val mockAuth: FirebaseAuth = mockk()
    private val mockUser: FirebaseUser = mockk()
    private val authRepository: AuthRepository = FirebaseAuthRepository(auth = mockAuth)

    @Test
    @DisplayName("Should return true if current user is anonymous")
    fun `test isCurrentUserAnonymous returns true`() = runBlocking {
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.isAnonymous } returns true

        val result = authRepository.isCurrentUserAnonymous()

        assertTrue(result)
        verify { mockAuth.currentUser }
        verify { mockUser.isAnonymous }
    }

    @Test
    @DisplayName("Should return false if current user is not anonymous")
    fun `test isCurrentUserAnonymous returns false`() = runBlocking {
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.isAnonymous } returns false

        val result = authRepository.isCurrentUserAnonymous()

        assertFalse(result)
        verify { mockAuth.currentUser }
        verify { mockUser.isAnonymous }
    }

    @Test
    @DisplayName("Should return anonymous email for anonymous user")
    fun `test getCurrentUserEmail returns anonymous email`() = runBlocking {
        coEvery { authRepository.isCurrentUserAnonymous() } returns true

        val result = authRepository.getCurrentUserEmail()

        assertEquals("anonymous@email.com", result)
        coVerify { authRepository.isCurrentUserAnonymous() }
    }

    @Test
    @DisplayName("Should return user email for non-anonymous user")
    fun `test getCurrentUserEmail returns user email`() = runBlocking {
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.email } returns "test@example.com"
        coEvery { authRepository.isCurrentUserAnonymous() } returns false

        val result = authRepository.getCurrentUserEmail()

        assertEquals("test@example.com", result)
        verify { mockAuth.currentUser }
        verify { mockUser.email }
    }

    @Test
    @DisplayName("Should sign out user")
    fun `test signOut`() = runBlocking {
        justRun { mockAuth.signOut() }

        authRepository.signOut()

        verify { mockAuth.signOut() }
    }

    @Test
    @DisplayName("Should delete current user")
    fun `test deleteCurrentUser`() = runBlocking {
        // Mock the current user
        every { mockAuth.currentUser } returns mockUser

        // Mock the delete().await() behavior
        coEvery { mockUser.delete().await() } returns null // Simulates successful completion

        // Call the method
        authRepository.deleteCurrentUser()

        // Verify that delete().await() was called
        verify { mockAuth.currentUser }
        coVerify { mockUser.delete().await() }
    }


    @Test
    @DisplayName("Should return current user ID")
    fun `test getCurrentUserId`() = runBlocking {
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "user123"

        val result = authRepository.getCurrentUserId()

        assertEquals("user123", result)
        verify { mockAuth.currentUser }
        verify { mockUser.uid }
    }

    @Test
    @DisplayName("Should return null if no current user exists")
    fun `test getCurrentUserId returns null`() = runBlocking {
        every { mockAuth.currentUser } returns null

        val result = authRepository.getCurrentUserId()

        assertNull(result)
        verify { mockAuth.currentUser }
    }
}
