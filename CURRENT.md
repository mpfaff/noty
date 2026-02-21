# CURRENT.md

## Currently Working On

Nothing specific — actively idle after the Compose UI migration.

## Recently Completed

- Migrated entire UI from XML/RecyclerView/Adapters to Jetpack Compose (Feb 2026)
- Added note editing support via pre-filled bottom sheet
- Added per-note sticky toggle (controls notification dismissibility)
- Added Quick Settings tile for fast note creation
- Added haptic feedback throughout the UI

## Next Steps

- Expand test coverage (NotyViewModelTest needs proper coroutine/Room test setup; Compose UI tests)
- Decide on `NoteType` enum — either surface it in the UI (type picker in `NoteBottomSheet`) or remove it
- Consider scheduled reminders (`NoteType.REMINDER` + `AlarmManager`)
- Consider note search improvements (currently in-memory filtering only)

## Blockers / Notes

- `NoteType` enum (NOTE/REMINDER/WORK) exists in the DB and entity but is not shown in the UI — a note created via the UI always uses a hardcoded type
- Unit test coverage is minimal; `NotyViewModelTest.kt` is a placeholder
