<!--
Copyright (C) 2026 NickFury001
SPDX-License-Identifier: AGPL-3.0-or-later
-->
# Java GeoFS Military Roleplay Bot

<p align="center">
  <img src="./src/main/resources/assets/images/logo.png" width=30% height=30% />
</p>

<p align="center">
  An open-source Java port of the original closed-source python-based GMRP Discord Bot.
</p>

---
<p align="center">
  <a href="#support">
    <img src="https://img.shields.io/badge/Support-PayPal%20%26%20Ko%2D%2DFi-EA4AAA?style=for-the-badge&logo=githubsponsors&logoColor=white" alt="Support GMRP">
  </a>
</p>

---

## Info

The goal of this open-source initiative is to ensure the long-term development, maintenance, and survivability of technology initially made by [ghost_of_denver](https://discord.com/users/1510279232201298022) for the [GeoFS Military Roleplay Community](https://discord.com/invite/updVrRXm4P) on Discord.

This repository aims to cover the Discord bot behavior of GMRPBot. That is, it's going to be a Discord bot with multiple different Commands and Loops, among other things, to serve the GMRP Community.

## Using JGMRPBot
### Initial Setup

Before we get started, you'll need to set up your local environment and get the code from the repository.
If you don't have [Java 26 or later](https://www.oracle.com/java/technologies/downloads/) and [Maven](https://maven.apache.org/install.html) installed, install those first. 

Clone the repository:
```bash
git clone https://github.com/Geo-MRP/JGMRPBot.git
```
Navigate to the directory and install the dependencies:
```bash
cd JGMRPBot
mvn clean install
```

### Setting up the Local Environment
To start off, you'll need to create a new Discord bot and invite it to your server. You can find instructions on how to do that [here](https://docs.discord.com/developers/quick-start/getting-started#step-1-creating-an-app). Once your bot is set up, configure the environment variables to have the bot's token:
```bash
export TOKEN=<your_bot_token>
```

This repository uses SQL to store data and communicate with external services. You'll have two options:
- Use a local SQLite3 database (recommended). External services won't be available, but we'll have the DB schema and seed data available in src/main/resources/db/, which you can use to set up your local SQLite3 database.
- Use a remote database (not recommended). This is intended for the actual release bot, which is hooked up to a paid Oracle SQL database, which is connected to multiple services which are not open-source.

To use a local SQLite3 database, create a .db file, apply the schema, and seed the data:
```bash
touch data/database.db
sqlite3 data/database.db < src/main/resources/db/schema.sql
sqlite3 data/database.db < src/main/resources/db/seed.sql
```
And finally configure the Environment variables to point to that SQLite DB:
```bash
export DB_TYPE=SQLite
export DB_SQLITE_PATH=data/database.db
```

To use a remote database, configure the Environment variables to point to the Oracle SQL database:
```bash
export DB_TYPE=Oracle
export DB_USER=<your_db_username>
export DB_PASSWORD=<your_db_password>
export DB_CONNECTION_STRING=<your_oracle_connection_string>
```

### Running the Bot

Once the SQL DB is configured, you can simply build the jar with dependencies:
```bash
mvn clean package
```

Finally, to run the bot, run the jar with dependencies:
```bash
java -jar target/JGMRPBot-x.x.x-jar-with-dependencies.jar
```

## Contributing
Before opening an issue or pull request, please read the contribution guidelines:

**[CONTRIBUTING.md](docs/CONTRIBUTING.md)**

The guide covers:
- Coding style and formatting
- AI-assisted contributions
- Pull request expectations
- Testing guidelines

For an overview of the codebase structure, see **[ARCHITECTURE.md](docs/ARCHITECTURE.md)**.


## Security

We take the security of this project seriously. If you discover a security vulnerability, please do not report it in the public issues tracker. Instead, refer to our [Security Policy](docs/SECURITY.md) for instructions on how to properly and responsibly report vulnerabilities.

## Support
Support Geo-MRP via PayPal or Ko-Fi. Every contribution helps fund ongoing development, long-term maintenance, and server hosting costs. PayPal is the preferred way to support the project.

[![PayPal](https://img.shields.io/badge/PayPal-00457C?style=for-the-badge&logo=paypal&logoColor=white)](https://paypal.me/GMRPTech)
[![Ko-fi](https://img.shields.io/badge/Ko--fi-F16061?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/GMRP)

Supporters get special perks in the GMRP Community depending on the amount donated. [Join the server](https://discord.com/invite/updVrRXm4P) to [learn more](https://discord.com/channels/1097877635645849620/1256654929481830532/1460326591870537770).

## License

JGMRPBot - Main Discord Bot for GMRP
Copyright (C) 2026 NickFury001

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.