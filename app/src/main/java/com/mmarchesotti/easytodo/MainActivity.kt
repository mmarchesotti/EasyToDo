package com.mmarchesotti.easytodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mmarchesotti.easytodo.ui.AppDestinations
import com.mmarchesotti.easytodo.ui.dailytasks.DailyTasksScreen
import com.mmarchesotti.easytodo.ui.mainscreen.TaskManagerScreen
import com.mmarchesotti.easytodo.ui.theme.EasyToDoTheme
import com.mmarchesotti.easytodo.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EasyToDoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController() // Creates and remembers the NavController
    val taskViewModel: TaskViewModel = viewModel() // Get a single ViewModel instance

    NavHost(
        navController = navController,
        startDestination = AppDestinations.TASK_MANAGER_SCREEN // Your initial screen
    ) {
        composable(route = AppDestinations.TASK_MANAGER_SCREEN) {
            // Pass navController if TaskManagerScreen needs to navigate elsewhere
            TaskManagerScreen(
                viewModel = taskViewModel,
                onNavigateToDailyTasks = {
                    navController.navigate(AppDestinations.DAILY_TASKS_SCREEN)
                }
            )
        }
        composable(route = AppDestinations.DAILY_TASKS_SCREEN) {
            // DailyTasksScreen might also need navController if it navigates (e.g., back or to details)
            DailyTasksScreen(
                viewModel = taskViewModel
                // onNavigateBack = { navController.popBackStack() } // Example
            )
        }
        // You can add more composable destinations here for other screens
    }
}
