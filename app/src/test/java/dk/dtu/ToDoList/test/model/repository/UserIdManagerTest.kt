package dk.dtu.ToDoList.util

import android.content.Context
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserIdManagerTest {

    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockUser: FirebaseUser

    @BeforeAll
    fun setupMocks() {
        mockkStatic(FirebaseAuth::class)
        mockAuth = mockk()
        mockUser = mockk()

        every { FirebaseAuth.getInstance() } returns mockAuth
    }

    @Test
    fun `getUserId should return user ID if user is authenticated`() {
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "testUserId"

        val context = mockk<Context>()

        val userId = UserIdManager.getUserId(context)

        assertEquals("testUserId", userId)
        verify { mockAuth.currentUser }
    }

    @Test
    fun `getUserId should throw exception if user is not authenticated`() {
        every { mockAuth.currentUser } returns null

        val context = mockk<Context>()

        val exception = assertThrows<IllegalStateException> {
            UserIdManager.getUserId(context)
        }

        assertEquals("User not authenticated", exception.message)
    }

    @Test
    fun `signInAnonymously should return user ID when sign-in is successful`() = runBlocking {
        // Mock the AuthResult and FirebaseUser
        val mockResult = mockk<AuthResult>()
        every { mockAuth.signInAnonymously() } returns mockk {
            coEvery { await() } returns mockResult
        }
        every { mockResult.user } returns mockUser
        every { mockUser.uid } returns "testUserId"

        // Call the method under test
        val userId = UserIdManager.signInAnonymously()

        // Verify the behavior and assertions
        assertEquals("testUserId", userId)
        coVerify { mockAuth.signInAnonymously() }
        coVerify { mockAuth.signInAnonymously().await() }
    }



    @Test
    fun `signInAnonymously should throw exception if sign-in fails`() = runBlocking {
        // Mock the sign-in method to throw an exception
        every { mockAuth.signInAnonymously() } returns mockk {
            coEvery { await() } throws Exception("Sign-in error")
        }

        // Assert that the exception is thrown
        val exception = assertThrows<IllegalStateException> {
            UserIdManager.signInAnonymously()
        }

        // Verify the exception message
        assertEquals("Authentication failed", exception.message)
    }



    @Test
    fun `getCurrentUserEmail should return email if user is authenticated`() {
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.email } returns "test@example.com"

        val email = UserIdManager.getCurrentUserEmail()

        assertEquals("test@example.com", email)
        verify { mockAuth.currentUser }
    }

    @Test
    fun `getCurrentUserEmail should return null if user is not authenticated`() {
        every { mockAuth.currentUser } returns null

        val email = UserIdManager.getCurrentUserEmail()

        assertNull(email)
    }

    @Test
    fun `isUserSignedIn should return true if user is authenticated`() {
        every { mockAuth.currentUser } returns mockUser

        val isSignedIn = UserIdManager.isUserSignedIn()

        assertTrue(isSignedIn)
        verify { mockAuth.currentUser }
    }

    @Test
    fun `isUserSignedIn should return false if user is not authenticated`() {
        every { mockAuth.currentUser } returns null

        val isSignedIn = UserIdManager.isUserSignedIn()

        assertFalse(isSignedIn)
    }

    @Test
    fun `signOut should call FirebaseAuth signOut`() {
        every { mockAuth.signOut() } just Runs

        UserIdManager.signOut()

        verify { mockAuth.signOut() }
    }

    @AfterAll
    fun teardown() {
        unmockkAll()
    }
}
