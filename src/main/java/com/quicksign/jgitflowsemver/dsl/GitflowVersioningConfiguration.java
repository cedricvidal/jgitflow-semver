package com.quicksign.jgitflowsemver.dsl;

import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.quicksign.jgitflowsemver.util.JGitUtil.isDetached;

/**
 * The Gradle plugin extension. This is the entry point of the <em>DSL</em> to configure the plugin.
 *
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class GitflowVersioningConfiguration {

    private Logger logger = LoggerFactory.getLogger(GitflowVersioningConfiguration.class);
    private boolean useMavenSnapshot = false;
    private boolean mavenCompatibility = false;

    public GitflowVersioningConfiguration() {
    }

    /**
     * Helper method to allow keyword-based configuration of the <code>repositoryRoot</code> property
     *
     * @param repositoryRoot
     */
    public void repositoryRoot(final String repositoryRoot) {
        setRepositoryRoot(repositoryRoot);
    }

    /**
     * Helper method to allow the <em>keyword-based configuration (the DSL)</em> of the <em>pre-release
     * identifiers</em> (<code>preReleaseIds</code>) according to <em>semantic versioning</em>.
     */
    public PreReleaseIdentifiers preReleaseIds() {
        return preReleaseIds;
    }

    /**
     * Helper method to allow the <em>keyword-based configuration (the DSL)</em> of the <em>build metadata identifiers
     * </em> (<code>buildMetadataIds</code>) according to <em>semantic versioning</em>.
     */
    public BuildMetadataIdentifiers buildMetadataIds() {
        return buildMetadataIds;
    }

    public String getRepositoryRoot() {
        return repositoryRoot;
    }

    public void setRepositoryRoot(String repositoryRoot) {
        this.repositoryRoot = repositoryRoot;
    }

    public final PreReleaseIdentifiers getPreReleaseIds() {
        return preReleaseIds;
    }

    public final BuildMetadataIdentifiers getBuildMetadataIds() {
        return buildMetadataIds;
    }

    /**
     * The root directory of the repository to use
     */
    private String repositoryRoot;
    /**
     * Holder that allows to configure the <em>pre-release identifiers</em> according to <em>semantic versioning</em>
     */
    private final PreReleaseIdentifiers preReleaseIds = new PreReleaseIdentifiers();
    /**
     * Holder that allows to configure the <em>build metadata identifiers</em> according to <em>semantic versioning</em>
     */
    private final BuildMetadataIdentifiers buildMetadataIds = new BuildMetadataIdentifiers();

    private String forceBranch;

    public String getForceBranch() {
        return forceBranch;
    }

    public void setForceBranch(String forceBranch) {
        this.forceBranch = forceBranch;
    }

    public GitflowVersioningConfiguration forceBranch(String branchName) {
        setForceBranch(branchName);
        return this;
    }

    public String getBranch(Repository repo) throws IOException {
        String branch = repo.getBranch();
        if(forceBranch != null && forceBranch.length() > 0) {
            if(isDetached(repo)) {
                branch = forceBranch;
            } else {
                logger.warn("Ignoring forceBranch as repository is not detached");
            }
        }
        return branch;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public GitflowVersioningConfiguration useMavenSnapshot() {
        this.useMavenSnapshot = true;
        return this;
    }

    public boolean isUseMavenSnapshot() {
        return useMavenSnapshot;
    }

    public GitflowVersioningConfiguration mavenCompatibility() {
        this.mavenCompatibility = true;
        return this;
    }

    public boolean isMavenCompatibility() {
        return mavenCompatibility;
    }
}
