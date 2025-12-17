# Routine Helper - Executive Function Android App

An MVP Android app built with Kotlin and Jetpack Compose that helps users with executive functioning by providing routines, step-by-step tasks, timers, and scheduled reminders.

## Features

### Core Functionality
- **Routines**: Create and manage daily routines with customizable steps
- **Step-by-Step Tasks**: Break down routines into manageable checklist items
- **Focus Timer**: Start timers for individual steps or entire routines
- **Scheduled Reminders**: Set up notifications for routines on specific days and times
- **Daily Tracking**: Track completion of routines per day

### Sample Data
The app comes pre-loaded with two example routines:
1. **Brush teeth (AM/PM)**: A simple 5-step routine for tooth brushing
2. **Weekday school morning**: A 5-step routine for school day mornings

## Technical Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM (ViewModel + Repository)
- **Database**: Room for local persistence
- **Notifications**: WorkManager for scheduled reminders
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)

## Project Structure

```
app/
├── src/main/java/com/bitties/routinehelper/
│   ├── data/                          # Data layer
│   │   ├── RoutineEntity.kt           # Room entities
│   │   ├── RoutineStepEntity.kt
│   │   ├── RoutineScheduleEntity.kt
│   │   ├── RoutineRunEntity.kt
│   │   ├── RoutineRunStepEntity.kt
│   │   ├── RoutineDao.kt              # DAOs for database access
│   │   ├── RoutineStepDao.kt
│   │   ├── RoutineScheduleDao.kt
│   │   ├── RoutineRunDao.kt
│   │   ├── RoutineRunStepDao.kt
│   │   ├── RoutineDatabase.kt         # Room database configuration
│   │   └── RoutineRepository.kt       # Repository for data operations
│   ├── ui/                            # UI layer
│   │   ├── HomeScreen.kt              # Home screen composable
│   │   ├── HomeViewModel.kt           # Home screen ViewModel
│   │   ├── RoutineListScreen.kt       # Routine list screen
│   │   ├── RoutineListViewModel.kt
│   │   ├── RoutineEditorScreen.kt     # Create/edit routines
│   │   ├── RoutineEditorViewModel.kt
│   │   ├── RoutineRunnerScreen.kt     # Execute routines
│   │   ├── RoutineRunnerViewModel.kt
│   │   └── theme/
│   │       └── Theme.kt               # Material 3 theming
│   ├── workers/
│   │   └── RoutineNotificationWorker.kt # WorkManager for notifications
│   ├── MainActivity.kt                # Main activity with navigation
│   ├── Routes.kt                      # Navigation routes
│   └── RoutineHelperApplication.kt    # Application class
└── src/main/res/                      # Resources
    ├── values/
    │   ├── strings.xml
    │   ├── colors.xml
    │   └── themes.xml
    └── drawable/
        └── ic_launcher.xml            # App icon
```

## Data Model

### Room Entities

1. **RoutineEntity**: Stores routine templates
   - `routineId` (UUID)
   - `name` (String)
   - `description` (String, nullable)
   - `isEnabled` (Boolean)

2. **RoutineStepEntity**: Steps within a routine
   - `stepId` (UUID)
   - `routineId` (FK)
   - `title` (String)
   - `details` (String, nullable)
   - `sortOrder` (Int)
   - `defaultTimerSeconds` (Int, nullable)

3. **RoutineScheduleEntity**: Scheduling information
   - `scheduleId` (UUID)
   - `routineId` (FK)
   - `daysOfWeek` (String: "MON,TUE,WED,THU,FRI")
   - `timeLocal` (String: "HH:mm")
   - `isEnabled` (Boolean)

4. **RoutineRunEntity**: Tracks routine execution
   - `runId` (UUID)
   - `routineId` (FK)
   - `dateLocal` (String: "YYYY-MM-DD")
   - `startedAtUtc` (Long, nullable)
   - `completedAtUtc` (Long, nullable)

