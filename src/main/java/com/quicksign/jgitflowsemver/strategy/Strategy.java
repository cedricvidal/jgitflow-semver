package com.quicksign.jgitflowsemver.strategy;

import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.version.VersionWithType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The interface for all strategies used to infer the version.
 * See the package
 * <a href="{@docRoot}/com/github/amkay/gradle/gitflow/strategy/package-summary.html#package-description">
 * strategy
 * </a>
 * to see all classes implementing this interface.
 *
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public interface Strategy {

    public VersionWithType infer(Repository repo, GitflowVersioningConfiguration conf) throws GitAPIException, IOException;

    /**
     * Determines if the strategy can infer the version. This is used to match the current branch, for example.
     *
     * @param repo
     * @return
     */
    public abstract boolean canInfer(final Repository repo) throws IOException;

    /**
     * All available strategies.
     * See the package
     * <a href="{@docRoot}/com/github/amkay/gradle/gitflow/strategy/package-summary.html#package-description">
     * strategy
     * </a>
     * to see all classes implementing this interface.
     */
    public static final ArrayList<Strategy> STRATEGIES = new ArrayList<Strategy>(Arrays.asList(
        new BranchReleaseStrategy(),
        new BranchDevelopStrategy(),
        new BranchFeatureStrategy()
    ));
}
