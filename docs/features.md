# Features — Noty

📄 For architecture context, see /docs/design.md
📄 For full requirements, see /docs/requirements.md

---

## Core Note Management

| Feature | Status | Description |
|---------|--------|-------------|
| Create note | complete | Title (required) + description (optional) + sticky toggle. Saved to Room, immediately shows as a notification. |
| Edit note | complete | Bottom sheet pre-filled with existing data. Updates DB and refreshes the notification. |
| Delete note | complete | Confirmation dialog before deletion. Removes from DB and cancels the notification. |
| Note persistence | complete | Notes survive app close, process kill, and device reboot via Room + foreground service + BootReceiver. |
| Sticky toggle | complete | Per-note toggle. Sticky (default) = non-dismissible notification. Non-sticky = swipe-to-delete. |
| Note type field | in progress | `NoteType` enum (NOTE / REMINDER / WORK) exists in the DB entity but is not yet shown in the UI. Notes always receive a hardcoded type on creation. |

---

## Notifications

| Feature | Status | Description |
|---------|--------|-------------|
| Persistent notifications | complete | Each note maps to one persistent status bar notification, using the note's `id` as the notification ID. |
| Notification delete action | complete | Delete button inside the notification deletes the note directly without opening the app. |
| Sticky resurrection | complete | If a sticky notification is dismissed, `NotificationReceiver` immediately re-posts it. |
| Non-sticky swipe-to-delete | complete | Swiping a non-sticky notification sends `ACTION_DISMISSED` → note is deleted from DB. |
| Boot restoration | complete | `BootReceiver` starts `NotyService` on device boot; service syncs and restores all notifications. |
| Notification channels | complete | Two channels: persistent notes (`IMPORTANCE_DEFAULT`), foreground service (`IMPORTANCE_MIN`, hidden). |

📄 See /docs/notifications.md for full notification system details.

---

## UI & UX

| Feature | Status | Description |
|---------|--------|-------------|
| Material 3 design | complete | Full M3 color system, typography, and rounded shapes. |
| Dynamic color | complete | Uses system wallpaper colors on API 31+; static indigo/teal palette below. |
| Light / Dark / System theme | complete | Persisted via DataStore, applied via `AppCompatDelegate`. |
| Theme picker | complete | Bottom sheet with radio buttons for SYSTEM / LIGHT / DARK. |
| Search | complete | Filters notes by title or description in real-time (in-memory). |
| Empty state | complete | Icon + message when no notes exist or search has no results. |
| Haptic feedback | complete | Applied to taps, long presses, confirmations, and validation errors. |
| Splash screen | complete | Animated launcher icon on startup via `core-splashscreen`. |
| Edge-to-edge rendering | complete | Full bleed layout. |
| Note dropdown menu | complete | Three-dot button on each `NoteCard` → Edit / Delete. |
| Long press context menu | complete | Long press a note → same options as the dropdown. |

---

## System Integration

| Feature | Status | Description |
|---------|--------|-------------|
| Quick Settings tile | complete | Tile in notification shade → opens app and triggers the add-note sheet. |
| Foreground service | complete | `NotyService` keeps the process alive; auto-starts/stops based on note count. |

---

## Planned / Not Yet Implemented

| Feature | Status | Notes |
|---------|--------|-------|
| Note type UI | planned | `NoteType` is in the DB but has no picker in `NoteBottomSheet`. |
| Reminder scheduling | planned | `NoteType.REMINDER` exists but there is no `AlarmManager` / scheduling integration. |
| Tags / categories | planned | Notes are flat; no tagging system. |
| Cloud backup / sync | planned | Entirely local; no export or sync mechanism. |
| Note reordering | planned | Currently sorted by timestamp DESC only. |
| Home screen widget | planned | No widget implementation. |
| Export / share | planned | No user-facing export or share feature. |
| Full-text search | planned | Current search is in-memory string matching; no Room FTS optimization. |
