package com.bitties.routinehelper

import android.app.Application
import com.bitties.routinehelper.data.RoutineDatabase
import com.bitties.routinehelper.data.RoutineRepository

/**
 * Application class for dependency injection.
 * Provides singleton instances of database and repository.
 */
class RoutineHelperApplication : Application() {
    val database: RoutineDatabase by lazy { RoutineDatabase.getDatabase(this) }
    val repository: RoutineRepository by lazy { RoutineRepository(database) }
}
