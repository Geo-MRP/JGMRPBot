/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.GMRP.features.aboutCommand;

import java.io.InputStream;
import java.util.Properties;

public class VersionReader {
	public static String getVersion() {
		Properties properties = new Properties();
		try (InputStream is = VersionReader.class.getClassLoader().getResourceAsStream("version.properties")) {
			if (is != null) {
				properties.load(is);
				return properties.getProperty("version");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Unknown";
	}
}