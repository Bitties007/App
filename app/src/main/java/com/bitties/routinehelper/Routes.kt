package com.bitties.routinehelper

/**
 * Navigation routes for the app.
 */
object Routes {
    const val HOME = "home"
    const val ROUTINE_LIST = "routine_list"
    const val ROUTINE_EDITOR = "routine_editor/{routineId}"
    const val ROUTINE_EDITOR_NEW = "routine_editor/new"
    const val ROUTINE_RUNNER = "routine_runner/{routineId}"

    fun routineEditor(routineId: String) = "routine_editor/$routineId"
    fun routineRunner(routineId: String) = "routine_runner/$routineId"
}
