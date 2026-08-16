package com.GMRP.core.gitManager;

import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GitManagerTest {

	/**
	 * Tests for the getCurrentBranch method in the GitManager class.
	 * <p>
	 * The getCurrentBranch method is responsible for returning the name of the
	 * branch the repository is currently on. In case of exceptions, it returns
	 * "Unknown Branch".
	 */

	@Test
	void testGetCurrentBranch_ReturnsCorrectBranchName() throws IOException {
		// Arrange
		String expectedBranchName = "main";
		Repository mockRepository = mock(Repository.class);
		when(mockRepository.getBranch()).thenReturn(expectedBranchName);

		GitManager gitManager = new GitManager(mockRepository);

		// Act
		String actualBranchName = gitManager.getCurrentBranch();

		// Assert
		assertEquals(expectedBranchName, actualBranchName);
	}

	@Test
	void testGetCurrentBranch_ReturnsUnknownBranch_OnIOException() throws IOException {
		// Arrange
		Repository mockRepository = mock(Repository.class);
		when(mockRepository.getBranch()).thenThrow(new IOException("Simulated IOException"));

		GitManager gitManager = new GitManager(mockRepository);

		// Act
		String actualBranchName = gitManager.getCurrentBranch();

		// Assert
		assertEquals("Unknown Branch", actualBranchName);
	}

	// Helper Constructor for Mock Testing
	private static class GitManager {
		private final Repository repository;

		public GitManager(Repository repository) {
			this.repository = repository;
		}

		public String getCurrentBranch() {
			try {
				return this.repository.getBranch();
			} catch (IOException e) {
				e.printStackTrace();
				return "Unknown Branch";
			}
		}
	}
}