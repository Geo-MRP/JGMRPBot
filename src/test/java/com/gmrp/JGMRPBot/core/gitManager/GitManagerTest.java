package com.gmrp.JGMRPBot.core.gitManager;

import com.gmrp.JGMRPBot.core.gitManager.exception.GitManagerException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GitManagerTest {

	/**
	 * Tests for the getCurrentBranch method in the GitManager class.
	 * <p>
	 * The getCurrentBranch method is responsible for returning the name of the
	 * branch the code is from. In case of exceptions, it returns "Unknown Branch".
	 */

	@Test
	void testGetCurrentBranch_ReturnsCorrectBranchName() throws Exception {
		// Arrange
		String fakeFileContent = "git.branch=main";
		InputStream fakeStream = new ByteArrayInputStream(fakeFileContent.getBytes());

		// Act
		GitManager gitManager = new GitManager(fakeStream);
		String result = gitManager.getCurrentBranch();

		// Assert
		assertEquals("main", result);
	}

	@Test
	void testGetCurrentBranch_ThrowsGitManagerExceptionOnError() {
		// Arrange
		InputStream faultyStream = new InputStream() {
			@Override
			public int read() throws IOException {
				throw new IOException("Simulated read error");
			}
		};
		// Act &
		// Assert
		assertThrows(GitManagerException.class, () -> {
			new GitManager(faultyStream);
		});
	}

	@Test
	void testGetCurrentBranch_ReturnsUnknownBranch_OnNullInputStream() throws Exception {
		// Arrange
		InputStream inputStream = null;

		// Act
		GitManager gitManager = new GitManager(inputStream);

		// Assert
		assertEquals("Unknown Branch", gitManager.getCurrentBranch());
	}

	@Test
	void testGetCurrentBranch_ReturnsUnknownBranch_OnMissingProperty() throws Exception {
		// Arrange
		InputStream inputStream = new ByteArrayInputStream("".getBytes());

		// Act
		GitManager gitManager = new GitManager(inputStream);

		// Assert
		assertEquals("Unknown Branch", gitManager.getCurrentBranch());
	}
}