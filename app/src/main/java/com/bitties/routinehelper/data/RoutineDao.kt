package com.bitties.routinehelper.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Routine operations.
 */
@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines ORDER BY name")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE routineId = :routineId")
    suspend fun getRoutineById(routineId: String): RoutineEntity?

    @Query("SELECT * FROM routines WHERE routineId = :routineId")
    fun getRoutineByIdFlow(routineId: String): Flow<RoutineEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Query("DELETE FROM routines WHERE routineId = :routineId")
    suspend fun deleteRoutineById(routineId: String)
}
