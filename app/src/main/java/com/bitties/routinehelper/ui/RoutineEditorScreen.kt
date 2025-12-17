package com.bitties.routinehelper.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Screen for creating or editing a routine.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineEditorScreen(
    viewModel: RoutineEditorViewModel,
    onNavigateBack: () -> Unit
) {
    val routineName by viewModel.routineName.collectAsState()
    val routineDescription by viewModel.routineDescription.collectAsState()
    val steps by viewModel.steps.collectAsState()
    val schedules by viewModel.schedules.collectAsState()

    var showAddStepDialog by remember { mutableStateOf(false) }
    var showAddScheduleDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (routineName.isBlank()) "New Routine" else "Edit Routine") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveRoutine { onNavigateBack() }
                        },
                        enabled = routineName.isNotBlank()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Routine name and description
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = routineName,
                            onValueChange = viewModel::updateName,
                            label = { Text("Routine Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = routineDescription,
                            onValueChange = viewModel::updateDescription,
                            label = { Text("Description (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            }

            // Steps section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Steps",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { showAddStepDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Step")
                    }
                }
            }

            if (steps.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No steps yet. Tap + to add one.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(steps) { index, step ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                step.details?.let { details ->
                                    Text(
                                        text = details,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                step.defaultTimerSeconds?.let { seconds ->
                                    Text(
                                        text = "Timer: ${seconds / 60}m ${seconds % 60}s",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Column {
                                IconButton(
                                    onClick = { viewModel.moveStepUp(step) },
                                    enabled = index > 0
                                ) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")
                                }
                                IconButton(
                                    onClick = { viewModel.moveStepDown(step) },
                                    enabled = index < steps.size - 1
                                ) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")
                                }
                            }
                            IconButton(onClick = { viewModel.deleteStep(step.stepId) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }

            // Schedules section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Schedules",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { showAddScheduleDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Schedule")
                    }
                }
            }

            if (schedules.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No schedules yet. Tap + to add one.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(schedules) { schedule ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = schedule.daysOfWeek.replace(",", ", "),
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "at ${schedule.timeLocal}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { viewModel.deleteSchedule(schedule.scheduleId) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Step Dialog
    if (showAddStepDialog) {
        var stepTitle by remember { mutableStateOf("") }
        var stepDetails by remember { mutableStateOf("") }
        var stepTimerMinutes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddStepDialog = false },
            title = { Text("Add Step") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stepTitle,
                        onValueChange = { stepTitle = it },
                        label = { Text("Step Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = stepDetails,
                        onValueChange = { stepDetails = it },
                        label = { Text("Details (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = stepTimerMinutes,
                        onValueChange = { stepTimerMinutes = it },
                        label = { Text("Timer (minutes, optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val timerSeconds = stepTimerMinutes.toIntOrNull()?.let { it * 60 }
                        viewModel.addStep(
                            title = stepTitle,
                            details = stepDetails.ifBlank { null },
                            timerSeconds = timerSeconds
                        )
                        showAddStepDialog = false
                    },
                    enabled = stepTitle.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStepDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Schedule Dialog
    if (showAddScheduleDialog) {
        var selectedDays by remember { 
            mutableStateOf(setOf("MON", "TUE", "WED", "THU", "FRI")) 
        }
        var scheduleTime by remember { mutableStateOf("07:00") }

        AlertDialog(
            onDismissRequest = { showAddScheduleDialog = false },
            title = { Text("Add Schedule") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select Days:", style = MaterialTheme.typography.labelMedium)
                    val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
                    days.chunked(4).forEach { rowDays ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            rowDays.forEach { day ->
                                FilterChip(
                                    selected = selectedDays.contains(day),
                                    onClick = {
                                        selectedDays = if (selectedDays.contains(day)) {
                                            selectedDays - day
                                        } else {
                                            selectedDays + day
                                        }
                                    },
                                    label = { Text(day) }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = scheduleTime,
                        onValueChange = { scheduleTime = it },
                        label = { Text("Time (HH:mm)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.addSchedule(
                            daysOfWeek = selectedDays.sorted().joinToString(","),
                            timeLocal = scheduleTime
                        )
                        showAddScheduleDialog = false
                    },
                    enabled = selectedDays.isNotEmpty()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddScheduleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
