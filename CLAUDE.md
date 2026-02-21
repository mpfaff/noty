# CLAUDE.md

Claude-specific behavior for the Noty project.

## Behavior Notes

- The codebase was migrated to Jetpack Compose in February 2026. All UI is in Compose — do not add XML layouts, adapters, or RecyclerView code.
- Database uses `fallbackToDestructiveMigration()` in debug — do not rely on data persistence during development.
- When adding new schema fields, always write a Room migration. Never just increment the DB version without a migration.

📄 For agent instructions, the documentation system, and all project details — see /docs/agent.md
