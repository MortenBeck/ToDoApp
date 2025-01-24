package dk.dtu.ToDoList.model.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import dk.dtu.ToDoList.model.data.task.Task
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
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
        every { Log.e(any(), any<String>(), any<Throwable>()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.i(any(), any<String>()) } returns 0
        every { Log.d(any(), any<String>()) } returns 0
        every { Log.v(any(), any<String>()) } returns 0
    }

    @Test
    fun `test addTask`() = runTest {
        val taskId = "test-task-id"
        val mockDocumentReference: DocumentReference = mockk(relaxed = true)
        val mockBatch: WriteBatch = mockk(relaxed = true)
        val mockTask: com.google.android.gms.tasks.Task<Void> = mockk()
        val task = Task(name = "Test Task", deadline = Date())

        // Mock the behavior of Firebase Task
        every { mockTask.isComplete } returns true
        every { mockTask.isSuccessful } returns true
        every { mockTask.isCanceled } returns false
        every { mockTask.getResult() } returns null
        every { mockTask.getException() } returns null
        every { mockTask.addOnCompleteListener(any()) } answers {
            val listener = it.invocation.args[0] as com.google.android.gms.tasks.OnCompleteListener<Void>
            listener.onComplete(mockTask)
            mockTask
        }

        // Mock Firestore batch and collection behavior
        every { mockTasksCollection.document(any()) } returns mockDocumentReference
        every { mockTasksCollection.document(taskId) } returns mockDocumentReference
        every { mockFirestore.batch() } returns mockBatch
        every { mockBatch.set(mockDocumentReference, any()) } returns mockBatch
        every { mockBatch.commit() } returns mockTask

        // Mock task ID generation
        mockkObject(taskCRUD)
        every { taskCRUD.generateTaskId() } returns taskId

        // Act: Add the task
        val result = taskCRUD.addTask(task)

        // Assert: Verify behavior and result
        assertTrue(result)
        verify { mockTasksCollection.document(taskId) }
        verify { mockBatch.set(mockDocumentReference, any()) }
        coVerify { mockBatch.commit() }
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


}
