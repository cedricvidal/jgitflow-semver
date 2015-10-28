package com.quicksign.jgitflowsemver.version;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class VersionWithTypeBuilderTest {
    @Test
    public void v1_0_0() {
        assertEquals(Version.valueOf("1.0.0"), new VersionWithTypeBuilder("1.0.0").build(new GitflowVersioningConfiguration().useMavenSnapshot()).getVersion());
    }
    @Test
    public void v1_0_0_SNAPSHOT() {
        final NearestVersion nearestVersion = new NearestVersion(Version.valueOf("0.9.0"), 1);
        assertEquals(Version.valueOf("1.0.0-SNAPSHOT"), new VersionWithTypeBuilder("1.0.0").branch("dev").distanceFromRelease(nearestVersion).build(new GitflowVersioningConfiguration().useMavenSnapshot()).getVersion());
    }
    @Test
    public void v1_0_0_fix_foo_SNAPSHOT() {
        final NearestVersion nearestVersion = new NearestVersion(Version.valueOf("0.9.0"), 1);
        assertEquals(Version.valueOf("1.0.0-fix.foo.SNAPSHOT"), new VersionWithTypeBuilder("1.0.0").branch("fix.foo").distanceFromRelease(nearestVersion).build(new GitflowVersioningConfiguration().useMavenSnapshot()).getVersion());
    }
    @Test
    public void v1_0_0_feature_foo_SNAPSHOT() {
        final NearestVersion nearestVersion = new NearestVersion(Version.valueOf("0.9.0"), 1);
        assertEquals(Version.valueOf("1.0.0-feature.foo.SNAPSHOT"), new VersionWithTypeBuilder("1.0.0").branch("feature.foo").distanceFromRelease(nearestVersion).build(new GitflowVersioningConfiguration().useMavenSnapshot()).getVersion());
    }
}
