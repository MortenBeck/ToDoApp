import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.tasks.Task
import dk.dtu.ToDoList.util.UserIdManager
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import kotlinx.coroutines.tasks.await
import org.junit.jupiter.api.assertThrows

class UserIdManagerTest {

    private val mockContext: Context = mockk(relaxed = true)
    private val mockFirebaseAuth: FirebaseAuth = mockk()
    private val mockFirebaseUser: FirebaseUser = mockk()

    @BeforeEach
    fun setUp() {
        // Set up MockK to mock FirebaseAuth
        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockFirebaseAuth
        every { mockFirebaseAuth.currentUser} returns mockFirebaseUser
        every { mockFirebaseUser.email} returns "user@example.com"
        every { mockFirebaseAuth.signOut() } returns Unit
        every { mockFirebaseUser.uid} returns "mockUserId"
    }

    @Test
    fun `test signInAnonymously success`() = runBlocking {
        // Given: Mock a successful anonymous sign-in result
        val mockAuthResult = mockk<com.google.firebase.auth.AuthResult>()
        val mockUser = mockk<FirebaseUser>()

        // Mock the user and result
        every { mockAuthResult.user } returns mockUser
        every { mockUser.uid } returns "testUid"  // Return a direct String (not a function)

        // Mock Task<AuthResult> to return mockAuthResult
        val mockTask = mockk<Task<com.google.firebase.auth.AuthResult>>()
        every { mockTask.isSuccessful } returns true
        every { mockTask.result } returns mockAuthResult
        coEvery { mockTask.await() } returns mockAuthResult

        // Mock the FirebaseAuth's signInAnonymously method to return the mock Task
        coEvery { mockFirebaseAuth.signInAnonymously() } returns mockTask

        // When: Call the `signInAnonymously` method
        val userId = UserIdManager.signInAnonymously()

        // Then: Assert that the user ID is as expected
        assertEquals("testUid", userId)
    }


    @Test
    fun `test signInAnonymously failure`() = runBlocking {
        // Given: Mock a failure scenario for sign-in
        coEvery { mockFirebaseAuth.signInAnonymously() } throws Exception("Auth failed")

        // When & Then: Expect IllegalStateException with message "Authentication failed"
        val exception = assertThrows<IllegalStateException> {
            UserIdManager.signInAnonymously()
        }
        assertEquals("Authentication failed", exception.message)
    }


    @Test
    fun `test getUserId when user is authenticated`() {
        // Given: mock FirebaseUser with a valid UID
        every { mockFirebaseAuth.currentUser } returns mockFirebaseUser
        every { mockFirebaseUser.uid } returns "testUid"  // Ensure it returns a String, not a function

        // When
        val userId = UserIdManager.getUserId(mockContext)

        // Then
        assertEquals("testUid", userId)
    }


    @Test
    fun `test isUserSignedIn when user is authenticated`() {
        // Given: mock FirebaseUser to indicate user is signed in
        every { mockFirebaseAuth.currentUser } returns mockFirebaseUser

        // When
        val isSignedIn = UserIdManager.isUserSignedIn()

        // Then
        assertTrue(isSignedIn)
    }


    @Test
    fun `test signOut`() {
        // Given: No specific setup needed for signOut() method
        UserIdManager.signOut()

        // Then: Verify that FirebaseAuth's signOut() was called
        verify { mockFirebaseAuth.signOut() }
    }
}
