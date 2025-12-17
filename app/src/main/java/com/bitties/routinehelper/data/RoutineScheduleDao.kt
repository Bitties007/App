package com.bitties.routinehelper.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for RoutineSchedule operations.
 */
@Dao
interface RoutineScheduleDao {
    @Query("SELECT * FROM routine_schedules WHERE routineId = :routineId")
    fun getSchedulesForRoutine(routineId: String): Flow<List<RoutineScheduleEntity>>

    @Query("SELECT * FROM routine_schedules WHERE isEnabled = 1")
    suspend fun getAllEnabledSchedules(): List<RoutineScheduleEntity>

    @Query("SELECT * FROM routine_schedules WHERE scheduleId = :scheduleId")
    suspend fun getScheduleById(scheduleId: String): RoutineScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: RoutineScheduleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<RoutineScheduleEntity>)

    @Update
    suspend fun updateSchedule(schedule: RoutineScheduleEntity)

    @Delete
    suspend fun deleteSchedule(schedule: RoutineScheduleEntity)

    @Query("DELETE FROM routine_schedules WHERE scheduleId = :scheduleId")
    suspend fun deleteScheduleById(scheduleId: String)

    @Query("DELETE FROM routine_schedules WHERE routineId = :routineId")
    suspend fun deleteSchedulesForRoutine(routineId: String)
}
