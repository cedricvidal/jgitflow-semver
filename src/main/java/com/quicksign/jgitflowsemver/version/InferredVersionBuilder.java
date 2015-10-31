package com.quicksign.jgitflowsemver.version;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;

/**
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class InferredVersionBuilder {

    /**
     * Builds the version
     *
     * @return
     * @param conf
     */
    public InferredVersion build(VersionContext ctx, GitflowVersioningConfiguration conf) {
        Version version;
        if(!conf.isUseMavenSnapshot()) {
            StringBuilder preRelease = new StringBuilder();
            StringBuilder buildMetadata = new StringBuilder();

            if(ctx.getDistanceFromRelease() > 0) {
                append(preRelease, ctx.getBranch());
                append(preRelease, Integer.toString(ctx.getDistanceFromRelease()));
                append(buildMetadata, ctx.getSha());
            }

            if(ctx.isDirty()) {
                append(buildMetadata, conf.getBuildMetadataIds().getDirty());
            }

            version = new Version.Builder(ctx.getNormal().getNormalVersion())
                .setPreReleaseVersion(preRelease.toString())
                .setBuildMetadata(buildMetadata.toString())
                .build();

        } else {
            StringBuilder buildMetadata = new StringBuilder();
            if(ctx.getDistanceFromRelease() > 0) {
                if(ctx.getBranch() != null && ctx.getBranch().length() > 0 && !conf.getPreReleaseIds().getDevelop().equals(ctx.getBranch())) {
                    buildMetadata.append(ctx.getBranch()).append('.');
                }
                buildMetadata.append("SNAPSHOT");
            }
            version = new Version.Builder(ctx.getNormal().getNormalVersion())
                .setPreReleaseVersion(buildMetadata.toString())
                .build();
        }

        return new InferredVersion(version, ctx);
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
