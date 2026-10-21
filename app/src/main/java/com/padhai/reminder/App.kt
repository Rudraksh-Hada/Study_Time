package com.padhai.reminder

import androidx.appcompat.app.AppCompatDelegate

class App : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(Prefs.themeMode(this))
    }
}
