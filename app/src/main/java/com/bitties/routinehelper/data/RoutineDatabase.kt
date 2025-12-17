package com.bitties.routinehelper.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Room database for the Routine Helper app.
 * Contains all entities and provides DAOs for data access.
 */
@Database(
    entities = [
        RoutineEntity::class,
        RoutineStepEntity::class,
        RoutineScheduleEntity::class,
        RoutineRunEntity::class,
        RoutineRunStepEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RoutineDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun routineStepDao(): RoutineStepDao
    abstract fun routineScheduleDao(): RoutineScheduleDao
    abstract fun routineRunDao(): RoutineRunDao
    abstract fun routineRunStepDao(): RoutineRunStepDao

    companion object {
        @Volatile
        private var INSTANCE: RoutineDatabase? = null

        fun getDatabase(context: Context): RoutineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoutineDatabase::class.java,
                    "routine_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed sample data on first launch
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    seedSampleData(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Seeds the database with two sample routines on first launch.
         */
        private suspend fun seedSampleData(database: RoutineDatabase) {
            val routineDao = database.routineDao()
            val stepDao = database.routineStepDao()
            val scheduleDao = database.routineScheduleDao()

            // Routine 1: Brush teeth (AM/PM)
            val brushTeethId = UUID.randomUUID().toString()
            routineDao.insertRoutine(
                RoutineEntity(
                    routineId = brushTeethId,
                    name = "Brush teeth (AM/PM)",
                    description = "Daily tooth brushing routine"
                )
            )

            stepDao.insertSteps(
                listOf(
                    RoutineStepEntity(
                        stepId = UUID.randomUUID().toString(),
                        routineId = brushTeethId,
                        title = "Get toothbrush",
                        sortOrder = 0
                    ),
                    RoutineStepEntity(
                        stepId = UUID.randomUUID().toString(),
                        routineId = brushTeethId,
                        title = "Apply toothpaste",
                        sortOrder = 1
                    ),
                    RoutineStepEntity(
                        stepId = UUID.randomUUID().toString(),
                        routineId = brushTeethId,
                        title = "Brush for 2 minutes",
                        sortOrder = 2,
                        defaultTimerSeconds = 120
                    ),
                    RoutineStepEntity(
                        stepId = UUID.randomUUID().toString(),
                        routineId = brushTeethId,
                        title = "Rinse",
                        sortOrder = 3
                    ),
                    RoutineStepEntity(
                        stepId = UUID.randomUUID().toString(),
                        routineId = brushTeethId,
                        title = "Put away",
                        sortOrder = 4
                    )
                )
            )

            scheduleDao.insertSchedules(
                listOf(
                    RoutineScheduleEntity(
                        scheduleId = UUID.randomUUID().toString(),
                        routineId = brushTeethId,
                        daysOfWeek = "MON,TUE,WED,THU,FRI,SAT,SUN",
                        timeLocal = "07:30"
                    ),
                    RoutineScheduleEntity(
                        scheduleId = UUID.randomUUID().toString(),
                        routineId = brushTeethId,
                        daysOfWeek = "MON,TUE,WED,THU,FRI,SAT,SUN",
                        timeLocal = "20:30"
                    )
                )
            )

            // Routine 2: Weekday school morning
            val schoolMorningId = UUID.randomUUID().toString()
            routineDao.insertRoutine(
                RoutineEntity(
                    routineId = schoolMorningId,
                    name = "Weekday school morning",
                    description = "Morning routine for school days"
                )
            )

            stepDao.insertSteps(
                listOf(
                    RoutineStepEntity(
                        stepId = UUID.randomUUID().toString(),
                        routineId = schoolMorningId,
                        title = "Wake up",
                        sortOrder = 0
                    ),
                    RoutineStepEntity(
                        stepId = UUID.randomUUID().toString(),
                        routineId = schoolMorningId,
                        title = "Get dressed",
                        sortOrder = 1,
                        defaultTimerSeconds = 300
                    ),
                    RoutineStepEntity(
                        stepId = UUID.randomUUID().toString(),
                        routineId = schoolMorningId,
                        title = "Breakfast",
                        sortOrder = 2,
                        defaultTimerSeconds = 900
                    ),
                    RoutineStepEntity(
                        stepId = UUID.randomUUID().toString(),
                        routineId = schoolMorningId,
                        title = "Pack bag",
                        sortOrder = 3,
                        defaultTimerSeconds = 300
                    ),
                    RoutineStepEntity(
                        stepId = UUID.randomUUID().toString(),
                        routineId = schoolMorningId,
                        title = "Leave house",
                        sortOrder = 4
                    )
                )
            )

            scheduleDao.insertSchedule(
                RoutineScheduleEntity(
                    scheduleId = UUID.randomUUID().toString(),
                    routineId = schoolMorningId,
                    daysOfWeek = "MON,TUE,WED,THU,FRI",
                    timeLocal = "06:30"
                )
            )
        }
    }
}
