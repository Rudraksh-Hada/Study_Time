# Padhai Reminder 📚

A tiny Android app that nags you to study. Every 10 minutes it pops up a silent notification with a taunt that is just guilt-trippy enough to make you put the phone down.

No AI, no cloud, no account. Everything runs on your phone.

---

## Download

[Download Study Reminder APK](https://github.com/Rudraksh-Hada/Study_Time/releases/latest)

---

## Why I made this

I kept telling myself "5 more minutes" and then it was 2 a.m. and the syllabus was exactly where I left it. Normal reminders were too polite, so I built one that isn't.

---

## What it does

- Sends a **silent pop-up notification** every 10 minutes (no sound, no vibration, it just appears on screen)
- Taunts are mostly **Hinglish and Hindi (80%)**, some **English (20%)**
- Has **10 MB+ of unique taunt lines**
- Has a **focus timer** with a ring that shrinks as your time runs out
- Tracks how much you studied today and your **streak**
- Comes in **light and dark mode** with a berry colour theme
- Works offline and starts again by itself after you restart your phone

---

## Features

| Feature | What it does |
|---|---|
| Reminder interval | Pick 1, 5, 10, 15, 20, 30, 45 or 60 minutes |
| Active hours | Set when reminders start and stop, so no taunts while you sleep |
| Today's subject | Type what you're studying, it shows up in the notification |
| Daily goal | Set your target in minutes and watch the progress bar fill |
| Streak | Counts how many days in a row you hit your goal |
| Focus timer | Set any length from 1 to 240 minutes, reminders pause while it runs |
| "I'm studying" button | Tap it on the notification to log your time |
| Theme switch | System, Light or Dark |

---

## Get the code

You have 3 easy ways. Pick one.

**Option 1: Clone with Git (best, lets you save your changes)**

First install Git from git-scm.com if you don't have it. Then open a terminal (Command Prompt on Windows) and run:

```
git clone https://github.com/Rudraksh-Hada/Study_Time.git
```

Replace `YOUR-USERNAME` with the real GitHub name. This makes a folder called `PadhaiReminder` with everything inside.

**Option 2: Clone from inside Android Studio**

1. Open Android Studio
2. Click **Get from VCS** on the welcome screen (or **File > New > Project from Version Control**)
3. Paste the repo link
4. Choose a folder and click **Clone**

**Option 3: Just download the ZIP**

On the GitHub page click **Code > Download ZIP**, then unzip it. Easiest, but you can't push changes back later without Git.

---

## Open it in Android Studio

You need **Android Studio** (free, from developer.android.com/studio).

1. Open Android Studio
2. Click **Open** and pick the `PadhaiReminder` folder (the one with `settings.gradle.kts` inside)
3. Wait for Gradle to finish syncing. The first time takes a while, so keep your Wi-Fi on. You can see progress at the bottom.
4. If it asks to install the Android SDK or any missing parts, click **Install** or **OK**

---

## Run it on your phone

1. On your phone: **Settings > About phone**, tap **Build number** 7 times to unlock Developer options
2. Go to **Developer options** and turn on **USB debugging**
3. Plug your phone into the PC and tap **Allow** on the popup
4. In Android Studio, pick your phone from the device list at the top
5. Press the green **Run** button (or `Shift + F10`)

No phone? Use **Device Manager** on the right side to make a virtual phone and run on that instead.

Want an APK file to share? Go to **Build > Build APK(s)**.

---

## Editing the app in Android Studio

**Finding your way around**

- The **Project** panel on the left shows all the files. At the top of it you can switch the view between **Android** and **Project**. Android view is simpler.
- Double click a file to open it in the middle.
- Press `Shift` twice to search for any file or setting.
- Press `Ctrl + F` to search inside a file, `Ctrl + Shift + F` to search the whole project.

**Which file to open for what**

| I want to change | Open this |
|---|---|
| How the main screen looks | `res/layout/activity_main.xml` |
| What the buttons do | `MainActivity.kt` |
| The notification (title, buttons, look) | `Notifier.kt` |
| Default interval, active hours, goal | `Prefs.kt` |
| The shrinking ring | `FocusScreen.kt` |
| Colours | `res/values/colors.xml` |
| Hinglish / English share | `TauntRepo.kt` |
| Taunt lines | `tools/generate_taunts.py` (see below) |

**After you change something**

1. Press the green **Run** button again. Android Studio rebuilds and reinstalls the app.
2. If you changed a `.gradle.kts` file, click **Sync Now** in the yellow bar at the top first.
3. If you see weird errors that make no sense, try **Build > Clean Project**, then run again.

**When something breaks**

- Red underline in the code: hover over it and read the message, or press `Alt + Enter` to see suggested fixes
- Build errors show up in the **Build** tab at the bottom
- App crashes on the phone: open the **Logcat** tab at the bottom and look for lines in red


Do this every time you finish a small change. It's like a save point you can go back to.

---

## First-time setup on your phone

Do these or the reminders may not show up properly:

1. **Allow notifications** when the app asks
2. **Allow "Alarms & reminders"** so the timing is exact
3. Tap **Battery settings** in the app and set it to **Unrestricted**
4. On Xiaomi, Oppo, Vivo or Realme: turn on **Autostart** and **Floating / Pop-up notifications**
5. In the notification settings, set the "Study taunts" channel to **Pop on screen**

Then hit **Send test notification** to check it works.

---

## Changing the taunts

All taunts come from a small Python script, so you can write your own.

1. Open `tools/generate_taunts.py` in any text editor
2. Edit the lists of openers, middles and closers
3. Run it:

```
python3 tools/generate_taunts.py
```

(On Windows try `python` or `py` instead of `python3`.)

4. Rebuild the app in Android Studio

Each taunt is built as **opener + middle + closer**, so a few dozen lines turn into tens of thousands of combinations.

---

## What's inside

```
PadhaiReminder/
├── app/src/main/
│   ├── assets/                 # taunt text files (10 MB+)
│   ├── java/com/padhai/reminder/
│   │   ├── MainActivity.kt     # main screen
│   │   ├── FocusActivity.kt    # focus timer screen
│   │   ├── FocusScreen.kt      # the shrinking ring
│   │   ├── Notifier.kt         # builds the notification
│   │   ├── Scheduler.kt        # sets the repeating alarm
│   │   ├── ReminderReceiver.kt # runs when an alarm fires
│   │   ├── BootReceiver.kt     # restarts reminders after reboot
│   │   ├── TauntRepo.kt        # picks a random taunt
│   │   └── Prefs.kt            # saved settings and progress
│   └── res/                    # layouts, colours, themes, icons
└── tools/generate_taunts.py    # makes the taunt files
```


---

## Built with

- Kotlin
- Android Views (XML) for the main screen
- Jetpack Compose for the focus timer
- AlarmManager for the 10-minute schedule
- Python for generating the taunts

Minimum Android version: **8.0 (API 26)**

---

## Things that aren't perfect

- The 10-minute timing can drift a bit if your phone's battery saver is aggressive
- Taunts are mixed from fragments, so a few lines may sound a little repetitive
- The focus timer needs the app installed normally, it won't count sessions if the app is force-stopped

---

## Made by

Made by a student who needed this more than anyone. Use it, change it, make the taunts meaner if you want. Just go study.
