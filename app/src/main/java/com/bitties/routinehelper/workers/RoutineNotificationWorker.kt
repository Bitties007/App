package com.bitties.routinehelper.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.bitties.routinehelper.MainActivity
import com.bitties.routinehelper.R
import com.bitties.routinehelper.data.RoutineDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * Worker that sends notifications for scheduled routines.
 */
class RoutineNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val routineId = inputData.getString(KEY_ROUTINE_ID) ?: return@withContext Result.failure()
        val routineName = inputData.getString(KEY_ROUTINE_NAME) ?: "Routine"

        createNotificationChannel()
        sendNotification(routineId, routineName)

        Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Routine Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for scheduled routines"
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) 
                as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(routineId: String, routineName: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("routineId", routineId)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            routineId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Using system icon for MVP
            .setContentTitle("Time for: $routineName")
            .setContentText("Tap to start your routine")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) 
            as NotificationManager
        notificationManager.notify(routineId.hashCode(), notification)
    }

    companion object {
        const val CHANNEL_ID = "routine_reminders"
        const val KEY_ROUTINE_ID = "routine_id"
        const val KEY_ROUTINE_NAME = "routine_name"

        /**
         * Schedules notifications for all enabled routine schedules.
         */
        suspend fun scheduleAllRoutineNotifications(context: Context) {
            val database = RoutineDatabase.getDatabase(context)
            val schedules = database.routineScheduleDao().getAllEnabledSchedules()

            for (schedule in schedules) {
                val routine = database.routineDao().getRoutineById(schedule.routineId)
                if (routine != null && routine.isEnabled) {
                    scheduleRoutineNotification(
                        context = context,
                        scheduleId = schedule.scheduleId,
                        routineId = routine.routineId,
                        routineName = routine.name,
                        daysOfWeek = schedule.daysOfWeek,
                        timeLocal = schedule.timeLocal
                    )
                }
            }
        }

        /**
         * Schedules a notification for a specific routine schedule.
         */
        private fun scheduleRoutineNotification(
            context: Context,
            scheduleId: String,
            routineId: String,
            routineName: String,
            daysOfWeek: String,
            timeLocal: String
        ) {
            val inputData = Data.Builder()
                .putString(KEY_ROUTINE_ID, routineId)
                .putString(KEY_ROUTINE_NAME, routineName)
                .build()

            // Calculate initial delay until next occurrence
            val now = LocalTime.now()
            val scheduledTime = LocalTime.parse(timeLocal, DateTimeFormatter.ofPattern("HH:mm"))
            
            // For simplicity in MVP, schedule daily and check day of week in notification
            // A production app would calculate exact next occurrence
            val initialDelay = if (scheduledTime.isAfter(now)) {
                java.time.Duration.between(now, scheduledTime).toMinutes()
            } else {
                java.time.Duration.between(now, scheduledTime).plusDays(1).toMinutes()
            }

            val workRequest = PeriodicWorkRequestBuilder<RoutineNotificationWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(initialDelay, TimeUnit.MINUTES)
                .setInputData(inputData)
                .addTag("routine_notification_$scheduleId")
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "routine_notification_$scheduleId",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }

        /**
         * Cancels all scheduled notifications.
         */
        fun cancelAllNotifications(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag("routine_notification")
        }
    }
}
