package com.quicksign.jgitflowsemver.util;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import java.io.IOException;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class JGitUtil {
    public static boolean isAncestorOf(Repository repo, ObjectId base, ObjectId tip) throws IOException {
        RevWalk revWalk = new RevWalk(repo);
        RevCommit baseCommit = revWalk.lookupCommit(base);
        RevCommit tipCommit = revWalk.lookupCommit(tip);
        final boolean mergedInto = revWalk.isMergedInto(baseCommit, tipCommit);
        revWalk.dispose();
        return mergedInto;
    }

    public static ObjectId mergeBase(Repository repo, ObjectId id1, ObjectId id2) throws IOException {
        RevWalk walk = new RevWalk(repo);
        walk.setRevFilter(RevFilter.MERGE_BASE);
        walk.markStart(walk.parseCommit(id1));
        walk.markStart(walk.parseCommit(id2));
        ObjectId mergeBase = walk.next().toObjectId();
        walk.dispose();
        return mergeBase;
    }

    public static boolean isDetached(Repository repo) throws IOException {
        return repo.getBranch().equals(repo.resolve(Constants.HEAD).getName());
    }
}
