package com.quicksign.jgitflowsemver.strategy;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.patterns.BranchVersionExtractor;
import com.quicksign.jgitflowsemver.version.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.quicksign.jgitflowsemver.strategy.BranchHotfixStrategy.getHotfixPrefix;

/**
 * The strategy to use when Gitflow's <strong>production release</strong> branch is the current branch.
 *
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class BranchPreReleaseStrategy extends AbstractStrategy implements Strategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(BranchPreReleaseStrategy.class);

    @Override
    public boolean canInfer(final Repository repo, GitflowVersioningConfiguration conf) throws IOException {
        final String branchName = conf.getBranch(repo);
        final Version version = extractReleaseOrHotfixVersion(repo, branchName);
        return version != null;
    }

    private Version extractReleaseOrHotfixVersion(Repository repo, String branchName) {
        Version version = new BranchVersionExtractor(getReleasePrefix(repo)).extract(branchName);
        if(version == null) {
            version = new BranchVersionExtractor(getHotfixPrefix(repo)).extract(branchName);
        }
        return version;
    }

    private static String getReleasePrefix(final Repository repo) {
        final String prefix = AbstractStrategy.getPrefix(repo, CONFIG_PREFIX_PRE_RELEASE);
        return prefix != null ? prefix : DEFAULT_PREFIX_PRE_RELEASE;
    }

    private static final String CONFIG_PREFIX_PRE_RELEASE = "release";
    private static final String DEFAULT_PREFIX_PRE_RELEASE = "release/";

    @Override
    protected VersionWithType doInfer(Git git, GitflowVersioningConfiguration conf) throws GitAPIException, IOException {
        NearestVersion nearestVersion = new NearestVersionLocator().locate(git);

        final Repository repo = git.getRepository();
        final String branchName = repo.getBranch();
        final Version releaseVersion = extractReleaseOrHotfixVersion(repo, branchName);
        if(LOGGER.isInfoEnabled()) {
            LOGGER.info("Extracted version {} from release branch name {}", releaseVersion.getNormalVersion(), branchName);
        }

        return new VersionWithTypeBuilder(releaseVersion.toString())
            .branch(conf.getPreReleaseIds().getPreRelease())
            .distanceFromRelease(nearestVersion)
            .sha(git, conf)
            .dirty(git, conf)
            .type(VersionType.PRE_RELEASE)
            .build(conf);
    }

}
