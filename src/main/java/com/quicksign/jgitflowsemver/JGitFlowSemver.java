package com.quicksign.jgitflowsemver;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.android.AndroidVersionCodeBuilder;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import com.quicksign.jgitflowsemver.strategy.JGitflowInferencer;
import com.quicksign.jgitflowsemver.version.InferredVersion;
import com.quicksign.jgitflowsemver.version.SemVersionBuilder;
import org.apache.commons.cli.*;
import org.eclipse.jgit.api.Git;
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
    private final Git git;

    public static void main(String[] args) throws Exception {

        // create Options object
        Options options = new Options();

        options.addOption("b", "branch", true, "force branch name in case of a detached repo");
        options.addOption("s", "snapshot", false, "Use Maven SNAPSHOT instead of semver build metadata");
        options.addOption("m", "maven", false, "Maven compatible semver versions");
        options.addOption("a", "androidVersionCode", false, "Android version code");
        options.addOption("e", "envPrefix", true, "Environment prefix to export variables to");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        final GitflowVersioningConfiguration conf = new GitflowVersioningConfiguration();
        String envPrevix = null;
        boolean inferAndroidVersionCode = false;
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            if(line.hasOption('b')) {
                conf.setForceBranch(line.getOptionValue('b'));
            }
            if(line.hasOption('s')) {
                conf.useMavenSnapshot();
            }
            if(line.hasOption('m')) {
                conf.mavenCompatibility();
            }
            envPrevix = line.getOptionValue('e');
            inferAndroidVersionCode = line.hasOption('a');
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
            InferredVersion v = null;
            final File root = new File(args[0]).getAbsoluteFile();
            final File dir = new File(root, ".git");
            conf.setRepositoryRoot(dir.getPath());

            if(new File(root, "pom.xml").exists()) {
                LOGGER.debug("Detected Maven pom.xml so activating Maven compatibility");
                conf.mavenCompatibility();
            }

            v = new JGitFlowSemver(dir, conf).infer();
            Integer androidVersionCode = null;
            if(inferAndroidVersionCode) {
                androidVersionCode = new AndroidVersionCodeBuilder().build(v, conf);
            }
            Version semVersion = new SemVersionBuilder().build(v, conf);

            String adjusted = semVersion.toString();
            if(conf.isMavenCompatibility()) {
                adjusted = adjusted.replaceAll("\\+", ".");
            }

            if(envPrevix == null) {
                if(inferAndroidVersionCode) {
                    adjusted = androidVersionCode.toString();
                }
                System.out.println(adjusted);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("export " + envPrevix + "_VERSION=" + adjusted + "\n");
                if(inferAndroidVersionCode) {
                    sb.append("export " + envPrevix + "_ANDROID_VERSION_CODE=" + androidVersionCode + "\n");
                }
                System.out.println(sb);
            }
        } catch (Exception e) {
            System.err.println("An error ocured: " + e);
            LOGGER.error("An error occured", e);
            System.exit(2);
        }
    }

    private static void printUsage(Options options, PrintStream stream) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("jgitflow-semver [options]... <path>", options, false);
    }

    public InferredVersion infer() throws Exception {

        Ref headRef = repository.getRef( Constants.HEAD );
        if( headRef == null || headRef.getObjectId() == null ) {
            LOGGER.warn("No commit yet");
        }

        final InferredVersion version = new JGitflowInferencer().infer(git, conf);
        return version;
    }

    public JGitFlowSemver(File dir, GitflowVersioningConfiguration conf) throws IOException {
        this.dir = dir;
        this.conf = conf;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(dir)
            .readEnvironment() // scan environment GIT_* variables
            .findGitDir() // scan up the file system tree
            .build();
        this.git = Git.wrap(repository);
    }

}
