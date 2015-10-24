package com.quicksign.jgitflowsemver.dsl;

import java.io.File;

/**
 * The Gradle plugin extension. This is the entry point of the <em>DSL</em> to configure the plugin.
 *
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class GitflowVersioningConfiguration {
    public GitflowVersioningConfiguration(final File projectDir) {
        setRepositoryRoot(projectDir.getPath());
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
}
