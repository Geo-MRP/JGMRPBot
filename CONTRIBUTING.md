<!--
Copyright (C) 2026 NickFury001
SPDX-License-Identifier: AGPL-3.0-or-later
-->
# Contributing

Thank you for considering contributing to the JGMRPBot repo. Contributions are always welcome!

> [!IMPORTANT]
> This project is licensed under the **GNU AGPL-3.0-or-later**. By submitting a Pull Request, you agree that your contributions will be distributed under this exact license.

## Setup for contribution

JGMRPBot uses Maven to make setup incredibly simple and straightforward.

1. Create an issue for the bug you want to fix or the feature that you want to add.
2. Create your own fork on GitHub, then clone your fork.
3. Configure the necessary .env file.
4. Write your code in your local copy. It's good practice to create a branch for each new issue you work on, although not compulsory.
5. To run the test suite, first install the dependencies by running mvn clean install, then mvn test.
6. Ensure your code is linted by running `mvn spotless:apply` -- fix any issue you see listed.
7. If the tests pass, you can commit your changes to your fork and then create a pull request from there. Make sure to reference your issue from the pull request comments by including the issue number e.g. #123.

## About Pull Requests
Pull requests must be made to the `develop` branch. This is due to the fact that the `main` branch is used for releases, and the `develop` branch is used for development.

In fact, the `develop` branch is the branch hooked to the Beta testing server & bot. If you want to watch your accepted Pull Requests be tested on the Beta testing server, you can join the [GMRP server](https://discord.com/invite/updVrRXm4P), and [learn more](https://discord.com/channels/1097877635645849620/1256654929481830532/1460326591870537770).

