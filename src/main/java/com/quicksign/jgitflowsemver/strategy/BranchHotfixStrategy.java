package com.quicksign.jgitflowsemver.strategy;

import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.version.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;

/**
 * The strategy to use when one of Gitflow's <strong>hotfix</strong> branches is the current branch.
 *
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class BranchHotfixStrategy extends AbstractStrategy implements Strategy {

    @Override
    public boolean canInfer(final Repository repo, GitflowVersioningConfiguration conf) throws IOException {
        return conf.getBranch(repo).startsWith(getHotfixPrefix(repo));
    }

    public static String getHotfixPrefix(final Repository repo) {
        final String prefix = AbstractStrategy.getPrefix(repo, CONFIG_PREFIX_HOTFIX);
        return prefix != null ? prefix : DEFAULT_PREFIX_HOTFIX;
    }

    private static final String CONFIG_PREFIX_HOTFIX = "hotfix";
    private static final String DEFAULT_PREFIX_HOTFIX = "hotfix/";

    @Override
    protected InferredVersion doInfer(Git git, GitflowVersioningConfiguration conf) throws GitAPIException, IOException {
        NearestVersion nearestVersion = new NearestVersionLocator().locate(git);

        final Repository repo = git.getRepository();
        String hotfix = repo.getBranch().substring(getHotfixPrefix(repo).length());

        return new InferredVersionBuilder().build(new VersionContext(nearestVersion)
            .branch(conf.getPreReleaseIds().getHotfix() + "." + hotfix)
            .distanceFromRelease()
            .sha(git, conf)
            .dirty(git, conf)
            .type(VersionType.HOTFIX),
            conf);
    }

}
