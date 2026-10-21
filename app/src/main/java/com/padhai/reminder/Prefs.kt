package com.padhai.reminder

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Prefs {
    private fun sp(c: Context) = c.getSharedPreferences("study", Context.MODE_PRIVATE)
    private fun fmt() = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private fun today(): String = fmt().format(Date())
    private fun yesterday(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return fmt().format(cal.time)
    }

    fun enabled(c: Context) = sp(c).getBoolean("enabled", false)
    fun setEnabled(c: Context, v: Boolean) = sp(c).edit().putBoolean("enabled", v).apply()

    // Fixed 5-minute interval (24/7)
    fun interval(c: Context) = 5

    fun subject(c: Context): String = sp(c).getString("subject", "") ?: ""
    fun setSubject(c: Context, v: String) = sp(c).edit().putString("subject", v).apply()

    fun goalMin(c: Context) = sp(c).getInt("goal", 240)
    fun setGoalMin(c: Context, v: Int) = sp(c).edit().putInt("goal", v).apply()

    fun focusUntil(c: Context) = sp(c).getLong("focusUntil", 0L)
    fun setFocusUntil(c: Context, v: Long) = sp(c).edit().putLong("focusUntil", v).apply()

    fun focusMin(c: Context) = sp(c).getInt("focusMin", 25).coerceIn(1, 240)
    fun setFocusMin(c: Context, v: Int) = sp(c).edit().putInt("focusMin", v.coerceIn(1, 240)).apply()
    fun sessionMin(c: Context) = sp(c).getInt("sessionMin", 25)       // length of the session that is running
    fun setSessionMin(c: Context, v: Int) = sp(c).edit().putInt("sessionMin", v).apply()

    /** URI string for custom background image (null = none). */
    fun backgroundUri(c: Context): String? = sp(c).getString("bgUri", null)
    fun setBackgroundUri(c: Context, v: String?) = sp(c).edit().putString("bgUri", v).apply()

    fun todayMinutes(c: Context): Int =
        if (sp(c).getString("day", "") == today()) sp(c).getInt("mins", 0) else 0

    fun addMinutes(c: Context, minutes: Int) {
        val s = sp(c)
        val t = today()
        val total = todayMinutes(c) + minutes
        val e = s.edit().putString("day", t).putInt("mins", total)
        if (total >= goalMin(c) && s.getString("met", "") != t) {
            val streak = if (s.getString("met", "") == yesterday()) s.getInt("streak", 0) + 1 else 1
            e.putInt("streak", streak).putString("met", t)
        }
        e.apply()
    }

    fun streak(c: Context): Int {
        val m = sp(c).getString("met", "")
        return if (m == today() || m == yesterday()) sp(c).getInt("streak", 0) else 0
    }

    fun themeMode(c: Context) = sp(c).getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    fun setThemeMode(c: Context, v: Int) = sp(c).edit().putInt("theme", v).apply()

    fun palette(c: Context) = sp(c).getInt("palette", 0)
    fun setPalette(c: Context, v: Int) = sp(c).edit().putInt("palette", v).apply()
}
