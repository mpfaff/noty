# System Design — Noty

📄 For project overview and rules, see /docs/agent.md

---

## Architecture Pattern

**MVVM + Repository**, with reactive data flow from Room through the ViewModel to Compose UI.

### Layer Responsibilities

| Layer | Classes | Responsibility |
|-------|---------|----------------|
| UI | `NotyApp.kt`, `MainActivity.kt` | Compose composables, user interaction, state rendering |
| ViewModel | `NotyViewModel`, `NotyViewModelFactory` | UI state, service lifecycle, notification sync |
| Repository | `NotyRepository` | Abstracts DAO, handles errors, returns typed results |
| Data | `NoteDao`, `AppDatabase`, `Note` | SQLite via Room; reactive Flow queries |
| Utils | `NotificationHelper`, `NotyService`, receivers | Background persistence, notifications, system integration |

---

## Data Flow

### Read Path (reactive)

```
Room: NoteDao.getAllNotes()
  → Flow<List<Note>>
  → NotyRepository.getAllNotes()
  → ViewModel.allNotes (LiveData, converted via asLiveData())
  → NotyApp composable via observeAsState()
  → recompose on every change
```

### Write Path — Note Creation

```
User taps Save in NoteBottomSheet
  → NotyApp calls viewModel.insert(note)
  → viewModelScope coroutine launches
  → repository.insert(note)
  → NoteDao.insertNote(note) → returns id: Long
  → NoteDao.getNoteById(id) → full Note with generated id
  → NotificationHelper.showNotification(note)
  → Room Flow emits updated list → UI recomposes
  → If first note: NotyService starts
```

### Write Path — Note Deletion

```
User confirms delete
  → viewModel.delete(note)
  → repository.delete(note)
  → NoteDao.deleteNoteById(id)
  → NotificationHelper.cancelNotification(note.id)
  → Room Flow emits updated list → UI recomposes
  → If list now empty: NotyService stops
```

### Notification Dismiss Path (sticky)

```
User attempts to swipe notification (sticky = true)
  → setOngoing(true) prevents swipe — but if dismissed via other means:
  → NotificationReceiver.onReceive(ACTION_DISMISSED)
  → note.isSticky == true → NotificationHelper.showNotification(note) [resurrection]
```

### Notification Dismiss Path (non-sticky)

```
User swipes notification (sticky = false)
  → NotificationReceiver.onReceive(ACTION_DISMISSED)
  → note.isSticky == false → repository.deleteById(id)
  → Room Flow updates → UI recomposes
```

📄 Need more detail on notification behavior? See /docs/notifications.md

---

## Component Interaction Map

```
┌────────────────┐   observes LiveData   ┌─────────────────┐
│  Compose UI    │◄──────────────────────│  NotyViewModel  │
│  (NotyApp.kt)  │──── user actions ────►│                 │
└────────────────┘                       │  - allNotes     │
                                         │  - themeFlow    │
┌────────────────┐                       │  - insert()     │
│ MainActivity   │──── tile / intent ───►│  - update()     │
└────────────────┘                       │  - delete()     │
                                         └────────┬────────┘
                                                  │ delegates
                                         ┌────────▼────────┐
                                         │ NotyRepository  │
                                         └────────┬────────┘
                                                  │
                                         ┌────────▼────────┐
                                         │    NoteDao      │
                                         │  (Room + KSP)   │
                                         └────────┬────────┘
                                                  │
                                         ┌────────▼────────┐
                                         │  AppDatabase    │
                                         │  (SQLite v4)    │
                                         └─────────────────┘

Background / System:

┌──────────────┐   starts / stops   ┌──────────────────┐
│ NotyViewModel│───────────────────►│   NotyService    │
└──────────────┘                    │  (ForegroundSvc) │
                                    └──────────────────┘

┌────────────────────┐  broadcasts  ┌──────────────────────┐
│  NotificationHelper│◄─────────────│  NotificationReceiver│
│  (shows/cancels)   │              │  (delete / dismiss)  │
└────────────────────┘              └──────────────────────┘

┌─────────────────────┐  ACTION_ADD_NOTE  ┌──────────────────┐
│ QuickNoteTileService│─────────────────►│  MainActivity    │
└─────────────────────┘                  └──────────────────┘

┌─────────────────┐  BOOT_COMPLETED  ┌──────────────────┐
│  BootReceiver   │─────────────────►│   NotyService    │
└─────────────────┘                  └──────────────────┘
```

