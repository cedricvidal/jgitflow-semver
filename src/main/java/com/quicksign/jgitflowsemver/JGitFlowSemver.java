package com.quicksign.jgitflowsemver;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.strategy.Strategy;
import com.quicksign.jgitflowsemver.version.VersionWithType;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class JGitFlowSemver {
    private final Repository repository;
    private final File dir;

    private static final Logger LOGGER = LoggerFactory.getLogger(JGitFlowSemver.class);

    public static void main(String[] args) throws Exception {
        Version v = new JGitFlowSemver(new File(args[0], ".git")).infer();
        System.out.println(v);
    }

    public Version infer() throws Exception {
        final GitflowVersioningConfiguration conf = new GitflowVersioningConfiguration(dir);

        Ref headRef = repository.getRef( Constants.HEAD );
        if( headRef == null || headRef.getObjectId() == null ) {
            LOGGER.warn("No commit yet");
        }

        VersionWithType versionWithType = null;
        for (Strategy strategy : Strategy.STRATEGIES) {
            if(strategy.canInfer(repository)) {
                versionWithType = strategy.infer(repository, conf);

                if(versionWithType != null) {
                    return versionWithType.getVersion();
                }
            }
        }

        return null;

    }

    public JGitFlowSemver(File dir) throws IOException {
        this.dir = dir;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(dir)
            .readEnvironment() // scan environment GIT_* variables
            .findGitDir() // scan up the file system tree
            .build();

    }

}
