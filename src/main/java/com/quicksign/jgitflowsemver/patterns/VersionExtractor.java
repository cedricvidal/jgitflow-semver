package com.quicksign.jgitflowsemver.patterns;

import com.github.zafarkhaja.semver.Version;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public interface VersionExtractor {
    public Version extract(String name);
}
