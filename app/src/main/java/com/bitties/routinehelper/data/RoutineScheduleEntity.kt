package com.bitties.routinehelper.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a schedule for a routine.
 * Defines when the routine should trigger notifications (days of week + time).
 */
@Entity(
    tableName = "routine_schedules",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["routineId"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("routineId")]
)
data class RoutineScheduleEntity(
    @PrimaryKey
    val scheduleId: String,
    val routineId: String,
    val daysOfWeek: String, // Comma-separated list: "MON,TUE,WED,THU,FRI"
    val timeLocal: String, // HH:mm format, e.g., "07:00"
    val isEnabled: Boolean = true
)
