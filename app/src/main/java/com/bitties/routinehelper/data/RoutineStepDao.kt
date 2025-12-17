package com.bitties.routinehelper.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for RoutineStep operations.
 */
@Dao
interface RoutineStepDao {
    @Query("SELECT * FROM routine_steps WHERE routineId = :routineId ORDER BY sortOrder")
    fun getStepsForRoutine(routineId: String): Flow<List<RoutineStepEntity>>

    @Query("SELECT * FROM routine_steps WHERE stepId = :stepId")
    suspend fun getStepById(stepId: String): RoutineStepEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: RoutineStepEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<RoutineStepEntity>)

    @Update
    suspend fun updateStep(step: RoutineStepEntity)

    @Delete
    suspend fun deleteStep(step: RoutineStepEntity)

    @Query("DELETE FROM routine_steps WHERE stepId = :stepId")
    suspend fun deleteStepById(stepId: String)

    @Query("DELETE FROM routine_steps WHERE routineId = :routineId")
    suspend fun deleteStepsForRoutine(routineId: String)
}
