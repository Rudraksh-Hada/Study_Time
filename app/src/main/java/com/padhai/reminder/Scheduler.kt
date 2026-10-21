package com.padhai.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object Scheduler {
    const val ACTION_TICK = "com.padhai.reminder.TICK"

    private fun pending(c: Context): PendingIntent = PendingIntent.getBroadcast(
        c, 0, Intent(c, ReminderReceiver::class.java).setAction(ACTION_TICK),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    fun canExact(c: Context): Boolean =
        Build.VERSION.SDK_INT < 31 || c.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()

    /** Schedules the NEXT reminder (one alarm at a time; receiver re-schedules itself). */
    fun schedule(c: Context) {
        val am = c.getSystemService(AlarmManager::class.java)
        val at = System.currentTimeMillis() + Prefs.interval(c) * 60_000L
        if (canExact(c)) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pending(c))
        else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pending(c))
    }

    fun cancel(c: Context) {
        c.getSystemService(AlarmManager::class.java).cancel(pending(c))
    }
}
