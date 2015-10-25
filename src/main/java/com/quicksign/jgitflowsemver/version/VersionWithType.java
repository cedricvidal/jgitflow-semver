package com.quicksign.jgitflowsemver.version;

import com.github.zafarkhaja.semver.Version;

/**
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class VersionWithType implements Comparable<Version> {
    private final Version version;
    private final VersionType type;

    /**
     * @param version the version
     * @param type    the type of the version
     */
    public VersionWithType(final Version version, final VersionType type) {
        this.version = version;
        this.type = type;
    }

    public Version getVersion() {
        return version;
    }

    public VersionType getType() {
        return type;
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
