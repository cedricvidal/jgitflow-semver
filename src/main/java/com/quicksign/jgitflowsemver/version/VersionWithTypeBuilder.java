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
public class VersionWithTypeBuilder {
    /**
     * @param normal the normal part of the version according to semantic versioning
     */
    public VersionWithTypeBuilder(final String normal) {
        this.normal = normal;
    }

    /**
     * Creates a new instance using the normal part of the given {@link NearestVersion} that allows to use all other
     * methods in this class that <strong>do not</strong> require a {@link NearestVersion} argument.
     *
     * @param nearestVersion
     */
    public VersionWithTypeBuilder(final NearestVersion nearestVersion) {
        this.nearestVersion = nearestVersion;
        this.normal = nearestVersion.getAny().toString();
    }

    /**
     * Sets the branch part of the version
     *
     * @param branch
     * @return
     */
    public VersionWithTypeBuilder branch(final String branch) {
        this.branch = branch;
        return this;
    }

    /**
     * Sets the distance to the last release tag
     *
     * @param nearestVersion
     * @return
     */
    public VersionWithTypeBuilder distanceFromRelease(final NearestVersion nearestVersion) {
        distanceFromRelease = nearestVersion.getDistanceFromAny();
        return this;
    }

    /**
     * Sets the distance to the last release tag
     *
     * @return
     */
    public VersionWithTypeBuilder distanceFromRelease() {
        return distanceFromRelease(nearestVersion);
    }

    /**
     * Sets the sha part of the version
     *
     * @param git
     * @param conf
     * @return
     */
    public VersionWithTypeBuilder sha(final Git git, final GitflowVersioningConfiguration conf) throws IOException {
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
    public VersionWithTypeBuilder dirty(final Git git, final GitflowVersioningConfiguration conf) throws GitAPIException {
        final boolean clean = git.status().call().isClean();
        if (!clean) {
            dirty = conf.getBuildMetadataIds().getDirty();
        }

        return this;
    }

    /**
     * Sets the type of the version.
     *
     * @param type
     * @return
     */
    public VersionWithTypeBuilder type(final VersionType type) {
        this.type = type;
        return this;
    }

    /**
     * Builds the version
     *
     * @return
     * @param conf
     */
    public VersionWithType build(GitflowVersioningConfiguration conf) {
        Version version;
        if(!conf.isUseMavenSnapshot()) {
            StringBuilder preRelease = new StringBuilder();
            StringBuilder buildMetadata = new StringBuilder();

            if(distanceFromRelease > 0) {
                append(preRelease, branch);
                append(preRelease, Integer.toString(distanceFromRelease));
                append(buildMetadata, sha);
            }

            append(buildMetadata, dirty);

            version = new Version.Builder(normal)
                .setPreReleaseVersion(preRelease.toString())
                .setBuildMetadata(buildMetadata.toString())
                .build();
        } else {
            StringBuilder buildMetadata = new StringBuilder();
            if(distanceFromRelease > 0) {
                if(branch != null && branch.length() > 0 && !conf.getPreReleaseIds().getDevelop().equals(branch)) {
                    buildMetadata.append(branch).append('-');
                }
                buildMetadata.append("SNAPSHOT");
            }
            version = new Version.Builder(normal)
                .setPreReleaseVersion(buildMetadata.toString())
                .build();
        }

        return new VersionWithType(version, type);
    }

    private void append(final StringBuilder sb, final String s) {
        if (s == null || s.length() == 0) {
            return;

        }

        if(sb.length() > 0) {
            sb.append(".");
        }
        sb.append(s);
    }

    private NearestVersion nearestVersion;
    private final String normal;
    private String branch;
    private int distanceFromRelease;
    private String sha;
    private String dirty;
    private VersionType type;
}
