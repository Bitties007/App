package com.bitties.routinehelper.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a routine template.
 * A routine is a collection of steps that can be scheduled and executed.
 */
@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey
    val routineId: String,
    val name: String,
    val description: String? = null,
    val isEnabled: Boolean = true
)
