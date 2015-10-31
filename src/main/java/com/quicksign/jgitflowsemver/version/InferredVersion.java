package com.quicksign.jgitflowsemver.version;

import com.github.zafarkhaja.semver.Version;

/**
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class InferredVersion implements Comparable<Version> {
    private final Version version;

    private final VersionContext context;

    /**
     * @param version the version
     * @param context the context of inferred version
     */
    public InferredVersion(final Version version, final VersionContext context) {
        this.version = version;
        this.context = context;
    }

    public Version getVersion() {
        return version;
    }

    public VersionContext getContext() {
        return context;
    }

    @Override
    public int compareTo(Version o) {
        return this.version.compareTo(o);
    }

    @Override
    public String toString() {
        return version.toString();
    }
}
