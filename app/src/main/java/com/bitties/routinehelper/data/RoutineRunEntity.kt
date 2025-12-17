package com.bitties.routinehelper.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a single execution/run of a routine on a specific day.
 * Tracks when the routine was started and completed.
 */
@Entity(
    tableName = "routine_runs",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["routineId"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("routineId"), Index("dateLocal")]
)
data class RoutineRunEntity(
    @PrimaryKey
    val runId: String,
    val routineId: String,
    val dateLocal: String, // YYYY-MM-DD format
    val startedAtUtc: Long? = null,
    val completedAtUtc: Long? = null
)
