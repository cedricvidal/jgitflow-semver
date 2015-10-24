package com.quicksign.jgitflowsemver.util;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class JGitUtil {
    public static boolean isAncestorOf(Repository repo, ObjectId base, ObjectId tip) throws IOException {
        RevWalk revWalk = new RevWalk(repo);
        RevCommit baseCommit = revWalk.lookupCommit(base);
        RevCommit tipCommit = revWalk.lookupCommit(tip);
        return revWalk.isMergedInto(baseCommit, tipCommit);
    }
}
