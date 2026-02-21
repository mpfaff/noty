# Notification System — Noty

📄 For architecture overview, see /docs/agent.md
📄 For full feature context, see /docs/features.md

---

## Overview

Every note in Noty maps 1:1 to a persistent Android notification. The notification system is managed by `NotificationHelper.kt`, coordinated by `NotyViewModel`, and kept alive by `NotyService`. The `NotificationReceiver` handles user actions on notifications.

---

## Notification Channels

| Channel ID | Importance | Visibility | Purpose |
|------------|-----------|------------|---------|
| `noty_persistent_channel` | DEFAULT | Public | User-visible persistent note notifications |
| `noty_service_channel` | MIN | Secret | Required foreground service notification (not visible to user by default) |

Channels are created in `NotificationHelper.createNotificationChannels()`, called from `NotyApplication.onCreate()`.

---

## Notification Anatomy

Each note notification includes:

- **Small icon:** `ic_stat_noty` (monochrome; shown in the status bar)
- **Large icon:** `ic_notification_large` (bitmap; shown in the expanded notification)
- **Title:** `note.title`
- **Text:** `note.description` (empty if null)
- **Timestamp:** `note.timestamp` (shown as relative time)
- **`setOngoing(note.isSticky)`** — prevents user swipe-dismissal when `true`
- **`setAutoCancel(!note.isSticky)`** — allows cancel-on-tap when `true` (non-sticky only)
- **Delete action button** → fires `ACTION_DELETE` broadcast
- **Dismiss PendingIntent** → fires `ACTION_DISMISSED` broadcast (behavior differs by sticky flag)
- **Content PendingIntent** → opens `MainActivity`

**Notification ID:** `note.id.toInt()` — unique and stable per note.

---

## Sticky vs Non-Sticky Behavior

| Behavior | Sticky (`isSticky = true`) | Non-Sticky (`isSticky = false`) |
|----------|---------------------------|----------------------------------|
| `setOngoing()` | `true` — cannot be swiped away | `false` |
| `setAutoCancel()` | `false` | `true` — dismissed on tap |
| On swipe / dismiss | `NotificationReceiver` immediately re-posts the notification | `NotificationReceiver` deletes the note from DB |
| Default for new notes | Yes | No |

### Sticky Resurrection Flow

```
User dismisses sticky notification (e.g. via "Clear all")
  → ACTION_DISMISSED PendingIntent fires
  → NotificationReceiver.onReceive(ACTION_DISMISSED)
  → note.isSticky == true
  → NotificationHelper.showNotification(note) called immediately
  → Notification reappears
```

### Non-Sticky Dismiss Flow

```
User swipes non-sticky notification
  → ACTION_DISMISSED PendingIntent fires
  → NotificationReceiver.onReceive(ACTION_DISMISSED)
  → note.isSticky == false
  → repository.deleteById(note.id)
  → Room Flow emits updated list → UI recomposes
```

---

## PendingIntent ID Scheme

To avoid collisions between multiple notes, all `PendingIntent` request codes are offset by note ID:

| Intent Type | Request Code |
|-------------|-------------|
| Content (open app) | `note.id.toInt()` |
| Delete action | `note.id.toInt() + 1000` |
| Dismiss | `note.id.toInt() + 2000` |

---

## Notification Broadcast Actions

`NotificationReceiver` handles two action strings:

| Action | Effect |
|--------|--------|
| `ACTION_DELETE` (delete button tapped) | Calls `repository.deleteById(id)`, cancels the notification |
| `ACTION_DISMISSED` (notification dismissed) | Resurrects if sticky; deletes note if non-sticky |

Both actions carry `EXTRA_NOTE_ID` (Long) as an intent extra. Both use `goAsync()` to safely run coroutines inside the BroadcastReceiver.

---

## Notification Sync

### On App Start
`NotyViewModel.init` calls `syncNotifications()`:
- Queries `notificationManager.activeNotifications`
- For each note in DB: if no active notification exists for that note → calls `showNotification(note)`
- Avoids creating duplicate notifications

### On Service Start
`NotyService.onStartCommand()` also calls `syncNotifications()` for the same reason — the service may start before the ViewModel init completes.

### On `onTaskRemoved` (app swiped from Recents)
`NotyService.onTaskRemoved()`:
- Re-syncs all notifications
- Posts a delayed `PendingIntent` to restart the service, ensuring it survives process termination

---

## Foreground Service Notification

`NotyService` must post a foreground notification to satisfy Android's foreground service requirements.

- **Notification ID:** `Int.MAX_VALUE` (2147483647) — avoids collision with note IDs (which start at 1)
- **Channel:** `noty_service_channel` (IMPORTANCE_MIN — hidden from user by default)
- **Priority:** `PRIORITY_MIN`

---

## Service Lifecycle (managed by NotyViewModel)

```
Room Flow emits updated notes list
  ↓
ViewModel observes
  ↓
notes.isNotEmpty() && !isServiceRunning  →  startForegroundService(NotyService)
notes.isEmpty()    &&  isServiceRunning  →  stopService(NotyService)
```

`isServiceRunning` is a boolean flag on `NotyViewModel` that prevents redundant start/stop calls when the note list emits the same state multiple times.

---

## Boot Restoration

`BootReceiver` receives `android.intent.action.BOOT_COMPLETED`:
1. Starts `NotyService`
2. `NotyService.onStartCommand()` calls `syncNotifications()`
3. All notes in the Room DB get their notifications restored
