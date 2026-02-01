# Wakey 🔔

A smart alarm clock Android app that requires you to complete tasks to dismiss alarms.

## Features

- **3 Wake-up Tasks**
  - 🚶 Walk 30 steps
  - 📱 Hold phone vertical for 20 seconds  
  - ⏳ Wait 15 seconds

- **Smart Alarms**
  - Exact alarm scheduling
  - Repeat on specific days
  - Snooze (5 minutes)
  - Works on lock screen
  - Survives device restart

- **Core Functionality**
  - Sound + vibration
  - Dark mode support
  - Device time format (12/24 hour)
  - Max 3 alarms (MVP)

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Database**: Room
- **Async**: Kotlin Coroutines + Flow

## Requirements

- **Min SDK**: API 26 (Android 8.0)
- **Target SDK**: API 34 (Android 14)

## Build Instructions

### Debug Build

```bash
./gradlew assembleDebug
```

### Release Build

1. Follow instructions in `SIGNING_INSTRUCTIONS.md`
2. Generate release keystore
3. Create `keystore.properties`
4. Build:

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

## Testing

See `walkthrough.md` for comprehensive test scenarios.

### Quick Test

1. Create alarm (2 min from now)
2. Close app + lock screen
3. Verify alarm wakes screen
4. Complete task to dismiss

## Project Structure

```
app/
├── data/          # Room database, repositories
├── domain/        # Models, use cases
├── service/       # AlarmScheduler, AlarmService
├── receiver/      # AlarmReceiver, BootReceiver
├── ui/
│   ├── alarmlist/    # List screen
│   ├── alarmedit/    # Create/edit screen
│   └── alarmring/    # Ringing screen + tasks
└── utils/         # Helpers (TimeFormatter, etc)
```

## Privacy

**No data collection**. Everything stays on device.

See `PRIVACY_POLICY.md` for details.

## Play Store

Ready for Play Store submission. See `play_store_checklist.md` for launch preparation.

## License

[-]

## Credits

Developed by [Sam]
