package com.quicksign.jgitflowsemver.strategy;

import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.version.VersionWithType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;

/**
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public abstract class AbstractStrategy implements Strategy {

    @Override
    public VersionWithType infer(Repository repo, GitflowVersioningConfiguration conf) throws GitAPIException, IOException {
        return doInfer(repo, conf);
    }

    protected abstract VersionWithType doInfer(Repository repo, GitflowVersioningConfiguration conf) throws GitAPIException, IOException;

    /**
     * Helper method to retrieve a Gitflow branch prefix from .git/config
     *
     * @param repo
     * @param name
     * @return
     */
    protected static String getPrefix(final Repository repo, final String name) {
        return repo.getConfig().getString(SECTION_GITFLOW, SUBSECTION_PREFIX, name);
    }

    /**
     * Helper method to retrieve a Gitflow branch name from .git/config
     *
     * @param repo
     * @param name
     * @return
     */
    protected static String getBranchName(final Repository repo, final String name) {
        return repo.getConfig().getString(SECTION_GITFLOW, SUBSECTION_BRANCH, name);
    }

    /**
     * The section of Gitflow's configuration in <code>.git/config</code>
     */
    public static final String SECTION_GITFLOW = "gitflow";
    /**
     * The subsection containing Gitflow's branch prefixes in <code>.git/config</code>
     */
    public static final String SUBSECTION_PREFIX = "prefix";
    /**
     * The subsection containing Gitflow's branch names in <code>.git/config</code>
     */
    public static final String SUBSECTION_BRANCH = "branch";
}
