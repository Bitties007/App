package com.bitties.routinehelper

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bitties.routinehelper.ui.*
import com.bitties.routinehelper.ui.theme.RoutineHelperTheme

/**
 * Main activity that hosts the navigation and Compose UI.
 */
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle permission result if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            RoutineHelperTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RoutineHelperApp()
                }
            }
        }
    }
}

@Composable
fun RoutineHelperApp() {
    val navController = rememberNavController()
    val application = androidx.compose.ui.platform.LocalContext.current.applicationContext 
        as RoutineHelperApplication
    val repository = application.repository

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        // Home screen
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(repository)
            )
            HomeScreen(
                viewModel = viewModel,
                onNavigateToRoutineList = {
                    navController.navigate(Routes.ROUTINE_LIST)
                },
                onNavigateToRoutineRunner = { routineId ->
                    navController.navigate(Routes.routineRunner(routineId))
                }
            )
        }

        // Routine list screen
        composable(Routes.ROUTINE_LIST) {
            val viewModel: RoutineListViewModel = viewModel(
                factory = RoutineListViewModelFactory(repository)
            )
            RoutineListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditor = { routineId ->
                    if (routineId != null) {
                        navController.navigate(Routes.routineEditor(routineId))
                    } else {
                        navController.navigate(Routes.ROUTINE_EDITOR_NEW)
                    }
                },
                onNavigateToRunner = { routineId ->
                    navController.navigate(Routes.routineRunner(routineId))
                }
            )
        }

        // Routine editor screen (new)
        composable(Routes.ROUTINE_EDITOR_NEW) {
            val viewModel: RoutineEditorViewModel = viewModel(
                factory = RoutineEditorViewModelFactory(repository, null)
            )
            RoutineEditorScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Routine editor screen (edit existing)
        composable(
            route = Routes.ROUTINE_EDITOR,
            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId")
            val viewModel: RoutineEditorViewModel = viewModel(
                factory = RoutineEditorViewModelFactory(repository, routineId)
            )
            RoutineEditorScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Routine runner screen
        composable(
            route = Routes.ROUTINE_RUNNER,
            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId") ?: return@composable
            val viewModel: RoutineRunnerViewModel = viewModel(
                factory = RoutineRunnerViewModelFactory(repository, routineId)
            )
            RoutineRunnerScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
