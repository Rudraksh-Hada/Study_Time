package com.padhai.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_STUDYING = "com.padhai.reminder.STUDYING"
        const val ACTION_STOP = "com.padhai.reminder.STOP"
    }

    override fun onReceive(c: Context, i: Intent) {
        when (i.action) {
            Scheduler.ACTION_TICK -> {
                if (!Prefs.enabled(c)) return
                // 24/7 — always fire UNLESS in a focus session
                val inFocus = System.currentTimeMillis() < Prefs.focusUntil(c)
                if (!inFocus) Notifier.show(c)
                Scheduler.schedule(c)   // schedule the next one
            }
            ACTION_STUDYING -> {
                Prefs.addMinutes(c, Prefs.interval(c))
                NotificationManagerCompat.from(c).cancel(Notifier.ID)
            }
            ACTION_STOP -> {
                Prefs.setEnabled(c, false)
                Scheduler.cancel(c)
                NotificationManagerCompat.from(c).cancel(Notifier.ID)
            }
        }
    }
}
