# Requirements — Noty

📄 For feature status, see /docs/features.md
📄 For system design, see /docs/design.md

---

## Functional Requirements

### Note Management
- Users must be able to create a note with a required title and an optional description.
- Users must be able to edit any note's title, description, and sticky status.
- Users must be able to delete any note, with a confirmation step before deletion.
- Notes must persist across app close, process kill, and device reboot.

### Notifications
- Each note must appear as a persistent notification in the status bar.
- Sticky notes must not be dismissible via swipe; if dismissed by any means, they must reappear immediately.
- Non-sticky notes must delete themselves from the database when swiped from the notification shade.
- Each notification must include a delete action button that removes the note without opening the app.
- All notifications must be restored automatically on device boot.

### Theme
- The app must support System, Light, and Dark themes.
- Theme selection must persist across sessions.
- On Android 12+ (API 31), the app must use Material 3 dynamic color derived from the device wallpaper.
- Below API 31, the app must use a static Material 3 color palette.

### System Integration
- A Quick Settings tile must allow the user to trigger note creation without navigating to the app manually.
- The foreground service must start automatically when at least one note exists and stop when no notes remain.

---

## Non-Functional Requirements

### Platform
- **Minimum Android version:** Android 8.0 (API 26)
- **Target Android version:** Android 14 (API 34)
- All API-level differences must be handled with appropriate version guards.

### Permissions
- `POST_NOTIFICATIONS` must be requested at runtime on API 33+.
- `RECEIVE_BOOT_COMPLETED` required for boot restoration.
- `FOREGROUND_SERVICE` and `FOREGROUND_SERVICE_DATA_SYNC` required for background operation.

### Performance
- The note list must update reactively without manual refresh (Room Flow → LiveData → Compose).
- Notification updates must be immediate, triggered synchronously after each DB write.
- Redundant service start/stop calls must be suppressed (via `isServiceRunning` flag).

### Reliability
- Notes must never be silently lost due to process death or device restart.
- A sticky notification must always be present in the status bar as long as its note exists in the DB.
- Database schema changes must never cause data loss in production builds — explicit Room migrations are required.

### Code Quality
- All UI must be written in Jetpack Compose. No XML layouts.
- All async operations must use Kotlin coroutines in appropriate scopes (`viewModelScope`).
- No database or background operations on the main thread.

---

## Constraints

- The app is entirely local — no network access, no server, no sync.
- Room uses `fallbackToDestructiveMigration()` in debug builds only; explicit migrations are required for release.
- The `NoteType` enum (NOTE / REMINDER / WORK) is defined in the data layer but not yet exposed in the UI — this is an acknowledged gap, not a bug.
