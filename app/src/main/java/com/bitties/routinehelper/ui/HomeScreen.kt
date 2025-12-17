package com.bitties.routinehelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Home screen showing today's scheduled routines.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToRoutineList: () -> Unit,
    onNavigateToRoutineRunner: (String) -> Unit
) {
    val todaySchedules by viewModel.todaySchedules.collectAsState()
    val nextSchedule by viewModel.nextSchedule.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Routine Helper") },
                actions = {
                    IconButton(onClick = onNavigateToRoutineList) {
                        Icon(Icons.Default.List, contentDescription = "Manage Routines")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Next scheduled routine card
            nextSchedule?.let { scheduled ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Next Scheduled",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = scheduled.routine.name,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "at ${scheduled.schedule.timeLocal}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = { onNavigateToRoutineRunner(scheduled.routine.routineId) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Start Routine")
                        }
                    }
                }
            }

            // Today's routines
            Text(
                text = "Today's Routines",
                style = MaterialTheme.typography.titleLarge
            )

            if (todaySchedules.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No routines scheduled for today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(todaySchedules) { scheduled ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigateToRoutineRunner(scheduled.routine.routineId) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = scheduled.routine.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = scheduled.schedule.timeLocal,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Start",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
