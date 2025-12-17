package com.bitties.routinehelper.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for RoutineRun operations.
 */
@Dao
interface RoutineRunDao {
    @Query("SELECT * FROM routine_runs WHERE routineId = :routineId AND dateLocal = :dateLocal")
    suspend fun getRunForDate(routineId: String, dateLocal: String): RoutineRunEntity?

    @Query("SELECT * FROM routine_runs WHERE runId = :runId")
    fun getRunByIdFlow(runId: String): Flow<RoutineRunEntity?>

    @Query("SELECT * FROM routine_runs WHERE dateLocal = :dateLocal")
    fun getRunsForDate(dateLocal: String): Flow<List<RoutineRunEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: RoutineRunEntity)

    @Update
    suspend fun updateRun(run: RoutineRunEntity)

    @Delete
    suspend fun deleteRun(run: RoutineRunEntity)
}
