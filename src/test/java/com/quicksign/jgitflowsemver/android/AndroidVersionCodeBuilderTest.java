package com.quicksign.jgitflowsemver.android;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.version.InferredVersion;
import org.junit.Before;
import org.junit.Test;

import static com.quicksign.jgitflowsemver.version.NearestVersion.nearest;
import static com.quicksign.jgitflowsemver.version.VersionType.DEVELOP;
import static com.quicksign.jgitflowsemver.version.VersionType.RELEASE;
import static java.lang.Integer.valueOf;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class AndroidVersionCodeBuilderTest {

    public static final Version V1_2_4 = Version.valueOf("1.2.4");
    public static final Version V1_2_3 = Version.valueOf("1.2.3");
    public static final Version V0_0_1 = Version.valueOf("0.0.1");

    private final GitflowVersioningConfiguration conf = new GitflowVersioningConfiguration();
    private AndroidVersionCodeBuilder builder;

    @Before
    public void setup() {
        builder = new AndroidVersionCodeBuilder();
    }

    @Test
    public void v1_2_3_master_0() throws OutOfRangeAndroidVersionCodeException {
        assertEquals(valueOf(102003000), builder.build(new InferredVersion(V1_2_3).type(RELEASE), conf));
    }

    @Test
    public void v1_2_4_dev_65() throws OutOfRangeAndroidVersionCodeException {
        assertEquals(valueOf(102003165), builder.build(new InferredVersion(V1_2_4).type(DEVELOP).distanceFromRelease(nearest(V1_2_3, 65)), conf));
    }

    @Test
    public void v1_2_3_master_2() throws OutOfRangeAndroidVersionCodeException {
        assertEquals(valueOf(102003002), builder.build(new InferredVersion(V1_2_3).type(RELEASE).distanceFromRelease(nearest(V1_2_3, 2)), conf));
    }

    @Test(expected = OutOfRangeAndroidVersionCodeException.class)
    public void v21_0_0_exceeds_limits() throws OutOfRangeAndroidVersionCodeException {
        builder.build(new InferredVersion(Version.valueOf("21.0.0")).type(RELEASE), conf);
    }

    @Test(expected = OutOfRangeAndroidVersionCodeException.class)
    public void v0_100_0_exceeds_limits() throws OutOfRangeAndroidVersionCodeException {
        builder.build(new InferredVersion(Version.valueOf("0.100.0")).type(RELEASE), conf);
    }

    @Test(expected = OutOfRangeAndroidVersionCodeException.class)
    public void v0_0_1000_exceeds_limits() throws OutOfRangeAndroidVersionCodeException {
        builder.build(new InferredVersion(Version.valueOf("0.0.1000")).type(RELEASE), conf);
    }

    @Test(expected = OutOfRangeAndroidVersionCodeException.class)
    public void v0_0_1_master_100_exceeds_limits() throws OutOfRangeAndroidVersionCodeException {
        builder.build(new InferredVersion(V0_0_1).type(RELEASE).distanceFromRelease(nearest(V0_0_1, 100)), conf);
    }

    @Test(expected = OutOfRangeAndroidVersionCodeException.class)
    public void v0_0_1_dev_900_exceeds_limits() throws OutOfRangeAndroidVersionCodeException {
        builder.build(new InferredVersion(V0_0_1).type(DEVELOP).distanceFromRelease(nearest(V0_0_1, 900)), conf);
    }

}
