package dk.dtu.ToDoList.model.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import dk.dtu.ToDoList.model.data.task.Task
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class TaskCRUDTest {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockTasksCollection: CollectionReference
    private lateinit var mockContext: Context
    private lateinit var taskCRUD: TaskCRUD

    @BeforeEach
    fun setup() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)
        mockkStatic(Log::class)

        mockFirestore = mockk()
        mockAuth = mockk()
        mockTasksCollection = mockk()
        mockContext = mockk()

        every { FirebaseFirestore.getInstance() } returns mockFirestore
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockFirestore.collection("tasks") } returns mockTasksCollection
        every { mockAuth.currentUser?.uid } returns "testUserId"

        taskCRUD = TaskCRUD(mockContext).apply {
            val firestoreField = TaskCRUD::class.java.getDeclaredField("firestore")
            firestoreField.isAccessible = true
            firestoreField.set(this, mockFirestore)

            val authField = TaskCRUD::class.java.getDeclaredField("auth")
            authField.isAccessible = true
            authField.set(this, mockAuth)
        }

        every { Log.e(any(), any<String>()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.i(any(), any<String>()) } returns 0
        every { Log.d(any(), any<String>()) } returns 0
        every { Log.v(any(), any<String>()) } returns 0
    }

    @Test
    @DisplayName("Should add a task successfully")
    fun `test addTask`() = runBlocking {
        val mockDocumentReference: DocumentReference = mockk()
        val task = Task(name = "Test Task", deadline = Date())

        every { mockTasksCollection.document(any()) } returns mockDocumentReference
        coEvery { mockDocumentReference.set(any()) } returns mockk()

        val result = taskCRUD.addTask(task)

        assertTrue(result)
        verify { mockTasksCollection.document(any()) }
        coVerify { mockDocumentReference.set(any()) }
    }

    @Test
    @DisplayName("Should get tasks as Flow")
    fun `test getTasksFlow`() = runBlocking {
        val mockQuery: Query = mockk()
        val mockRegistration: ListenerRegistration = mockk()
        val mockQuerySnapshot: QuerySnapshot = mockk()
        val mockDocument: DocumentSnapshot = mockk()
        val task = Task(id = "task1", name = "Task 1")

        every { mockTasksCollection.whereEqualTo("userId", "testUserId") } returns mockQuery
        every { mockQuery.orderBy("deadline", Query.Direction.ASCENDING) } returns mockQuery
        every { mockQuery.orderBy("createdAt", Query.Direction.DESCENDING) } returns mockQuery
        every { mockQuery.addSnapshotListener(any<EventListener<QuerySnapshot>>()) } answers {
            val listener = args[0] as EventListener<QuerySnapshot>
            listener.onEvent(mockQuerySnapshot, null)
            mockRegistration
        }

        every { mockQuerySnapshot.documents } returns listOf(mockDocument)
        every { mockDocument.toObject(Task::class.java) } returns task

        val tasksFlow = taskCRUD.getTasksFlow()
        val tasks = tasksFlow.first()

        assertEquals(1, tasks.size)
        assertEquals("Task 1", tasks.first().name)
        verify { mockTasksCollection.whereEqualTo("userId", "testUserId") }
        verify { mockQuery.orderBy("deadline", Query.Direction.ASCENDING) }
    }

    @Test
    @DisplayName("Should update a task successfully")
    fun `test updateTask`() = runBlocking {
        val mockDocumentReference: DocumentReference = mockk()
        val task = Task(id = "task1", name = "Updated Task")

        every { mockTasksCollection.document("task1") } returns mockDocumentReference
        coEvery { mockDocumentReference.get().await() } returns mockk {
            every { exists() } returns true
            every { getString("userId") } returns "testUserId"
        }
        coEvery { mockDocumentReference.set(any()) } returns mockk()

        val result = taskCRUD.updateTask(task)

        assertTrue(result)
        verify { mockTasksCollection.document("task1") }
        coVerify { mockDocumentReference.get().await() }
        coVerify { mockDocumentReference.set(any()) }
    }

    @Test
    @DisplayName("Should delete a task successfully")
    fun `test deleteTask`() = runBlocking {
        val mockDocumentReference: DocumentReference = mockk()

        every { mockTasksCollection.document("task1") } returns mockDocumentReference
        coEvery { mockDocumentReference.get().await() } returns mockk {
            every { exists() } returns true
            every { getString("userId") } returns "testUserId"
        }
        coEvery { mockDocumentReference.delete().await() } returns mockk()

        val result = taskCRUD.deleteTask("task1")

        assertTrue(result)
        verify { mockTasksCollection.document("task1") }
        coVerify { mockDocumentReference.get().await() }
        coVerify { mockDocumentReference.delete().await() }
    }
}
