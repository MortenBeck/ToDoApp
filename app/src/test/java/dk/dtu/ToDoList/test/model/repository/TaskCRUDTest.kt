package dk.dtu.ToDoList.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import dk.dtu.ToDoList.domain.model.Task
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class TaskCRUDTest {
    private lateinit var taskCRUD: TaskCRUD
    private lateinit var mockContext: Context
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockTasksCollection: CollectionReference
    private val mockUserId = "test_user_id"

    @BeforeEach
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockAuth = mockk(relaxed = true)
        mockFirestore = mockk(relaxed = true)
        mockTasksCollection = mockk(relaxed = true)

        // Mock FirebaseAuth and Firestore
        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { FirebaseFirestore.getInstance() } returns mockFirestore
        every { mockAuth.currentUser?.uid } returns mockUserId
        every { mockFirestore.collection("tasks") } returns mockTasksCollection

        taskCRUD = TaskCRUD(mockContext)
    }

    @Test
    fun `getTasksFlow should return tasks when user is authenticated`() = runBlocking {
        // Arrange
        val mockSnapshot = mockk<QuerySnapshot>()
        val mockDocument = mockk<DocumentSnapshot>()
        val task = Task(id = "task1", name = "Test Task", userId = mockUserId)

        every { mockTasksCollection.whereEqualTo("userId", mockUserId) } returns mockTasksCollection
        every { mockTasksCollection.orderBy("deadline", Query.Direction.ASCENDING) } returns mockTasksCollection
        every { mockTasksCollection.addSnapshotListener(capture(slot<com.google.firebase.firestore.EventListener<QuerySnapshot>>()))} answers {
            firstArg<com.google.firebase.firestore.EventListener<QuerySnapshot>>().onEvent(mockSnapshot, null)
            mockk()
        }
        every { mockSnapshot.documents } returns listOf(mockDocument)
        every { mockDocument.toObject(Task::class.java) } returns task

        // Act
        val tasks = taskCRUD.getTasksFlow().first()

        // Assert
        assertEquals(1, tasks.size)
        assertEquals(task, tasks[0])
        verify { mockTasksCollection.whereEqualTo("userId", mockUserId) }
        verify { mockTasksCollection.orderBy("deadline", Query.Direction.ASCENDING) }
    }

    @Test
    fun `getTasksFlow should return empty list when user is not authenticated`() = runBlocking {
        // Arrange
        every { mockAuth.currentUser?.uid } returns null

        // Act
        val tasks = taskCRUD.getTasksFlow().first()

        // Assert
        assertTrue(tasks.isEmpty())
    }

    @Test
    fun `addTask should return true on successful task addition`() = runBlocking {
        // Arrange
        val task = Task(id = "task1", name = "Test Task", userId = mockUserId)
        val mockBatch = mockk<WriteBatch>(relaxed = true)
        val mockDocRef = mockk<DocumentReference>()

        every { mockTasksCollection.document(any()) } returns mockDocRef
        every { mockFirestore.batch() } returns mockBatch
        every { mockBatch.set(any(), any()) } returns mockBatch
        coEvery { mockBatch.commit().await() } returns null

        // Act
        val result = taskCRUD.addTask(task)

        // Assert
        assertTrue(result)
        verify { mockBatch.set(mockDocRef, task) }
        coVerify { mockBatch.commit() }
    }

    @Test
    fun `deleteTask should return true if task exists and belongs to user`() = runBlocking {
        // Arrange
        val taskId = "task1"
        val mockDocSnapshot = mockk<DocumentSnapshot>()
        val mockDocRef = mockk<DocumentReference>()

        every { mockTasksCollection.document(taskId) } returns mockDocRef
        coEvery { mockDocRef.get().await() } returns mockDocSnapshot
        every { mockDocSnapshot.exists() } returns true
        every { mockDocSnapshot.getString("userId") } returns mockUserId
        coEvery { mockDocRef.delete().await() } returns null

        // Act
        val result = taskCRUD.deleteTask(taskId)

        // Assert
        assertTrue(result)
        coVerify { mockDocRef.delete() }
    }

    @Test
    fun `updateTask should return true on successful task update`() = runBlocking {
        // Arrange
        val task = Task(id = "task1", name = "Updated Task", userId = mockUserId)
        val mockDocSnapshot = mockk<DocumentSnapshot>()
        val mockDocRef = mockk<DocumentReference>()

        every { mockTasksCollection.document(task.id) } returns mockDocRef
        coEvery { mockDocRef.get().await() } returns mockDocSnapshot
        every { mockDocSnapshot.exists() } returns true
        every { mockDocSnapshot.getString("userId") } returns mockUserId
        coEvery { mockDocRef.set(any()).await() } returns null

        // Act
        val result = taskCRUD.updateTask(task)

        // Assert
        assertTrue(result)
        coVerify { mockDocRef.set(task) }
    }

    @Test
    fun `getTasksByDeadlineRange should return tasks within the range`() = runBlocking {
        // Arrange
        val startDate = Date()
        val endDate = Date(startDate.time + 86400000) // +1 day
        val mockQuery = mockk<Query>()
        val mockQuerySnapshot = mockk<QuerySnapshot>()
        val mockTask = Task(id = "task1", name = "Task in Range", userId = mockUserId)

        every { mockTasksCollection.whereEqualTo("userId", mockUserId) } returns mockQuery
        every { mockQuery.whereGreaterThanOrEqualTo("deadline", startDate) } returns mockQuery
        every { mockQuery.whereLessThanOrEqualTo("deadline", endDate) } returns mockQuery
        every { mockQuery.orderBy("deadline", Query.Direction.ASCENDING) } returns mockQuery
        coEvery { mockQuery.get().await() } returns mockQuerySnapshot
        every { mockQuerySnapshot.toObjects(Task::class.java) } returns listOf(mockTask)

        // Act
        val tasks = taskCRUD.getTasksByDeadlineRange(startDate, endDate)

        // Assert
        assertEquals(1, tasks.size)
        assertEquals(mockTask, tasks[0])
    }
}