---

## Database Schema

**Table: `notes`** — Room entity: `Note.kt`

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | INTEGER | PRIMARY KEY autoincrement | Used as notification ID |
| `title` | TEXT | NOT NULL | Required; shown in notification |
| `description` | TEXT | nullable | Optional; shown in notification body |
| `type` | TEXT | NOT NULL | `NoteType` enum stored as string |
| `timestamp` | INTEGER | NOT NULL | Epoch milliseconds; used for ordering |
| `isSticky` | INTEGER | NOT NULL, DEFAULT 1 | Boolean (1=sticky, 0=non-sticky) |

**Queries (`NoteDao`):**
- `getAllNotes(): Flow<List<Note>>` — ORDER BY timestamp DESC
- `insertNote(note): Long` — returns auto-generated id
- `updateNote(note)` — full object update by id
- `deleteNoteById(id)` — delete by primary key
- `getNoteById(id): Note?` — single-row lookup

**Database version:** 4
**Migration 3→4:** Adds `isSticky INTEGER NOT NULL DEFAULT 1` column
**Debug mode:** `fallbackToDestructiveMigration()` enabled — data is wiped on schema mismatch

**`NoteType` enum values:** `NOTE`, `REMINDER`, `WORK`
> Note: the enum exists in the DB layer but is not yet surfaced in the UI.

---

## Theme System

1. `ThemeManager` stores preference in `DataStore` (key: `theme_preference`)
2. `NotyApplication` collects `themeFlow` at startup → calls `AppCompatDelegate.setDefaultNightMode()`
3. Compose `NotyTheme` composable reads `isSystemInDarkTheme()`, which respects the delegate setting
4. `ThemeSelectionSheet` lets the user pick SYSTEM/LIGHT/DARK → calls `viewModel.setTheme()`
5. On API 31+: Material 3 dynamic color (derived from system wallpaper)
6. Below API 31: static indigo/teal palette

**ThemeMode enum:** `SYSTEM`, `LIGHT`, `DARK`

---

## Service Lifecycle

```
Notes exist? + service not running  →  startForegroundService(NotyService)
No notes?   + service running       →  stopService(NotyService)

isServiceRunning flag prevents redundant start/stop calls.

onTaskRemoved (app swiped from recents):
  → NotyService.onTaskRemoved()
  → Re-syncs all notifications
  → Posts a PendingIntent to restart itself
```

📄 Need more detail on how notifications work inside the service? See /docs/notifications.md

---

## Compose UI Structure

All Compose code lives in `NotyApp.kt`:

```
NotyTheme
  └─ NotyApp (root composable)
        ├─ Scaffold
        │   ├─ LargeTopAppBar  (title + theme icon button)
        │   ├─ content: SearchBar + LazyColumn (NoteCard × n) or EmptyStateContent
        │   └─ FAB: ExtendedFloatingActionButton (Add Note)
        ├─ NoteBottomSheet      (add / edit)
        ├─ ThemeSelectionSheet
        └─ AlertDialog          (delete confirmation)
```

**State managed in `NotyApp`:**
- `showAddSheet: Boolean`
- `noteToEdit: Note?`
- `noteToDelete: Note?`
- `showThemeSheet: Boolean`
- `searchQuery: String`
- `searchActive: Boolean`

**Quick Settings tile integration:**
- `MainActivity` exposes `addNoteFromTile` as a `mutableStateOf(false)`
- `QuickNoteTileService` sends `ACTION_ADD_NOTE` → `onNewIntent` sets `addNoteFromTile = true`
- `NotyApp` observes this via a callback and opens the add sheet via `LaunchedEffect`
