# Personal Life OS — Android V0.2 Alpha

Android-first, local-first personal life system built with Kotlin + Jetpack Compose.

## What works in this slice

- Warm Personal Observatory light/dark theme
- First-run onboarding
- Notification, exact-alarm and microphone permission guidance
- Native Android SpeechRecognizer voice capture with offline preference when supported
- Five-tab shell: Today, Plan, Capture, Journey, Me
- Universal Capture deterministic classification:
  - Task
  - Reminder
  - Diary
  - Idea
- Natural reminder parsing for common phrases such as:
  - `tomorrow at 10 AM`
  - `today at 7:30 PM`
  - `tomorrow morning`
  - `tonight`
- Room persistence for tasks, reminders, journal entries, ideas, projects and activity events
- Today/Plan/Journey/Me driven by saved local data instead of hard-coded demo values
- Task completion writes back to Room and records activity history
- Exact AlarmManager reminders when exact-alarm access is available
- Notification delivery for reminders
- Boot/package-update recovery through WorkManager
- Cold-start reminder reconciliation, including reminders saved before exact-alarm access was granted
- GitHub Actions debug-APK workflow

## Architecture

- Kotlin + Jetpack Compose
- Hilt
- Room / SQLite
- DataStore Preferences
- AlarmManager
- WorkManager
- NotificationManager
- Android runtime permission APIs
- MVVM-oriented UI state

## Build

The repository includes `.github/workflows/android-debug-apk.yml`, which builds:

`app/build/outputs/apk/debug/app-debug.apk`

The build workflow targets AGP 9.4.0 / Gradle 9.6.0 / API 37 / JDK 17.

## Next slice

1. Create/edit Project and Goal flows
2. Reminder edit/reschedule/split actions
3. Daily/weekly review generation from activity data
4. Search across tasks, journal, ideas and activity
5. Biometric app lock
6. Better date/time parser and recurring reminders
