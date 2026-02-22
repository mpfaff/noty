# CLAUDE.md

Claude-specific behavior for the Noty project.

## Behavior Notes

- The codebase was migrated to Jetpack Compose in February 2026. All UI is in Compose — do not add XML layouts, adapters, or RecyclerView code.
- Database uses `fallbackToDestructiveMigration()` in debug — do not rely on data persistence during development.
- When adding new schema fields, always write a Room migration. Never just increment the DB version without a migration.

## Critical Rules

These rules must never be violated regardless of context:

- **Compose only** — All UI must be written in Jetpack Compose. Do not add XML layouts, RecyclerView adapters, or ViewBinding code.
- **Room migrations required** — Any schema change must include an explicit Room migration object. Never just bump the database version without one.
- **Service lifecycle** — `NotyService` is managed exclusively by `NotyViewModel`. Do not start or stop it from anywhere else.
- **Notifications via helper** — All notification operations must go through `NotificationHelper`. Do not create or cancel notifications directly.

ALWAYS read /docs/agent.md at session start and follow all rules defined there.
ALWAYS re-check the relevant section in /docs/agent.md before working on a new feature or making an architectural decision.
