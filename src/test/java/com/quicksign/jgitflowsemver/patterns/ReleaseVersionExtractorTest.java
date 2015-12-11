package com.quicksign.jgitflowsemver.patterns;

import com.github.zafarkhaja.semver.Version;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class ReleaseVersionExtractorTest {

    private final BranchVersionExtractor extractor = new BranchVersionExtractor("release/");

    @Test
    public void v1_0_0() {
        assertEquals(Version.valueOf("1.0.0"), extractor.extract("release/1.0.0"));
    }

    @Test
    public void v1_0_1() {
        assertEquals(Version.valueOf("1.0.1"), extractor.extract("release/1.0.1"));
    }

    @Test
    public void v1_0_31() {
        assertEquals(Version.valueOf("1.0.31"), extractor.extract("release/1.0.31"));
    }

    @Test
    public void v1_10_0() {
        assertEquals(Version.valueOf("1.10.0"), extractor.extract("release/1.10.0"));
    }

    @Test
    public void v10_0_0() {
        assertEquals(Version.valueOf("10.0.0"), extractor.extract("release/10.0.0"));
    }

    @Test
    public void v1_0() {
        assertNull(extractor.extract("release/1.0"));
    }

    @Test
    public void v1() {
        assertNull(extractor.extract("release/1"));
    }

    @Test
    public void dumb() {
        assertNull(extractor.extract("dumb"));
    }

    @Test
    public void nonVersion() {
        assertNull(extractor.extract("release/dumb"));
    }

}
