package dk.dtu.ToDoList.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dk.dtu.ToDoList.R
import dk.dtu.ToDoList.data.Task
import dk.dtu.ToDoList.data.TaskTag
import dk.dtu.ToDoList.data.TaskPriority
import dk.dtu.ToDoList.data.TasksRepository.simpleDateFormat
import dk.dtu.ToDoList.feature.TaskList
import dk.dtu.ToDoList.feature.BottomNavBar
import dk.dtu.ToDoList.feature.BottomNavItem
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.time.ZoneId






class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoApp()
        }
    }
}

@Composable
fun ToDoApp() {
    val navController = rememberNavController()
    val currentScreen = remember { mutableStateOf("Tasks") }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                items = listOf(
                    BottomNavItem(
                        label = "Tasks",
                        icon = R.drawable.home_grey,
                        isSelected = currentScreen.value == "Tasks"
                    ),
                    BottomNavItem(
                        label = "Favourites",
                        icon = R.drawable.favorite_grey,
                        isSelected = currentScreen.value == "Favourites"
                    ),
                    BottomNavItem(
                        label = "Planned",
                        icon = R.drawable.calender_grey,
                        isSelected = currentScreen.value == "Planned"
                    ),
                    BottomNavItem(
                        label = "Profile",
                        icon = R.drawable.profile_grey,
                        isSelected = currentScreen.value == "Profile"
                    )
                ),
                onItemClick = { item ->
                    currentScreen.value = item.label
                    navController.navigate(item.label) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "Tasks",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable("Tasks") { TaskListScreen() }
            composable("Favourites") { FavouritesScreen() }
            composable("Planned") { PlannedScreen() }
            composable("Profile") { ProfileScreen() }
        }
    }
}

@Composable
fun TaskListScreen() {
    // Define today's tasks
    val todayTasks = remember {
        listOf(
            Task(
                name = "Homework - UX",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.SCHOOL,
                completed = false
            ),
            Task(
                name = "Fix project at work",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.WORK,
                completed = true
            ),
            Task(
                name = "Walk the dog",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.PET,
                completed = false
            ),
            Task(
                name = "Cancel Netflix subscription",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.HOME,
                completed = false
            )
        )
    }


    // Define future tasks
    val futureTasks = remember {
        listOf(
            Task(
                name = "Call mechanic",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.TRANSPORT,
                completed = false
            ),
            Task(
                name = "Grocery Shopping",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.HOME,
                completed = false
            ),
            Task(
                name = "Reorganize desk at work",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.WORK,
                completed = false
            ),
            Task(
                name = "Clean bathroom",
                deadline = simpleDateFormat.parse("19-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.HOME,
                completed = false
            ),
            Task(
                name = "Get ready for album drop",
                deadline = simpleDateFormat.parse("21-11-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.HOME,
                completed = false
            ),
            Task(
                name = "Homework - Math",
                deadline = simpleDateFormat.parse("22-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.SCHOOL,
                completed = false
            ),
            Task(
                name = "Find passport",
                deadline = simpleDateFormat.parse("31-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.HOME,
                completed = false
            ),
            Task(
                name = "Research christmas gifts",
                deadline = simpleDateFormat.parse("12-12-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.HOME,
                completed = false
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Title
        Text(
            text = "To-Do List",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Today's Tasks Section
        Text(
            text = "Today",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TaskList(
            Tasks = todayTasks,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        // Future Tasks Section
        Text(
            text = "Future",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TaskList(
            Tasks = futureTasks,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun FavouritesScreen() {
    // List of favorite tasks
    val favouriteTasks = remember {
        listOf(
            Task(
                name = "Walk the dog",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.PET,
                completed = false
            ),
            Task(
                name = "Grocery Shopping",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.HOME,
                completed = false
            ),
            Task(
                name = "Research christmas gifts",
                deadline = simpleDateFormat.parse("12-12-2024")!!,
                priority = TaskPriority.LOW,
                tag = TaskTag.HOME,
                completed = false
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Screen Title
        Text(
            text = "Favourites",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Favourites Task List
        TaskList(
            Tasks = favouriteTasks,
            modifier = Modifier.weight(1f)
        )
    }
}



@Composable
fun PlannedScreen() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Planned",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Calendar
        Calendar(
            selectedDate = selectedDate,
            currentMonth = currentMonth,
            onDateSelected = { selectedDate = it },
            onMonthChanged = { currentMonth = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tasks for selected date
        TasksForDate(selectedDate)
    }
}

@Composable
fun Calendar(
    selectedDate: LocalDate,
    currentMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (YearMonth) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        // Month navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChanged(currentMonth.minusMonths(1)) }) {
                Text("←")
            }

            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleMedium
            )

            IconButton(onClick = { onMonthChanged(currentMonth.plusMonths(1)) }) {
                Text("→")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weekday headers
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek.let { firstDay ->
                (0..6).map { firstDay.plus(it.toLong()) }
            }

            daysOfWeek.forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        val days = mutableListOf<LocalDate>()
        val firstOfMonth = currentMonth.atDay(1)
        val lastOfMonth = currentMonth.atEndOfMonth()

        // Add padding days from previous month
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var current = firstOfMonth
        while (current.dayOfWeek != firstDayOfWeek) {
            current = current.minusDays(1)
            days.add(0, current)
        }

        // Add all days of current month
        current = firstOfMonth
        while (!current.isAfter(lastOfMonth)) {
            days.add(current)
            current = current.plusDays(1)
        }

        // Add padding days from next month
        current = lastOfMonth.plusDays(1)
        while (days.size < 42) { // 6 rows of 7 days
            days.add(current)
            current = current.plusDays(1)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(days) { date ->
                DayCell(
                    date = date,
                    isSelected = date == selectedDate,
                    isCurrentMonth = date.month == currentMonth.month,
                    onDateSelected = onDateSelected
                )
            }
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val isToday = date == LocalDate.now()

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }
            )
            .clickable { onDateSelected(date) }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                !isCurrentMonth -> MaterialTheme.colorScheme.outline
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
fun TasksForDate(date: LocalDate) {
    // Define tasks for each date (adjust this to your actual data source)
    val allTasks = remember {
        listOf(
            Task(
                name = "Homework - UX",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.HIGH,
                tag = TaskTag.SCHOOL,
                completed = false
            ),
            Task(
                name = "Fix project at work",
                deadline = simpleDateFormat.parse("18-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.WORK,
                completed = true
            ),
            Task(
                name = "Walk the dog",
                deadline = simpleDateFormat.parse("17-11-2024")!!,
                priority = TaskPriority.MEDIUM,
                tag = TaskTag.PET,
                completed = false
            ),
            // Add more tasks as needed
        )
    }

    // Filter tasks for the selected date
    val tasksForDate = remember(date) {
        allTasks.filter { task ->
            // Compare the task deadline with the selected date (only include tasks with matching dates)
            task.deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() == date
        }
    }

    Column {
        Text(
            text = "Tasks for ${date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Display tasks for the selected date
        if (tasksForDate.isNotEmpty()) {
            TaskList(
                Tasks = tasksForDate,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = "No tasks for this day.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}


    @Composable
    fun ProfileScreen() {
        Text(
            text = "Profile Screen",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    }
