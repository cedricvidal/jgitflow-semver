package com.quicksign.jgitflowsemver.android;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.version.InferredVersion;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class AndroidVersionCodeBuilder {
    public Integer build(InferredVersion ctx, GitflowVersioningConfiguration conf) {
        final Version nearest = ctx.getNearestVersion().getAny();
        int major = nearest.getMajorVersion();
        int minor = nearest.getMinorVersion();
        int patch = nearest.getPatchVersion();
        int baseCode = ((major * 1000 + minor) * 1000 + patch) * 1000;
        int commitCount = ctx.getDistanceFromRelease();
        int code = baseCode + commitCount;
        return code;
    }
}

