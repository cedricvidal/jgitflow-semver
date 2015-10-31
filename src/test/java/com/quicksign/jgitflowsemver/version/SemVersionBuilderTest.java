package com.quicksign.jgitflowsemver.version;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import org.junit.Before;
import org.junit.Test;

import static com.quicksign.jgitflowsemver.version.NearestVersion.nearest;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class SemVersionBuilderTest {

    public static final Version V0_9_0 = Version.valueOf("0.9.0");
    public static final Version V1_0_0 = Version.valueOf("1.0.0");
    private SemVersionBuilder builder;

    @Before
    public void setup() {
        builder = new SemVersionBuilder();
    }

    @Test
    public void v1_0_0() {
        assertEquals(V1_0_0, builder.build(new InferredVersion(V1_0_0), new GitflowVersioningConfiguration().useMavenSnapshot()));
    }
    @Test
    public void v1_0_0_SNAPSHOT() {
        assertEquals(Version.valueOf("1.0.0-SNAPSHOT"), builder.build(new InferredVersion(V1_0_0).branch("dev").distanceFromRelease(nearest(V0_9_0, 1)), new GitflowVersioningConfiguration().useMavenSnapshot()));
    }
    @Test
    public void v1_0_0_fix_foo_SNAPSHOT() {
        assertEquals(Version.valueOf("1.0.0-fix.foo.SNAPSHOT"), builder.build(new InferredVersion(V1_0_0).branch("fix.foo").distanceFromRelease(nearest(V0_9_0, 1)), new GitflowVersioningConfiguration().useMavenSnapshot()));
    }
    @Test
    public void v1_0_0_feature_foo_SNAPSHOT() {
        assertEquals(Version.valueOf("1.0.0-feature.foo.SNAPSHOT"), builder.build(new InferredVersion(V1_0_0).branch("feature.foo").distanceFromRelease(nearest(V0_9_0, 1)), new GitflowVersioningConfiguration().useMavenSnapshot()));
    }

}
