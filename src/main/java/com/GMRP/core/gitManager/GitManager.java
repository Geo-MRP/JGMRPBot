package com.GMRP.core.gitManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;

public class GitManager {
	private Repository repository;
	private Git git;
	
	public GitManager(String path) throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		this.repository = builder.setGitDir(new File(path, ".git"))
				.readEnvironment() // scan environment GIT_* variables
				.findGitDir() // scan up the file system tree
				.build();
		this.git = new Git(repository);
	}

	/**
	 * Simple current branch getter
	 * @return Returns a string containing the branch this GitManager Object's repository is currently in. e.g. "origin/main"
	 */
	public String getCurrentBranch() {
		try {
			return this.repository.getBranch();
		} catch (IOException e) {
			e.printStackTrace();
			return "Unknown Branch";
		}
	}
	
	public int countFeatureCommits() {
		int featCount = 0;
        Iterable<RevCommit> commits;
		try {
			commits = git.log().call();
			for (RevCommit commit : commits) {
				String message = commit.getShortMessage().toLowerCase();
				
				if (message.startsWith("feat:") || message.startsWith("feat(")) {
					featCount++;
				}
			}
			return featCount;
		} catch (NoHeadException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public int countAllCommits() {
		try {
			int count = 0;
			Iterable<RevCommit> commits = git.log().call();
			for (@SuppressWarnings("unused") RevCommit commit : commits) {
				count++;
			}
			return count;
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		return -1;
	}
}