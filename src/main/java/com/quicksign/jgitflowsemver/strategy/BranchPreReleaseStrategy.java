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
public class BranchPreReleaseStrategy extends AbstractStrategy implements Strategy {

    @Override
    public boolean canInfer(final Repository repo, GitflowVersioningConfiguration conf) throws IOException {
        return conf.getBranch(repo).startsWith(getReleasePrefix(repo));
    }

    private static String getReleasePrefix(final Repository repo) {
        final String prefix = AbstractStrategy.getPrefix(repo, CONFIG_PREFIX_PRE_RELEASE);
        return prefix != null ? prefix : DEFAULT_PREFIX_PRE_RELEASE;
    }

    private static final String CONFIG_PREFIX_PRE_RELEASE = "release";
    private static final String DEFAULT_PREFIX_PRE_RELEASE = "release/";

    @Override
    protected VersionWithType doInfer(Repository repo, GitflowVersioningConfiguration conf) throws GitAPIException, IOException {
        NearestVersion nearestVersion = new NearestVersionLocator().locate(repo);

        String releaseVersion = repo.getBranch().substring(getReleasePrefix(repo).length());

        return new VersionWithTypeBuilder(releaseVersion)
            .branch(conf.getPreReleaseIds().getPreRelease())
            .distanceFromRelease(nearestVersion)
            .sha(repo, conf)
            .dirty(repo, conf)
            .type(VersionType.PRE_RELEASE)
            .build();
    }

}
