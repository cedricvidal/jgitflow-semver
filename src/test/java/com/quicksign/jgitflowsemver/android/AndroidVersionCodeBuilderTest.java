package com.quicksign.jgitflowsemver.android;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.version.InferredVersion;
import com.quicksign.jgitflowsemver.version.NearestVersion;
import org.junit.Before;
import org.junit.Test;

import static com.quicksign.jgitflowsemver.version.NearestVersion.nearest;
import static java.lang.Integer.valueOf;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class AndroidVersionCodeBuilderTest {

    public static final Version V1_2_4 = Version.valueOf("1.2.4");
    public static final Version V1_2_3 = Version.valueOf("1.2.3");

    private final GitflowVersioningConfiguration conf = new GitflowVersioningConfiguration();
    private AndroidVersionCodeBuilder builder;

    @Before
    public void setup() {
        builder = new AndroidVersionCodeBuilder();
    }

    @Test
    public void v1_2_3() {
        assertEquals(valueOf(1002003000), builder.build(new InferredVersion(V1_2_3), conf));
    }

    @Test
    public void v1_2_4_dev_765() {
        assertEquals(valueOf(1002003765), builder.build(new InferredVersion(V1_2_4).distanceFromRelease(nearest(V1_2_3, 765)), conf));
    }

    @Test
    public void v1_2_3_master_987() {
        assertEquals(valueOf(1002003987), builder.build(new InferredVersion(V1_2_3).distanceFromRelease(nearest(V1_2_3, 987)), conf));
    }

}
