package com.quicksign.jgitflowsemver.patterns;

import com.github.zafarkhaja.semver.Version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class BranchVersionExtractor implements VersionExtractor {

    protected Pattern versionHotfixPattern;

    public BranchVersionExtractor(String prefix) {
        this.versionHotfixPattern = Pattern.compile("^" + prefix.replace("/", "\\/") + "([0-9]+\\.[0-9]+\\.[0-9]+)$");
    }

    @Override
    public Version extract(String name) {
        final Matcher m = versionHotfixPattern.matcher(name);
        if(m.matches()) {
            return Version.valueOf(m.group(1));
        }
        return null;
    }
}
