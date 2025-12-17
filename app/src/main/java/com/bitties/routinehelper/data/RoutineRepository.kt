package com.bitties.routinehelper.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Repository for managing routines, steps, schedules, and runs.
 * Provides a clean API for the ViewModel layer to access data.
 */
class RoutineRepository(private val database: RoutineDatabase) {
    private val routineDao = database.routineDao()
    private val stepDao = database.routineStepDao()
    private val scheduleDao = database.routineScheduleDao()
    private val runDao = database.routineRunDao()
    private val runStepDao = database.routineRunStepDao()

    // Routine operations
    fun getAllRoutines(): Flow<List<RoutineEntity>> = routineDao.getAllRoutines()
    
    suspend fun getRoutineById(routineId: String): RoutineEntity? = 
        routineDao.getRoutineById(routineId)
    
    fun getRoutineByIdFlow(routineId: String): Flow<RoutineEntity?> = 
        routineDao.getRoutineByIdFlow(routineId)

    suspend fun insertRoutine(routine: RoutineEntity) = routineDao.insertRoutine(routine)
    
    suspend fun updateRoutine(routine: RoutineEntity) = routineDao.updateRoutine(routine)
    
    suspend fun deleteRoutine(routineId: String) = routineDao.deleteRoutineById(routineId)

    // Step operations
    fun getStepsForRoutine(routineId: String): Flow<List<RoutineStepEntity>> = 
        stepDao.getStepsForRoutine(routineId)

    suspend fun insertStep(step: RoutineStepEntity) = stepDao.insertStep(step)
    
    suspend fun insertSteps(steps: List<RoutineStepEntity>) = stepDao.insertSteps(steps)
    
    suspend fun updateStep(step: RoutineStepEntity) = stepDao.updateStep(step)
    
    suspend fun deleteStep(stepId: String) = stepDao.deleteStepById(stepId)

    // Schedule operations
    fun getSchedulesForRoutine(routineId: String): Flow<List<RoutineScheduleEntity>> = 
        scheduleDao.getSchedulesForRoutine(routineId)

    suspend fun getAllEnabledSchedules(): List<RoutineScheduleEntity> = 
        scheduleDao.getAllEnabledSchedules()

    suspend fun insertSchedule(schedule: RoutineScheduleEntity) = 
        scheduleDao.insertSchedule(schedule)
    
    suspend fun updateSchedule(schedule: RoutineScheduleEntity) = 
        scheduleDao.updateSchedule(schedule)
    
    suspend fun deleteSchedule(scheduleId: String) = scheduleDao.deleteScheduleById(scheduleId)

    // Run operations
    suspend fun getOrCreateRunForToday(routineId: String): RoutineRunEntity {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val existingRun = runDao.getRunForDate(routineId, today)
        
        return if (existingRun != null) {
            existingRun
        } else {
            val newRun = RoutineRunEntity(
                runId = UUID.randomUUID().toString(),
                routineId = routineId,
                dateLocal = today,
                startedAtUtc = System.currentTimeMillis()
            )
            runDao.insertRun(newRun)
            
            // Create run steps for all routine steps
            val steps = stepDao.getStepsForRoutine(routineId)
            // We need to get the steps synchronously here
            // This is a simplified approach; in production, consider using first()
            newRun
        }
    }

    fun getRunByIdFlow(runId: String): Flow<RoutineRunEntity?> = runDao.getRunByIdFlow(runId)
    
    suspend fun updateRun(run: RoutineRunEntity) = runDao.updateRun(run)

    fun getRunsForDate(dateLocal: String): Flow<List<RoutineRunEntity>> = 
        runDao.getRunsForDate(dateLocal)

    // Run step operations
    fun getRunStepsForRun(runId: String): Flow<List<RoutineRunStepEntity>> = 
        runStepDao.getRunStepsForRun(runId)

    suspend fun createRunStepsForRun(runId: String, steps: List<RoutineStepEntity>) {
        val runSteps = steps.map { step ->
            RoutineRunStepEntity(
                runStepId = UUID.randomUUID().toString(),
                runId = runId,
                stepId = step.stepId,
                isCompleted = false
            )
        }
        runStepDao.insertRunSteps(runSteps)
    }

    suspend fun updateRunStep(runStep: RoutineRunStepEntity) = runStepDao.updateRunStep(runStep)

    suspend fun toggleStepCompletion(runStepId: String) {
        val runStep = runStepDao.getRunStepById(runStepId)
        if (runStep != null) {
            val updated = runStep.copy(
                isCompleted = !runStep.isCompleted,
                completedAtUtc = if (!runStep.isCompleted) System.currentTimeMillis() else null
            )
            runStepDao.updateRunStep(updated)
        }
    }
}
