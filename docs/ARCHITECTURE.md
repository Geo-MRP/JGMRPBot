<!--
Copyright (C) 2026 NickFury001
SPDX-License-Identifier: AGPL-3.0-or-later
-->

# Architecture

This document describes how JGMRPBot is structured so contributors can orient themselves quickly and add new features without fighting the existing design.

## High-level overview

JGMRPBot is a **JDA-based Discord bot** written in Java. It follows a lightweight **Model / View / Controller** separation:

- **Core** components (Models) provide shared infrastructure (config, database, Git info).
- **Views** format Discord embeds and messages.
- **Controllers** handle Discord events (slash commands, messages, etc.) and coordinate data access.
- **Main** sets up all the MVCs at startup.

The bot currently supports two kinds of feature modules:

| Type            | Interface | Purpose                   | Example           |
|-----------------|-----------|---------------------------|-------------------|
| Slash Commands  | `ISlashCommandController` | User-invoked `/commands`  | `/about`, `/help` |
| Event Listeners | `IEventListenerController` | Listen for Discord events | Bot-bait honeypot |

## Package structure
```
com.GMRP
├── Main.java                               # Sets up the MVCs
├── BotConfig.java                          # Singleton that reads ENV vars
├── core/                                   # Model
│   ├── databaseManager/                        # Database Core
│   │   ├── IDatabaseManager.java                   # Database Management Interface
│   │   ├── DatabaseManagerFactory.java             # Database Manager Factory
│   │   ├── DatabaseManagerOracle.java              # Oracle Database Implementation
│   │   ├── DatabaseManagerSQLite.java              # SQLite Database Implementation
│   │   └── exception/                              # Database exceptions
│   │       └── DatabaseManagerException.java       # Base exception
│   └── gitManager/                             # Git Core
│       └── GitManager.java                         # Git Management
├── features/                               # View + Controller
│   ├── IEventListenerController.java           # Interface for event listeners
│   ├── ISlashCommandController.java            # Interface for Slash Commands
│   ├── aboutCommand/                           # Bot Info Slash Command
│   │   ├── AboutCommandController.java             # Controller
│   │   ├── AboutEmbedView.java                     # View
│   │   └── VersionReader.java                      # Utility
│   ├── helpCommand/                            # Help Slash Command
│   │   ├── HelpCommandController.java              # Controller
│   │   └── HelpEmbedView.java                      # View
│   └── moderation/                             # Moderation Feature Group
│       └── botBait/                                # Bot Bait Honeypot Event Listener
│           ├── BotBaitEventController.java             # Controller
│           └── BotBaitView.java                        # View
└── views/                                  # Views
    └── shared/                                 # Shared Views
        └── BotEmbedBuilder.java                    # Shared Embed Styling
```

## Startup flow (`Main`)

1. `BotConfig.init()` – loads environment variables (token, DB settings, etc.).
2. Create Core Models:
    - `GitManager` (repository info)
    - `IDatabaseManager` (connection pool / SQLite)
        - With a shutdown hook to close the pool.
3. Instantiate feature modules (controllers + views).
4. Register every controller as a JDA event listener.
5. Build the JDA instance and wait until it is ready.
6. Set the bot avatar URL on `BotEmbedBuilder` (used for consistent footers).
7. Collect all `SlashCommandData` from the command controllers and register them on the target guild (guild ID comes from the database).

## Key components

### BotConfig

Singleton that reads configuration from environment variables:

| Variable | Purpose |
|----------|---------|
| `TOKEN` | Discord bot token |
| `DB_TYPE` | `sqlite` (default) or `oracle` |
| `DB_SQLITE_PATH` | Path to the local SQLite file |
| `DB_USER` / `DB_PASSWORD` / `DB_CONNECT_STRING` | Oracle credentials |

Most runtime values that used to live in environment variables (owner ID, server ID, bait channel) are now stored in the `CONFIG` table and queried at runtime.

### Database access (`IDatabaseManager`)

Provides a single entry point for database access:

- **`IDatabaseManager`** – interface used by the rest of the app (`getConfigKey()`, `testConnection()`, `close()`).
- **`DatabaseManagerOracle`** / **`DatabaseManagerSQLite`** – concrete DB implementations.
- **`DatabaseManagerFactory.create()`** – picks the implementation from `DB_TYPE` (Oracle vs SQLite).

Callers depend only on the interface. That means that new interface methods must be added to the interface and implemented by the concrete implementations.

Schema and seed data live in `src/main/resources/db/`.

### Feature modules

#### Slash commands

Implement `ISlashCommandController` (which extends JDA’s `EventListener`):

1. Provide `getCommandSetup()` – returns the `SlashCommandData` (name, description, options).
2. Override the appropriate JDA event method (usually `onSlashCommandInteraction`).
3. Guard on the command name so multiple listeners don’t fight each other.
4. Fetch any necessary data (DB, Git, etc.), build an embed via the View, and reply.

Controllers receive their dependencies (View, IDatabaseManager, GitManager, …) via constructor injection.

#### Event loops / moderation

Implement `IEventListenerController` (also an `EventListener`).  
These react to continuous events such as `MessageReceivedEvent`. The Bot-bait module is the current example: it watches a configured channel and bans accounts that post there (typical self-bot bait).

### Views & embeds

- Feature-specific views (e.g. `AboutEmbedView`) know how to format the data for that command.
- `BotEmbedBuilder` supplies the common styling (color, attribution footer with the bot avatar).
- Prefer creating embeds through the views rather than building them ad-hoc inside controllers.

## Adding a new slash command

1. Create a new package under `features/` (e.g. `features/pingCommand/`).
2. Add a `CommandController` that implements `ISlashCommandController` and extends `ListenerAdapter`.
3. Add a corresponding `EmbedView` (or reuse `BotEmbedBuilder` directly for very simple replies).
4. In `Main`:
    - Instantiate the view and controller.
    - Add the controller to the `slashCommands` list.
5. Register any new database keys in the seed data if the command needs configuration.
6. Write unit tests under `src/test/java/...` following the existing patterns.
7. Run `mvn spotless:apply` and `mvn clean install` before opening a PR.

## Adding a new event-driven feature

1. Create a package under the appropriate category (e.g. `features/moderation/...`).
2. Implement `IEventListenerController`.
3. Register the controller in the `eventListeners` list inside `Main`.
4. Follow the same testing and formatting rules as above.

## Design principles currently in use

- **Constructor injection** – Controllers receive their collaborators; they do not look them up statically (except for the BotConfig singleton, for now).
- **Thin controllers** – Business logic and Discord I/O stay in the controller; presentation stays in the view.
- **Database as source of truth for runtime config** – Owner, server, bait channel, etc. live in the `CONFIG` table rather than environment variables.
- **Fail fast at startup** – Missing critical config or a broken database connection should surface immediately.
- **Consistent embeds** – All user-facing embeds go through the shared builder so styling stays uniform.

## Future considerations

As the bot grows, the following areas are likely candidates for further structure:

- A proper command registry / automatic discovery instead of manual lists in `Main`.
- Shared service layer for common database queries.
- More comprehensive test coverage (especially for event controllers).
- Clearer separation between “core” infrastructure and domain features.

---

When in doubt, look at the existing `/about` and Bot-bait modules – they represent the current preferred patterns.

