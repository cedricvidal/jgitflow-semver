package com.quicksign.jgitflowsemver.strategy;

import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.version.*;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;

/**
 * The strategy to use when the current head is a <strong>detached head</strong>.
 *
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class DetachedHeadStrategy extends AbstractStrategy implements Strategy {

    @Override
    public boolean canInfer(final Repository repo, GitflowVersioningConfiguration conf) throws IOException {
        return true;
    }

    @Override
    protected VersionWithType doInfer(Git git, GitflowVersioningConfiguration conf) throws GitAPIException, IOException {
        NearestVersion nearestVersion = new NearestVersionLocator().locate(git);

        return new VersionWithTypeBuilder(nearestVersion)
            .branch(conf.preReleaseIds().getDetachedHead())
            .distanceFromRelease()
            .sha(git, conf)
            .dirty(git, conf)
            .type(VersionType.DETACHED_HEAD)
            .build(conf);
    }

}
