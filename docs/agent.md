# Agent Documentation — Noty

This is the primary entry point for any AI agent working on this project. Read this file first to orient yourself, then load specific docs on demand via signposts.

---

## Project Overview

**Name:** Noty
**Purpose:** A lightweight Android note-taking app that pins notes as persistent notifications in the status bar, so users never forget tasks or thoughts.
**Package:** `com.example.noty`

---

## Stack & Frameworks

| Category | Technology | Version |
|----------|-----------|---------|
| Language | Kotlin | 1.9.22 |
| UI Framework | Jetpack Compose | BOM 2024.02.00 |
| Design System | Material 3 | (via Compose BOM) |
| Architecture | MVVM + Repository | — |
| Database | Room | 2.6.1 |
| DB Code Generation | KSP | 1.9.22-1.0.17 |
| Preferences | DataStore | 1.0.0 |
| Lifecycle | AndroidX Lifecycle (ViewModel, LiveData) | 2.7.0 |
| Compose ViewModel | lifecycle-viewmodel-compose | 2.7.0 |
| Compose LiveData | runtime-livedata | (via BOM) |
| Activity Compose | activity-compose | (via BOM) |
| Icons | material-icons-extended | (via BOM) |
| Splash Screen | core-splashscreen | 1.0.1 |
| AppCompat | appcompat | 1.6.1 |
| Core KTX | core-ktx | 1.12.0 |
| Build System | Gradle | 8.2.0 |
| Min SDK | Android 8.0 | API 26 |
| Target / Compile SDK | Android 14 | API 34 |

---

## Architecture

MVVM + Repository pattern with reactive data flow.

```
UI (Compose)
  └─ observes LiveData
      └─ ViewModel (NotyViewModel)
          ├─ controls foreground service lifecycle
          ├─ manages notification sync
          └─ delegates to Repository
              └─ NotyRepository
                  └─ Room (NoteDao → AppDatabase → SQLite)
```

**Key decisions:**
- Room queries return `Flow<List<Note>>` → ViewModel converts to `LiveData` → Compose observes via `observeAsState()`
- A foreground service (`NotyService`) keeps the process alive so notifications persist
- Service auto-starts when ≥1 note exists, auto-stops when empty
- `NotificationReceiver` (BroadcastReceiver) handles notification action intents (delete, dismiss)
- `BootReceiver` restarts the service after device reboot
- Theme is persisted via `DataStore` and applied via `AppCompatDelegate`

📄 Need more detail on system design? See /docs/design.md
📄 Need more detail on the notification system? See /docs/notifications.md

---

## Project Layout

```
noty/
├── app/src/main/
│   ├── AndroidManifest.xml               # Components, permissions, receivers, tile service
│   └── java/com/example/noty/
│       ├── NotyApplication.kt            # Application class; theme initialization
│       ├── data/
│       │   ├── AppDatabase.kt            # Room database singleton (v4), migration 3→4
│       │   ├── Note.kt                   # Note entity + NoteType enum
│       │   ├── NoteDao.kt                # DAO: CRUD + reactive getAllNotes()
│       │   └── NotyRepository.kt         # Repository: wraps DAO, exposes to ViewModel
│       ├── ui/
│       │   ├── MainActivity.kt           # Single activity, Compose host, permission + tile intent handling
│       │   ├── NotyApp.kt                # ALL Compose UI: composables, screens, sheets, theme
│       │   └── NotyViewModel.kt          # ViewModel + ViewModelFactory; service lifecycle, notification sync
│       └── utils/
│           ├── NotificationHelper.kt     # Creates/cancels notifications; channel setup
│           ├── NotyService.kt            # Foreground service; keeps app alive
│           ├── NotificationReceiver.kt   # Handles DELETE and DISMISSED notification actions
│           ├── BootReceiver.kt           # BOOT_COMPLETED → starts NotyService
│           ├── QuickNoteTileService.kt   # Quick Settings tile → sends ACTION_ADD_NOTE
│           └── ThemeManager.kt           # DataStore theme preference (SYSTEM/LIGHT/DARK)
├── app/src/test/                         # Unit tests (ThemeManagerTest, NotyViewModelTest placeholder)
├── app/build.gradle                      # Module config: SDK versions, dependencies, Compose enabled
├── build.gradle                          # Project config: plugins
└── docs/                                 # This documentation system
```

---

## Commands

```bash
# Install debug APK on connected device/emulator
./gradlew installDebug

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.example.noty.utils.ThemeManagerTest"

# Run instrumentation tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Clean and rebuild
./gradlew clean build
```

---

## Rules

1. **Compose only** — All UI must be written in Jetpack Compose. Do not add XML layouts, RecyclerView adapters, or ViewBinding code.
2. **Room migrations required** — Any schema change must include an explicit Room migration object. Never just bump the database version without one.
3. **Debug destructive migration** — `fallbackToDestructiveMigration()` is enabled in debug builds. Do not rely on data persistence during development.
4. **Coroutines for async** — All database and background operations use coroutines in `viewModelScope`. Do not use callbacks or threads directly.
5. **Service lifecycle** — `NotyService` is managed exclusively by `NotyViewModel`. Do not start or stop it from anywhere else.
6. **Notifications via helper** — All notification operations must go through `NotificationHelper`. Do not create or cancel notifications directly.
7. **Minimum SDK 26** — Do not use APIs below API 26 without appropriate version guards.

📌 Note: Any new key rules discovered or decided during work on this project should be added to this section.
📌 Note: If a rule is critical — something that must never be violated — add it to `claude.md` directly for maximum enforcement weight, in addition to listing it here.
📌 If uncertain about any rule, re-read this file before proceeding.

---

## Documentation System

Docs are loaded on demand — never all at once. Load a doc only when working on or referencing the area it covers.

```
📄 design.md        — Architecture decisions, data flow, component interactions, DB schema, theme system.
                      Load when making structural or architectural changes.

📄 features.md      — Full feature list with status (complete / in progress / planned).
                      Load when working on or referencing any feature.

📄 requirements.md  — Functional and non-functional requirements, constraints.
                      Load when validating scope or assessing fit.

📄 notifications.md — Notification system deep-dive: channels, sticky/non-sticky behavior,
                      PendingIntent scheme, resurrection, service lifecycle.
                      Load when touching notifications, NotificationHelper, NotyService, or NotificationReceiver.

📄 play-store.md    — Google Play Store submission: app name, descriptions, data safety, permissions,
                      graphic asset specs, and pre-launch checklist.
                      Load when preparing a release or working on anything store-facing.
```

---

## Signposting Convention

Throughout all docs, use this format to point to another doc:

```
📄 Need more detail on [topic]? See /docs/[file].md
```

---

## Update Policy

- If you are working on something and have loaded a doc for it, and anything it covers changes — update that doc before finishing your work on that task.
- If anything in **this file** changes — new framework added, architecture shifts, new tool introduced, new rule established — update this file immediately to reflect it.
- If something new arises that doesn't fit cleanly into any existing doc — create a new doc for it, add it to the documentation system section in this file, and signpost to it from wherever relevant.
- Keep all docs accurate. They are the source of truth for agents.
- `CURRENT.md` should be updated only when focus, status, or key decisions change — not on a schedule.
