package com.quicksign.jgitflowsemver.android;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.version.InferredVersion;
import com.quicksign.jgitflowsemver.version.VersionType;

import static com.quicksign.jgitflowsemver.version.VersionType.RELEASE;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class AndroidVersionCodeBuilder {

    public static final int LIMIT_MAJOR = 20;
    public static final int LIMIT_MINOR = 100;
    public static final int LIMIT_PATCH = 1000;
    public static final int LIMIT_COMMIT_COUNT_DEV = 900;
    public static final int LIMIT_COMMIT_COUNT_MASTER = 100;
    public static final int LIMIT_COMMIT_COUNT = LIMIT_COMMIT_COUNT_DEV + LIMIT_COMMIT_COUNT_MASTER;

    static {
        assert LIMIT_MAJOR * LIMIT_MINOR * LIMIT_PATCH * LIMIT_COMMIT_COUNT <= Integer.MAX_VALUE;
    }

    public Integer build(InferredVersion ctx, GitflowVersioningConfiguration conf) throws OutOfRangeAndroidVersionCodeException {
        final Version nearest = ctx.getNearestVersion().getAny();
        int major = nearest.getMajorVersion();
        int minor = nearest.getMinorVersion();
        int patch = nearest.getPatchVersion();

        checkLimit(nearest, "major", major, LIMIT_MAJOR);
        checkLimit(nearest, "minor", minor, LIMIT_MINOR);
        checkLimit(nearest, "patch", patch, LIMIT_PATCH);

        int baseCode = ((major * LIMIT_MINOR + minor) * LIMIT_PATCH + patch) * LIMIT_COMMIT_COUNT;
        int commitCount = ctx.getDistanceFromRelease();

        if(commitCount >= (ctx.getType().equals(RELEASE) ? LIMIT_COMMIT_COUNT_MASTER : LIMIT_COMMIT_COUNT_DEV)) {
            throw new OutOfRangeAndroidVersionCodeException("Commit count " + commitCount + " since previous release " + nearest + " exceeds the " + LIMIT_COMMIT_COUNT_DEV + " limit");
        }

        if(ctx.getType().equals(VersionType.DEVELOP)) {
            commitCount += LIMIT_COMMIT_COUNT_MASTER;
        }

        int code = baseCode + commitCount;
        return (int)code;
    }

    private static void checkLimit(Version nearest, String typ, long major, int limit) throws OutOfRangeAndroidVersionCodeException {
        if(major >= limit) {
            throw new OutOfRangeAndroidVersionCodeException("Version " + typ + " " + nearest + " exceeds the " + limit + " limit");
        }
    }

}
