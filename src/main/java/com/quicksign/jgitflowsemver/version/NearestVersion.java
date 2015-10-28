package com.quicksign.jgitflowsemver.version;

import com.github.zafarkhaja.semver.Version;
import org.eclipse.jgit.lib.ObjectId;

/**
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class NearestVersion {
    /**
     * The nearest version that is tagged.
     */
    private final Version any;

    private ObjectId objectId;

    /**
     * The number of commits since {@code any} reachable from <em>HEAD</em>.
     */
    private final int distanceFromAny;

    public NearestVersion(Version any, int distanceFromAny) {
        this.any = any;
        this.distanceFromAny = distanceFromAny;
    }

    public final Version getAny() {
        return any;
    }

    public final int getDistanceFromAny() {
        return distanceFromAny;
    }

    public NearestVersion objectId(ObjectId objectId) {
        this.objectId = objectId;
        return this;
    }

    public ObjectId getObjectId() {
        return objectId;
    }
}
