package com.padhai.reminder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvToday: TextView
    private lateinit var tvStreak: TextView
    private lateinit var tvFocusTitle: TextView
    private lateinit var etFocusMin: EditText
    private lateinit var progress: LinearProgressIndicator
    private lateinit var btnToggle: MaterialButton
    private lateinit var btnFocus: MaterialButton
    private lateinit var ivBackground: ImageView
    private lateinit var vDimOverlay: View

    // Gallery picker for background image
    private val bgPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Persist permission so we can reload across app restarts
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
            Prefs.setBackgroundUri(this, uri.toString())
            applyBackground()
        }
    }

    // Notification permission
    private val notifPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) Toast.makeText(this, getString(R.string.toast_notif_permission), Toast.LENGTH_LONG).show()
        startReminders()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        tvToday = findViewById(R.id.tvToday)
        tvStreak = findViewById(R.id.tvStreak)
        tvFocusTitle = findViewById(R.id.tvFocusTitle)
        etFocusMin = findViewById(R.id.etFocusMin)
        progress = findViewById(R.id.progress)
        btnToggle = findViewById(R.id.btnToggle)
        btnFocus = findViewById(R.id.btnFocus)
        ivBackground = findViewById(R.id.ivBackground)
        vDimOverlay = findViewById(R.id.vDimOverlay)

        applyBackground()

        etFocusMin.setText(Prefs.focusMin(this).toString())
        etFocusMin.doAfterTextChanged {
            val v = it?.toString()?.toIntOrNull()
            if (v != null && v in 1..240) { Prefs.setFocusMin(this, v); refresh() }
        }

        btnToggle.setOnClickListener {
            if (Prefs.enabled(this)) stopReminders() else requestAndStart()
        }

        btnFocus.setOnClickListener {
            saveInputs()
            startActivity(Intent(this, FocusActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnCustomize).setOnClickListener { showCustomizeSheet() }
    }

    override fun onResume() {
        super.onResume()
        if (Prefs.enabled(this)) Scheduler.schedule(this)
        refresh()
    }

    override fun onPause() {
        super.onPause()
        saveInputs()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun saveInputs() {
        val f = etFocusMin.text.toString().toIntOrNull()
        if (f != null && f in 1..240) Prefs.setFocusMin(this, f)
        else etFocusMin.setText(Prefs.focusMin(this).toString())
    }

    // ─── Customize bottom sheet ───────────────────────────────────────────────

    private fun showCustomizeSheet() {
        val sheet = BottomSheetDialog(this)
        val v = LayoutInflater.from(this).inflate(R.layout.sheet_customize, null)
        sheet.setContentView(v)

        // Send test notification
        v.findViewById<MaterialButton>(R.id.btnSheetTest).setOnClickListener {
            val t = TauntRepo.next(this)
            Notifier.show(this, t)
            sheet.dismiss()
        }

        // Battery settings
        v.findViewById<MaterialButton>(R.id.btnSheetBattery).setOnClickListener {
            startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
            sheet.dismiss()
        }

        // Theme toggle
        val btnTheme = v.findViewById<MaterialButton>(R.id.btnSheetTheme)
        btnTheme.text = themeLabel()
        btnTheme.setOnClickListener {
            val next = when (Prefs.themeMode(this)) {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> AppCompatDelegate.MODE_NIGHT_NO
                AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            Prefs.setThemeMode(this, next)
            AppCompatDelegate.setDefaultNightMode(next)
            sheet.dismiss()
        }

        // Background image picker
        v.findViewById<MaterialButton>(R.id.btnSheetBg).setOnClickListener {
            bgPickerLauncher.launch("image/*")
            sheet.dismiss()
        }

        // Remove background
        v.findViewById<MaterialButton>(R.id.btnSheetBgClear).setOnClickListener {
            Prefs.setBackgroundUri(this, null)
            applyBackground()
            sheet.dismiss()
        }

        sheet.show()
    }

    private fun themeLabel() = when (Prefs.themeMode(this)) {
        AppCompatDelegate.MODE_NIGHT_NO -> "Theme: Light ☀️"
        AppCompatDelegate.MODE_NIGHT_YES -> "Theme: Dark 🌙"
        else -> "Theme: System 🌓"
    }

    // ─── Background ──────────────────────────────────────────────────────────

    private fun applyBackground() {
        val uriStr = Prefs.backgroundUri(this)
        if (uriStr != null) {
            try {
                ivBackground.setImageURI(Uri.parse(uriStr))
                ivBackground.visibility = View.VISIBLE
                vDimOverlay.visibility = View.VISIBLE
            } catch (_: Exception) {
                ivBackground.visibility = View.GONE
                vDimOverlay.visibility = View.GONE
            }
        } else {
            ivBackground.visibility = View.GONE
            vDimOverlay.visibility = View.GONE
        }
    }

    // ─── Reminders ───────────────────────────────────────────────────────────

    private fun requestAndStart() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        else startReminders()
    }

    private fun startReminders() {
        Prefs.setEnabled(this, true)
        Notifier.ensureChannel(this)
        if (!Scheduler.canExact(this) && Build.VERSION.SDK_INT >= 31) {
            Toast.makeText(this, getString(R.string.toast_exact_alarm), Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName")))
        }
        Scheduler.schedule(this)
        refresh()
    }

    private fun stopReminders() {
        Prefs.setEnabled(this, false)
        Scheduler.cancel(this)
        refresh()
    }

    // ─── UI Refresh ───────────────────────────────────────────────────────────

    private fun refresh() {
        val on = Prefs.enabled(this)
        btnToggle.text = if (on) getString(R.string.btn_stop_reminders) else getString(R.string.btn_start_reminders)
        tvStatus.text = if (on) getString(R.string.status_on, Prefs.interval(this)) else getString(R.string.status_off)

        tvFocusTitle.text = "Focus timer (${Prefs.focusMin(this)} min)"

        val done = Prefs.todayMinutes(this)
        val goal = Prefs.goalMin(this)
        tvToday.text = getString(R.string.today_progress, done, goal)
        progress.max = goal
        progress.progress = done.coerceAtMost(goal)
        val s = Prefs.streak(this)
        tvStreak.text = if (s > 0) getString(R.string.streak_active, s) else getString(R.string.streak_zero)

        val focusActive = Prefs.focusUntil(this) > System.currentTimeMillis()
        btnFocus.text = if (focusActive) "Focus running\u2026 tap to open" else "Open focus timer"
    }
}
