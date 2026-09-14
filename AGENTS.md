# AGENTS.md

## Project overview

JGMRPBot is a JDA-based Discord bot (Java 26, built with Maven) for the
GeoFS Military Roleplay Discord community. It follows a lightweight
Model/View/Controller split: `core/` holds shared infrastructure (config,
database, Git info), `features/` holds Controllers + Views for each
command or event listener, and `views/shared/` holds common embed
styling. See `docs/ARCHITECTURE.md` for the full design and the steps
for adding a new slash command or event listener.

## Project structure

- `src/main/java/com/GMRP/` - application code (`core/`, `features/`, `views/`)
- `src/main/resources/` - `logback.xml`, `version.properties`, `db/schema.sql`, `db/seed.sql`
- `src/test/java/com/GMRP/` - JUnit 5 + Mockito tests, mirrors `src/main/java`
- `config/` - `checkstyle.xml` (lint rules), `formatter.xml` (Eclipse formatter used by Spotless)
- `docs/` - `ARCHITECTURE.md`, `CONTRIBUTING.md`, `SECURITY.md`

## Setup & build

```bash
mvn clean install      # installs deps, formats-check, lints, tests, compiles
mvn clean package      # builds target/JGMRPBot-x.x.x-jar-with-dependencies.jar
```

Requires Java 26+ and Maven. The bot needs a `TOKEN` env var and a
configured database (SQLite for local dev, Oracle for production) —
see `.env.example` and the README's "Setting up the Local Environment"
section. This project does not use a `.env` loader; these must be real
environment variables.

## Testing

```bash
mvn clean test                                 # full suite
mvn test -Dtest=ClassName#methodName           # single test
mvn spotless:check                             # formatting check (also runs at `validate`)
mvn checkstyle:check                           # lint (also runs at `validate`)
```

- `mvn clean install` runs Spotless check and Checkstyle at the `validate`
  phase before compiling — a formatting or naming violation fails the
  build before tests even run.
- Run the full suite before committing. All tests must pass.
- While iterating, run the single test closest to your change.
- Never delete, weaken, or rewrite a test to make a change pass.
- Do not claim that an interrupted or timed-out run passed.

## Code style

- Formatter: Eclipse formatter via Spotless (`config/formatter.xml`) —
  tabs, 4-space-equivalent indentation, 120-char line splits. Run
  `mvn spotless:apply` to auto-format; do not hand-format.
- Linter: Checkstyle (`config/checkstyle.xml`). Classes/interfaces are
  PascalCase, methods and local variables are camelCase, constants are
  `ALL_CAPS_WITH_UNDERSCORES`. No unused/redundant imports, no empty
  catch blocks, `switch` statements need a `default` case.
- Follow the MVC pattern already in place: Controllers implement
  `ISlashCommandController` or `IEventListenerController`, receive
  dependencies via constructor injection, and stay thin — business
  logic and Discord I/O in the controller, presentation in the View.
- Logging: SLF4J + Logback only. Never use `System.out.println()`,
  `System.err.println()`, or `e.printStackTrace()`. Use the correct
  level (TRACE/DEBUG/INFO/WARN/ERROR) and clear MDC context in a
  `finally` block when set.
- Comments should explain design decisions, not restate the code.
- Do not reformat code you are not otherwise changing.

## Git workflow

- Branch from `develop`; all PRs target `develop` (PRs targeting `main`
  are closed immediately — `main` is release-only).
- Use a descriptive branch per issue (e.g. `feat/new-command`); don't
  commit directly to `main`.
- Reference the linked issue in the PR (e.g. "Fixes #123").
- Never commit, push, or open a PR unless asked.
- All CI checks must pass before merge (`mvn clean test` on GitHub Actions).
- If a change is AI-assisted, say so in the PR description, explain
  what the change does and why in your own words, and describe how it
  was verified — per `docs/CONTRIBUTING.md`.

## Boundaries

- Do not modify unrelated files or widen scope beyond the request.
- Do not add dependencies without asking.
- Never commit secrets, API keys, or `.env` files (real credentials are
  environment variables only, never checked in — see `.env.example`).
- This project is licensed AGPL-3.0-or-later; contributions are
  distributed under that license.
- If a command fails, report the failure. Do not guess or present
  assumptions as confirmed results.
