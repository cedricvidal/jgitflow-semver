package com.quicksign.jgitflowsemver.strategy;

import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.version.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The strategy to use when Gitflow's <strong>production release</strong> branch is the current branch.
 *
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class BranchFeatureStrategy extends AbstractStrategy implements Strategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(BranchDevelopStrategy.class);

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
    protected VersionWithType doInfer(Git git, GitflowVersioningConfiguration conf) throws GitAPIException, IOException {
        NearestVersion nearestVersion = new NearestVersionLocator().locate(git);

        final Repository repo = git.getRepository();
        String feature = repo.getBranch().substring(getFeaturePrefix(repo).length());

        final NearestVersion nextVersion = new NearestVersion(
            nearestVersion.getAny().incrementMinorVersion(),
            nearestVersion.getDistanceFromAny()
        );
        if(LOGGER.isInfoEnabled()) {
            LOGGER.info("On feature branch so bumping minor version {} -> {}", nearestVersion.getAny().getNormalVersion(), nextVersion.getAny().getNormalVersion());
        }

        return new VersionWithTypeBuilder(nextVersion)
            .branch(conf.getPreReleaseIds().getFeature() + "." + feature)
            .distanceFromRelease(nearestVersion)
            .sha(git, conf)
            .dirty(git, conf)
            .type(VersionType.FEATURE)
            .build(conf);
    }

}
