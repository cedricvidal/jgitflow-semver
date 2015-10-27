package com.quicksign.jgitflowsemver;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.strategy.Strategy;
import com.quicksign.jgitflowsemver.version.VersionWithType;
import org.apache.commons.cli.*;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class JGitFlowSemver {
    private final Repository repository;
    private final File dir;

    private static final Logger LOGGER = LoggerFactory.getLogger(JGitFlowSemver.class);
    private final GitflowVersioningConfiguration conf;

    public static void main(String[] args) throws Exception {

        // create Options object
        Options options = new Options();

        // add t option
        options.addOption("b", true, "force branch name in case of a detached repo");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        final GitflowVersioningConfiguration conf = new GitflowVersioningConfiguration();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            if(line.hasOption('b')) {
                conf.setForceBranch(line.getOptionValue('b'));
            }
            args = line.getArgs();
        }
        catch( ParseException exp ) {
            printUsage(options, System.err);
            System.exit(1);
        }

        if(args.length != 1) {
            printUsage(options, System.err);
            System.exit(1);
        }

        try {
            Version v = null;
            final File dir = new File(args[0], ".git");
            conf.setRepositoryRoot(dir.getPath());

            v = new JGitFlowSemver(dir, conf).infer();
            System.out.println(v);
        } catch (Exception e) {
            System.err.println("An error ocured: " + e.getMessage());
            printUsage(options, System.err);
            System.out.println("unknown");
        }
    }

    private static void printUsage(Options options, PrintStream stream) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("jgitflow-semver [options]... <path>", options, false);
    }

    public Version infer() throws Exception {

        Ref headRef = repository.getRef( Constants.HEAD );
        if( headRef == null || headRef.getObjectId() == null ) {
            LOGGER.warn("No commit yet");
        }

        VersionWithType versionWithType = null;
        for (Strategy strategy : Strategy.STRATEGIES) {
            if(strategy.canInfer(repository, conf)) {
                versionWithType = strategy.infer(repository, conf);

                if(versionWithType != null) {
                    return versionWithType.getVersion();
                }
            }
        }

        return null;

    }

    public JGitFlowSemver(File dir, GitflowVersioningConfiguration conf) throws IOException {
        this.dir = dir;
        this.conf = conf;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(dir)
            .readEnvironment() // scan environment GIT_* variables
            .findGitDir() // scan up the file system tree
            .build();

    }

}
