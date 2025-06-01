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
import com.mmarchesotti.easytodo.ui.dailyschedules.DailySchedulesScreen
import com.mmarchesotti.easytodo.ui.mainscreen.ScheduleManagerScreen
import com.mmarchesotti.easytodo.ui.theme.EasyToDoTheme
import com.mmarchesotti.easytodo.viewmodel.ScheduleViewModel

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
    val scheduleViewModel: ScheduleViewModel = viewModel() // Get a single ViewModel instance

    NavHost(
        navController = navController,
        startDestination = AppDestinations.TASK_MANAGER_SCREEN // Your initial screen
    ) {
        composable(route = AppDestinations.TASK_MANAGER_SCREEN) {
            // Pass navController if ScheduleManagerScreen needs to navigate elsewhere
            ScheduleManagerScreen(
                viewModel = scheduleViewModel,
                onNavigateToDailySchedules = {
                    navController.navigate(AppDestinations.DAILY_TASKS_SCREEN)
                }
            )
        }
        composable(route = AppDestinations.DAILY_TASKS_SCREEN) {
            // DailySchedulesScreen might also need navController if it navigates (e.g., back or to details)
            DailySchedulesScreen(
                viewModel = scheduleViewModel
                // onNavigateBack = { navController.popBackStack() } // Example
            )
        }
        // You can add more composable destinations here for other screens
    }
}
