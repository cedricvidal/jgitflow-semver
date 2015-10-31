package com.quicksign.jgitflowsemver.strategy;

import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.version.InferredVersion;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class JGitflowInferencer {
    public InferredVersion infer(Git git, GitflowVersioningConfiguration conf) throws IOException, GitAPIException {
        for (Strategy strategy : Strategy.STRATEGIES) {
            if(strategy.canInfer(git.getRepository(), conf)) {
                final InferredVersion inferredVersion = strategy.infer(git, conf);
                if(inferredVersion != null) {
                    return inferredVersion;
                }
            }
        }
        return null;
    }
}
