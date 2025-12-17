package com.bitties.routinehelper.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing completion status of a step within a routine run.
 * Tracks which steps have been completed during a specific routine execution.
 */
@Entity(
    tableName = "routine_run_steps",
    foreignKeys = [
        ForeignKey(
            entity = RoutineRunEntity::class,
            parentColumns = ["runId"],
            childColumns = ["runId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoutineStepEntity::class,
            parentColumns = ["stepId"],
            childColumns = ["stepId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("runId"), Index("stepId")]
)
data class RoutineRunStepEntity(
    @PrimaryKey
    val runStepId: String,
    val runId: String,
    val stepId: String,
    val isCompleted: Boolean = false,
    val completedAtUtc: Long? = null
)