5. **RoutineRunStepEntity**: Step completion within a run
   - `runStepId` (UUID)
   - `runId` (FK)
   - `stepId` (FK)
   - `isCompleted` (Boolean)
   - `completedAtUtc` (Long, nullable)

## Screens

### 1. Home Screen
- Displays next scheduled routine with time
- Shows list of today's scheduled routines
- Quick "Start Routine" button for immediate access
- Access to routine management

### 2. Routine List Screen
- View all routines
- Create new routines
- Edit existing routines
- Delete routines
- Quick start button for each routine

### 3. Routine Editor Screen
- Edit routine name and description
- Add, reorder, and delete steps
- Set default timer for each step
- Add schedules (days of week + time)
- Delete schedules

### 4. Routine Runner Screen
- Shows routine name and progress
- Checklist of steps with completion status
- Select step to view details
- Timer controls (Start/Pause/Reset)
- Countdown display
- Finish routine when all steps complete

## Building the App

### Requirements
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API 34
- Gradle 8.2

### Build Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd App
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository
   - Wait for Gradle sync to complete

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run on device or emulator**
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

### Command Line Build (without Android Studio)

If you have Android SDK installed and `ANDROID_HOME` set:

```bash
# Build debug APK
./gradlew assembleDebug

# Install to connected device
./gradlew installDebug

# Build release APK (unsigned)
./gradlew assembleRelease
```

The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

## Key Implementation Details

### Database Seeding
On first launch, the app automatically seeds two sample routines:
- "Brush teeth (AM/PM)" - scheduled twice daily
- "Weekday school morning" - scheduled weekday mornings

### Timer Implementation
- Timers use Kotlin coroutines for countdown
- Each step can have an optional default timer
- Timer controls: Start, Pause, Reset
- Visual countdown display in MM:SS format

### Notifications
- Uses WorkManager for scheduling
- Periodic work requests for daily routines
- Android 13+ runtime notification permission handling
- Tapping notification opens the app

### State Management
- ViewModels handle UI state
- StateFlow for reactive UI updates
- Repository pattern for data access
- Room Flow for real-time database updates

## MVP Constraints

This is an MVP (Minimum Viable Product) following strict scope:

**Included:**
- ✅ Local database (Room)
- ✅ Routines with steps
- ✅ Timers
- ✅ Scheduled reminders
- ✅ Daily progress tracking

**Explicitly NOT included:**
- ❌ Cloud sync
- ❌ User accounts/login
- ❌ AI/LLM features
- ❌ Social features
- ❌ Gamification (streaks, badges)
- ❌ Analytics
- ❌ Complex settings

## Design Principles

1. **Low friction**: Minimal taps to start a routine
2. **Low choice**: Sensible defaults, limited options
3. **Clean architecture**: Separation of concerns (UI/ViewModel/Repository/Data)
4. **Maintainable code**: Descriptive names, inline comments
5. **Material 3**: Modern Android UI guidelines

## Permissions

The app requests:
- `POST_NOTIFICATIONS` - For scheduled routine reminders (Android 13+)
- `SCHEDULE_EXACT_ALARM` - For precise notification timing
- `USE_EXACT_ALARM` - Alternative exact alarm permission

## Future Enhancements (Not in MVP)

Potential v2 features (not implemented):
- Custom routine icons
- Routine templates library
- Voice timer controls
- Widget for quick access
- Dark mode customization
- Backup/restore to local file
- Statistics and insights

## Troubleshooting

### Build Fails
- Ensure JDK 17 is installed and set as project JDK
- Verify Android SDK is properly installed
- Run `./gradlew clean` then rebuild

### Notifications Not Working
- Check notification permissions in device settings
- Verify exact alarm permissions (Android 12+)
- Check battery optimization settings

### Database Errors
- Clear app data to reset database
- Check logcat for detailed error messages

## License

This is an MVP demonstration project.

## Contact

For issues or questions, please create an issue in the repository.
