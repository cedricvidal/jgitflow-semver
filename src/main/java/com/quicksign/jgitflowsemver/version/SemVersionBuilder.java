package com.quicksign.jgitflowsemver.version;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;

/**
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class SemVersionBuilder {

    /**
     * Builds the version
     *
     * @return
     * @param conf
     */
    public Version build(InferredVersion inferredVersion, GitflowVersioningConfiguration conf) {
        Version version;
        if(!conf.isUseMavenSnapshot()) {
            StringBuilder preRelease = new StringBuilder();
            StringBuilder buildMetadata = new StringBuilder();

            if(inferredVersion.getDistanceFromRelease() > 0) {
                append(preRelease, inferredVersion.getBranch());
                append(preRelease, Integer.toString(inferredVersion.getDistanceFromRelease()));
                append(buildMetadata, inferredVersion.getSha());
            }

            if(inferredVersion.isDirty()) {
                append(buildMetadata, conf.getBuildMetadataIds().getDirty());
            }

            version = new Version.Builder(inferredVersion.getNormal().getNormalVersion())
                .setPreReleaseVersion(preRelease.toString())
                .setBuildMetadata(buildMetadata.toString())
                .build();

        } else {
            StringBuilder buildMetadata = new StringBuilder();
            if(inferredVersion.getDistanceFromRelease() > 0) {
                if(inferredVersion.getBranch() != null && inferredVersion.getBranch().length() > 0 && !conf.getPreReleaseIds().getDevelop().equals(inferredVersion.getBranch())) {
                    buildMetadata.append(inferredVersion.getBranch()).append('.');
                }
                buildMetadata.append("SNAPSHOT");
            }
            version = new Version.Builder(inferredVersion.getNormal().getNormalVersion())
                .setPreReleaseVersion(buildMetadata.toString())
                .build();
        }

        return version;
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

}
