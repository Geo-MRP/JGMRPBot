package com.GMRP.core.gitManager;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class GitManager {
	private final Repository repository;

    public GitManager(String path) throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		this.repository = builder.setGitDir(new File(path, ".git"))
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
    }

	/**
	 * Simple current branch getter
	 * 
	 * @return Returns a string containing the branch this GitManager Object's
	 *         repository is currently in. e.g. "origin/main"
	 */
	public String getCurrentBranch() {
		try {
			return this.repository.getBranch();
		} catch (IOException e) {
			e.printStackTrace();
			return "Unknown Branch";
		}
	}
}