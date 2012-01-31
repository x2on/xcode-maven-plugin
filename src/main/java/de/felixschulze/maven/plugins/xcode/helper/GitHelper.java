/*
 * Copyright (C) 2012 Felix Schulze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.felixschulze.maven.plugins.xcode.helper;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Helper for creating an automatic version number from git.
 *
 * @author <a href="mail@felixschulze.de">Felix Schulze</a>
 */
public class GitHelper {

    Repository repository;

    public GitHelper(File gitDir) throws IOException {
        RepositoryBuilder builder = new RepositoryBuilder();
        repository = builder.setGitDir(gitDir).readEnvironment().findGitDir().build();
    }

    public int numberOfCommits() throws NoHeadException {
        Git git = new Git(repository);
        Iterable<RevCommit> logs = git.log().call();
        Iterator<RevCommit> i = logs.iterator();
        int numberOfCommits = 0;
        while (i.hasNext()) {
            numberOfCommits++;
            i.next();
        }
        return numberOfCommits;
    }

    public String currentHeadRef() throws IOException {
        ObjectId head = repository.resolve("HEAD");
        RevWalk walk = new RevWalk(repository);
        RevCommit commit = walk.parseCommit(head);
        return commit.abbreviate(7).name();
    }
}
