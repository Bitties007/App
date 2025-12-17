package com.bitties.routinehelper.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for RoutineRunStep operations.
 */
@Dao
interface RoutineRunStepDao {
    @Query("SELECT * FROM routine_run_steps WHERE runId = :runId ORDER BY runStepId")
    fun getRunStepsForRun(runId: String): Flow<List<RoutineRunStepEntity>>

    @Query("SELECT * FROM routine_run_steps WHERE runStepId = :runStepId")
    suspend fun getRunStepById(runStepId: String): RoutineRunStepEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunStep(runStep: RoutineRunStepEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunSteps(runSteps: List<RoutineRunStepEntity>)

    @Update
    suspend fun updateRunStep(runStep: RoutineRunStepEntity)

    @Delete
    suspend fun deleteRunStep(runStep: RoutineRunStepEntity)

    @Query("DELETE FROM routine_run_steps WHERE runId = :runId")
    suspend fun deleteRunStepsForRun(runId: String)
}
