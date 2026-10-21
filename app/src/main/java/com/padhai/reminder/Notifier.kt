package com.padhai.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object Notifier {
    const val ID = 1001
    // HIGH importance = heads-up pop; sound/vibration removed = silent
    const val CHANNEL = "study_taunts_silent_v1"

    private val titles = listOf(
        "📚 Study time", "⏰ Time to focus", "📖 Your books are waiting",
        "🎯 Remember your goal", "Study alert"
    )

    fun ensureChannel(c: Context) {
        val ch = NotificationChannel(CHANNEL, "Study taunts (silent pop-up)", NotificationManager.IMPORTANCE_HIGH)
        ch.description = "Silent pop-up study reminders"
        ch.setSound(null, null)
        ch.enableVibration(false)
        ch.enableLights(false)
        ch.setShowBadge(false)
        c.getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
    }

    fun show(c: Context, text: String = TauntRepo.next(c), title: String = titles.random()) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(c, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return
        ensureChannel(c)

        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val open = PendingIntent.getActivity(c, 0, Intent(c, MainActivity::class.java), flags)
        val studying = PendingIntent.getBroadcast(
            c, 1, Intent(c, ReminderReceiver::class.java).setAction(ReminderReceiver.ACTION_STUDYING), flags)
        val stop = PendingIntent.getBroadcast(
            c, 2, Intent(c, ReminderReceiver::class.java).setAction(ReminderReceiver.ACTION_STOP), flags)

        val b = NotificationCompat.Builder(c, CHANNEL)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(open)
            .setColor(ContextCompat.getColor(c, R.color.berry_primary))
            .addAction(0, c.getString(R.string.notif_action_studying), studying)
            .addAction(0, c.getString(R.string.notif_action_stop), stop)
        val goal = Prefs.subject(c)
        if (goal.isNotBlank()) b.setSubText("Goal: $goal")

        NotificationManagerCompat.from(c).notify(ID, b.build())
    }
}
