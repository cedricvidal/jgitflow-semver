package com.quicksign.jgitflowsemver.version;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.Constants;

import java.io.IOException;

/**
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class VersionContext {

    /**
     * @param normal the normal part of the version according to semantic versioning
     */
    public VersionContext(final Version normal) {
        this.normal = normal;
    }

    /**
     * Creates a new instance using the normal part of the given {@link com.quicksign.jgitflowsemver.version.NearestVersion} that allows to use all other
     * methods in this class that <strong>do not</strong> require a {@link com.quicksign.jgitflowsemver.version.NearestVersion} argument.
     *
     * @param nearestVersion
     */
    public VersionContext(final NearestVersion nearestVersion) {
        this.nearestVersion = nearestVersion;
        this.normal = nearestVersion.getAny();
    }

    /**
     * Sets the branch part of the version
     *
     * @param branch
     * @return
     */
    public VersionContext branch(final String branch) {
        this.branch = branch;
        return this;
    }

    /**
     * Sets the distance to the last release tag
     *
     * @param nearestVersion
     * @return
     */
    public VersionContext distanceFromRelease(final NearestVersion nearestVersion) {
        distanceFromRelease = nearestVersion.getDistanceFromAny();
        return this;
    }

    /**
     * Sets the distance to the last release tag
     *
     * @return
     */
    public VersionContext distanceFromRelease() {
        return distanceFromRelease(nearestVersion);
    }

    /**
     * Sets the sha part of the version
     *
     * @param git
     * @param conf
     * @return
     */
    public VersionContext sha(final Git git, final GitflowVersioningConfiguration conf) throws IOException {
        AbbreviatedObjectId id = git.getRepository().resolve(Constants.HEAD).abbreviate(7);
        sha = conf.getBuildMetadataIds().getSha() + "." + id.name();

        return this;
    }

    /**
     * Sets the dirty part of the version
     *
     * @param git
     * @param conf
     * @return
     */
    public VersionContext dirty(final Git git, final GitflowVersioningConfiguration conf) throws GitAPIException {
        final boolean clean = git.status().call().isClean();
        this.dirty = !clean;
        return this;
    }

    /**
     * Sets the type of the version.
     *
     * @param type
     * @return
     */
    public VersionContext type(final VersionType type) {
        this.type = type;
        return this;
    }

    private NearestVersion nearestVersion;
    private final Version normal;
    private String branch;
    private int distanceFromRelease;
    private String sha;
    private boolean dirty;
    private VersionType type;

    public int getDistanceFromRelease() {
        return distanceFromRelease;
    }

    public String getBranch() {
        return branch;
    }

    public String getSha() {
        return sha;
    }

    public boolean isDirty() {
        return dirty;
    }

    public Version getNormal() {
        return normal;
    }

    public VersionType getType() {
        return type;
    }
}
