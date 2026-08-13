/* SPDX-License-Identifier: AGPL-3.0-or-later */
/*
 Configuration table
 */
CREATE TABLE IF NOT EXISTS CONFIG (
    CONFIG_KEY   TEXT PRIMARY KEY,
    CONFIG_VALUE TEXT NOT NULL
);