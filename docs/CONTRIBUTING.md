<!--
Copyright (C) 2026 NickFury001
SPDX-License-Identifier: AGPL-3.0-or-later
-->
# Contributing

Thank you for considering contributing to the JGMRPBot repo. Contributions are always welcome!

## Reporting Bugs and Feature Requests

If you find a bug or want to suggest a new feature, please [open an issue](https://github.com/Geo-MRP/JGMRPBot/issues/new/choose).

Before opening a new issue:
- Search existing issues (open and closed) to avoid duplicates.
- Use the provided templates when available.
- Include a clear title, steps to reproduce (for bugs), expected vs actual behavior, and any relevant environment details or logs.

Incomplete or duplicate issues may be closed.

> [!IMPORTANT]
> This project is licensed under the **GNU AGPL-3.0-or-later**. By submitting a Pull Request, you agree that your contributions will be distributed under this exact license.

## Setup for contribution

JGMRPBot uses Maven to make setup incredibly simple and straightforward.

1. Create an issue for the bug you want to fix or the feature that you want to add.
2. Create your own fork on GitHub, then clone your fork.
3. Set up and configure your local environment by following the instructions in the [README](../README.md).
4. Write your code in your local copy. You must create a new branch for each new issue you work on (e.g., `feat/new-command`). Do not commit directly to your `main` branch.
5. Format your code by running `mvn spotless:apply`. This will automatically enforce our 4-space tab indentation and organize imports.
6. Run the test suite and the Checkstyle linter by running `mvn clean install`. If Checkstyle lists any structural or naming convention errors, you must fix them before committing.
7. If the build succeeds, commit your changes to your branch and create a pull request. Make sure to reference your issue from the pull request comments by including the issue number (e.g., "Fixes #123").

## About Pull Requests
Pull requests must be made to the `develop` branch. This is due to the fact that the `main` branch is used for releases, and the `develop` branch is used for development.

In fact, the `develop` branch is the branch hooked to the Beta testing server and bot. If you want to watch your accepted Pull Requests be tested on the Beta testing server, you can join the [GMRP server](https://discord.com/invite/updVrRXm4P), and [learn more](https://discord.com/channels/1097877635645849620/1256654929481830532/1460326591870537770).

## Regarding AI-Assisted Contributions
AI-assisted development is welcomed and may be used for research, reverse engineering, code generation, or documentation.

However, contributors are expected to fully understand every line of code they submit. By opening a Pull Request, you confirm that you are able to explain, modify, debug, and maintain the submitted code without relying on the AI that generated it.

When submitting an AI-assisted PR:
* Mention that the code is AI-generated in the pull request description.
* Clearly explain **what the change does, why it is needed**, and **what problem it solves**, using your own words.
* Describe **how you verified the change**, including the test cases used.
* **Ensure all logging follows the guidelines below** – AI-generated logs are often excessive or use the wrong log level. Review and adjust them before submitting.
* Comments should document design decisions or implementation details in your own words. Avoid generic AI-generated comments that merely restate what the code already does.
* Be prepared to answer review questions about the implementation. "The AI generated it" is not considered a sufficient explanation.
* Large AI-generated changes without a clear understanding of the implementation are unlikely to be accepted.
* If the implementation cannot be reasonably explained during code review, the pull request may be rejected regardless of whether it works.
  The quality, correctness, maintainability, and long-term ownership of the submitted code remain the responsibility of the contributor.

## Logging Guidelines

JGMRPBot uses **SLF4J with Logback** for structured, production-ready logging. All code contributions must follow these logging practices:

### Core Principles

- **Never use** `System.out.println()`, `System.err.println()`, or `e.printStackTrace()` in production code. All logging must go through the SLF4J `Logger`.
- **Use the appropriate log level** for every message:
  - `TRACE` – Extremely detailed debugging, typically disabled in production
  - `DEBUG` – Useful development information, enabled during local testing
  - `INFO` – Important lifecycle events (bot startup, command execution, successful operations)
  - `WARN` – Recoverable issues that don't stop the bot (missing config values, permission failures)
  - `ERROR` – Critical problems that need investigation (database connection failures, exceptions that disrupt functionality)
- **Include relevant context** using MDC (Mapped Diagnostic Context) when handling Discord events:
  - Add `userId`, `guildId`, `channelId`, and the `command` or `event` name
  - **Always clear MDC** in a `finally` block to prevent context from leaking to other log entries
- **Avoid excessive logging** in performance-critical code paths or tight loops. Log only what provides meaningful diagnostic value.

### Configuration

Logging behavior can be controlled at runtime:
- **Log level**: Set the `LOG_LEVEL` environment variable (e.g., `export LOG_LEVEL=DEBUG`) or JVM property (e.g., `-Dlog.level=DEBUG`). Defaults to `INFO`.
- **Log output**: The bot writes to both the console (for local development) and rotating log files in the `logs/` directory (20MB per file, 14 days retention).
- **Structured output**: An optional JSON appender is available for log aggregation services (commented out in `logback.xml`; enable by adding the `logstash-logback-encoder` dependency).