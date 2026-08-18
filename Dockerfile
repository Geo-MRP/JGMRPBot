# syntax=docker/dockerfile:1

# -----------------------------------------------------------------------------
# Stage 1: Build
# -----------------------------------------------------------------------------
FROM eclipse-temurin:26-jdk AS build

WORKDIR /app

# Install Maven
RUN apt-get update && \
    apt-get install -y --no-install-recommends maven && \
    rm -rf /var/lib/apt/lists/*

# Copy only the files needed to resolve dependencies first (better layer caching)
COPY pom.xml .
COPY config/ config/

# Download dependencies (this layer is cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy the rest of the source
COPY src/ src/

# Build the fat JAR (skip tests for faster image builds; remove -DskipTests if you want them)
RUN mvn clean package -DskipTests -B

# -----------------------------------------------------------------------------
# Stage 2: Runtime
# -----------------------------------------------------------------------------
FROM eclipse-temurin:26-jre

# Create a non-root user for security
RUN groupadd --system bot && \
    useradd --system --gid bot --home-dir /app --shell /sbin/nologin bot

WORKDIR /app

# Copy the fat JAR from the build stage
# Version comes from pom.xml (currently 1.0.2-SNAPSHOT)
COPY --from=build /app/target/JGMRPBot-*-jar-with-dependencies.jar /app/jgmrbot.jar

# Create the data directory (for SQLite) and give ownership to the bot user
RUN mkdir -p /app/data && chown -R bot:bot /app

USER bot

# Environment variables (override at runtime)
# Required:
#   TOKEN                  – Discord bot token
# Optional / recommended for local use:
#   DB_TYPE=SQLite
#   DB_SQLITE_PATH=/app/data/database.db
# For Oracle:
#   DB_TYPE=Oracle
#   DB_USER=...
#   DB_PASSWORD=...
#   DB_CONNECT_STRING=...

ENV DB_TYPE=SQLite \
    DB_SQLITE_PATH=/app/data/database.db

# The bot does not expose any HTTP ports by default
# EXPOSE is omitted intentionally

ENTRYPOINT ["java", "-jar", "/app/jgmrbot.jar"]