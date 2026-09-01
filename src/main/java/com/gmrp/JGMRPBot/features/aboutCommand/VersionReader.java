/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.gmrp.JGMRPBot.features.aboutCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class VersionReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(VersionReader.class);

	public static String getVersion() {
		Properties properties = new Properties();
		try (InputStream is = VersionReader.class.getClassLoader().getResourceAsStream("version.properties")) {
			if (is != null) {
				properties.load(is);
				return properties.getProperty("version");
			}
		} catch (Exception e) {
			LOGGER.error("Failed to read version.properties", e);
		}
		return "Unknown";
	}
}