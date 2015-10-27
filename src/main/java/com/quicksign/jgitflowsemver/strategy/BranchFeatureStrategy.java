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
public class BranchFeatureStrategy extends AbstractStrategy implements Strategy {

    @Override
    public boolean canInfer(final Repository repo, GitflowVersioningConfiguration conf) throws IOException {
        return conf.getBranch(repo).startsWith(getFeaturePrefix(repo));
    }

    private static String getFeaturePrefix(final Repository repo) {
        final String prefix = AbstractStrategy.getPrefix(repo, CONFIG_PREFIX_FEATURE);
        return prefix != null ? prefix : DEFAULT_PREFIX_FEATURE;
    }

    private static final String CONFIG_PREFIX_FEATURE = "feature";
    private static final String DEFAULT_PREFIX_FEATURE = "feature/";

    @Override
    protected VersionWithType doInfer(Repository repo, GitflowVersioningConfiguration conf) throws GitAPIException, IOException {
        NearestVersion nearestVersion = new NearestVersionLocator().locate(repo);

        String feature = repo.getBranch().substring(getFeaturePrefix(repo).length());

        return new VersionWithTypeBuilder(nearestVersion)
            .branch(conf.getPreReleaseIds().getFeature() + "." + feature)
            .distanceFromRelease()
            .sha(repo, conf)
            .dirty(repo, conf)
            .type(VersionType.FEATURE)
            .build();
    }

}
