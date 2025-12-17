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

/**
 * Screen for running a routine with steps and timer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineRunnerScreen(
    viewModel: RoutineRunnerViewModel,
    onNavigateBack: () -> Unit
) {
    val routine by viewModel.routine.collectAsState()
    val stepsWithCompletion by viewModel.stepsWithCompletion.collectAsState()
    val selectedStep by viewModel.selectedStep.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val completionProgress by viewModel.completionProgress.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(routine?.name ?: "Routine") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            // Timer controls if a step is selected
            selectedStep?.let { step ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = step.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        // Timer display
                        Text(
                            text = formatTime(timerSeconds),
                            style = MaterialTheme.typography.displayMedium,
                            color = if (isTimerRunning) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Start/Pause button
                            Button(
                                onClick = {
                                    if (isTimerRunning) {
                                        viewModel.pauseTimer()
                                    } else {
                                        step.defaultTimerSeconds?.let { seconds ->
                                            if (timerSeconds == 0 || timerSeconds == seconds) {
                                                viewModel.startTimer(seconds)
                                            } else {
                                                viewModel.startTimer(timerSeconds)
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = step.defaultTimerSeconds != null
                            ) {
                                Icon(
                                    if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(if (isTimerRunning) "Pause" else "Start")
                            }

                            // Reset button
                            OutlinedButton(
                                onClick = { viewModel.resetTimer() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Reset")
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Progress indicator
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Progress: ${completionProgress.first} / ${completionProgress.second}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    LinearProgressIndicator(
                        progress = if (completionProgress.second > 0) 
                            completionProgress.first.toFloat() / completionProgress.second 
                        else 0f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Finish routine button
                    if (completionProgress.first == completionProgress.second && 
                        completionProgress.second > 0) {
                        Button(
                            onClick = {
                                viewModel.finishRoutine()
                                onNavigateBack()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Finish Routine")
                        }
                    }
                }
            }

            // Steps list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stepsWithCompletion) { stepWithCompletion ->
                    val step = stepWithCompletion.step
                    val runStep = stepWithCompletion.runStep
                    val isCompleted = runStep?.isCompleted == true

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.selectStep(step) },
                        colors = if (selectedStep?.stepId == step.stepId) {
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        } else {
                            CardDefaults.cardColors()
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Checkbox
                                Checkbox(
                                    checked = isCompleted,
                                    onCheckedChange = {
                                        runStep?.let { rs ->
                                            viewModel.toggleStepCompletion(rs.runStepId)
                                        }
                                    }
                                )
                                
                                Column {
                                    Text(
                                        text = step.title,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    step.details?.let { details ->
                                        Text(
                                            text = details,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    step.defaultTimerSeconds?.let { seconds ->
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Timer,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "${seconds / 60}m ${seconds % 60}s",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }

                            if (selectedStep?.stepId == step.stepId) {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Selected",
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

/**
 * Formats seconds into MM:SS format.
 */
private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
