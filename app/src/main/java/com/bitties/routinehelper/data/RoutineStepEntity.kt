package com.bitties.routinehelper.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a step within a routine.
 * Steps are ordered by sortOrder and can have optional timers.
 */
@Entity(
    tableName = "routine_steps",
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
data class RoutineStepEntity(
    @PrimaryKey
    val stepId: String,
    val routineId: String,
    val title: String,
    val details: String? = null,
    val sortOrder: Int,
    val defaultTimerSeconds: Int? = null
)
