package com.quicksign.jgitflowsemver.strategy;

import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.version.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;

/**
 * The strategy to use when Gitflow's <strong>production release</strong> branch is the current branch.
 *
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class BranchReleaseStrategy extends AbstractStrategy implements Strategy {

    @Override
    public boolean canInfer(final Repository repo, GitflowVersioningConfiguration conf) throws IOException {
        return conf.getBranch(repo).equals(getMasterBranchName(repo));
    }

    private static String getMasterBranchName(final Repository repo) {
        final String name = AbstractStrategy.getBranchName(repo, CONFIG_BRANCH_RELEASE);
        return name != null ? name : DEFAULT_BRANCH_RELEASE;
    }

    private static final String CONFIG_BRANCH_RELEASE = "master";
    private static final String DEFAULT_BRANCH_RELEASE = "master";

    @Override
    protected VersionWithType doInfer(Repository repo, GitflowVersioningConfiguration conf) throws GitAPIException, IOException {
        NearestVersion nearestVersion = new NearestVersionLocator().locate(repo);

        return new VersionWithTypeBuilder(nearestVersion)
            .branch(conf.preReleaseIds().getRelease())
            .distanceFromRelease()
            .sha(repo, conf)
            .dirty(repo, conf)
            .type(VersionType.RELEASE)
            .build();
    }

}
