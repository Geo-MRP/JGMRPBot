/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.gmrp.JGMRPBot.core.gitManager;

import com.gmrp.JGMRPBot.core.gitManager.exception.GitManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;
import java.io.IOException;

public class GitManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(GitManager.class);

	private final Properties gitProperties = new Properties();
	private static final String BRANCH_UNKNOWN = "Unknown Branch";

	public GitManager() throws GitManagerException {
		this(GitManager.class.getResourceAsStream("git.properties"));
	}

	GitManager(InputStream inputStream) throws GitManagerException {
		loadGitProperties(inputStream);
	}

	/**
	 * Load git.properties from the classpath
	 */
	private void loadGitProperties(InputStream inputStream) throws GitManagerException {
		try (InputStream stream = inputStream) {
			if (stream != null)
				gitProperties.load(stream);
		} catch (IOException e) {
			LOGGER.error("Failed to load git.properties", e);
			throw new GitManagerException("Failed to load git.properties", e);
		}
	}

	/**
	 * Simple current branch getter
	 * 
	 * @return Returns a string containing the branch this GitManager Object's
	 *         repository is currently in. e.g. "origin/main"
	 */
	public String getCurrentBranch() {
		return gitProperties.getProperty("git.branch", BRANCH_UNKNOWN);
	}
}