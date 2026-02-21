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

Before starting any work, read /docs/agent.md and follow all rules and instructions defined there.
When starting a new task, re-consult /docs/agent.md to ensure you are following the correct conventions.
If uncertain about any rule or convention, re-read /docs/agent.md before proceeding.

📄 For agent instructions, documentation system, and all project details — see /docs/agent.md
