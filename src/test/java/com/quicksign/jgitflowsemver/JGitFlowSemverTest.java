package com.quicksign.jgitflowsemver;

import com.atlassian.jgitflow.core.JGitFlow;
import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.quicksign.jgitflowsemver.dsl.GitflowVersioningConfiguration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

/**
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class JGitFlowSemverTest {

    @Rule
    public TemporaryFolder workTempFolder = new TemporaryFolder();
    private File workDir;
    private Git git;
    private File gitDir;
    private Repository repository;
    private JGitFlowSemver jGitFlowSemver;
    private GitflowVersioningConfiguration conf;
    private Logger confLoggerMock;


    @Before
    public void setup() throws GitAPIException, IOException {
        workDir = workTempFolder.getRoot();
        gitDir = new File(workDir, ".git");
        git = Git.init().setDirectory(workTempFolder.getRoot()).setGitDir(gitDir).call();
        repository = git.getRepository();
        assertNotNull(repository.getRef(Constants.HEAD));
        assertTrue(git.status().call().isClean());

        conf = new GitflowVersioningConfiguration();
        confLoggerMock = Mockito.mock(Logger.class);
        conf.setLogger(confLoggerMock);

        jGitFlowSemver = new JGitFlowSemver(gitDir, conf);

//        System.out.println("Git dir: " + gitDir);
    }

    @Test
    public void normal() throws Exception {

        // Init repo on master

        git.add().addFilepattern(appendToFile("README", "Line 1\n").getName()).call();
        git.commit().setMessage("Line 1").call();

        assertEquals(Version.valueOf("0.0.0-1+sha." + sha()), jGitFlowSemver.infer());

        // Add file on develop
        final JGitFlow jGitFlow = JGitFlow.init(workDir);

        git.checkout().setName("develop").call();
        assertEquals("develop", repository.getBranch());

        assertEquals(Version.valueOf("0.0.0-dev.1+sha." + sha()), jGitFlowSemver.infer());

        git.add().addFilepattern(appendToFile("README", "Line 2\n").getName()).call();
        git.commit().setMessage("Line 2").call();

        assertEquals(Version.valueOf("0.0.0-dev.2+sha." + sha()), jGitFlowSemver.infer());

        // Add Feature
        jGitFlow.featureStart("first").call();

        git.add().addFilepattern(appendToFile("README", "Feature 1\n").getName()).call();
        git.commit().setMessage("Feature 1").call();

        assertEquals(Version.valueOf("0.0.0-feature.first.3+sha." + sha()), jGitFlowSemver.infer());

        // Finish feature

        jGitFlow.featureFinish("first").call();

        assertEquals(Version.valueOf("0.0.0-dev.4+sha." + sha()), jGitFlowSemver.infer());

        // Start release

        jGitFlow.releaseStart("0.1.0").call();

        assertEquals(Version.valueOf("0.1.0-pre.4+sha." + sha()), jGitFlowSemver.infer());

        git.add().addFilepattern(appendToFile("README", "Prepare release\n").getName()).call();
        git.commit().setMessage("Prepare release").call();

        assertEquals(Version.valueOf("0.1.0-pre.5+sha." + sha()), jGitFlowSemver.infer());

        // Finish release

        jGitFlow.releaseFinish("0.1.0").call();

        git.checkout().setName("master").call();
        assertEquals("master", repository.getBranch());
        assertEquals(Version.valueOf("0.1.0"), jGitFlowSemver.infer());

        // Back to develop

        git.checkout().setName("develop").call();
        assertEquals(Version.valueOf("0.1.0-dev.1+sha." + sha()), jGitFlowSemver.infer());

        // Create hotfix

        jGitFlow.hotfixStart("whoops").call();
        git.add().addFilepattern(appendToFile("README", "Whoops\n").getName()).call();
        git.commit().setMessage("Whoops").call();

        assertEquals(Version.valueOf("0.1.0-fix.whoops.1+sha." + sha()), jGitFlowSemver.infer());

        jGitFlow.hotfixFinish("whoops").call();
        assertEquals("develop", repository.getBranch());
        assertEquals(Version.valueOf("0.1.0-dev.4+sha." + sha()), jGitFlowSemver.infer());

        // Detached mode (often the case in CI servers)

        git.checkout().setName("HEAD").call(); // This checks out develop in detached mode
        assertEquals(Version.valueOf("0.1.0-detached.4+sha." + sha()), jGitFlowSemver.infer());

        // Detached forceBranch (used to force branch name on CI servers which often checkout in detached mode)
        conf.setForceBranch("develop");
        assertEquals(Version.valueOf("0.1.0-dev.4+sha." + sha()), jGitFlowSemver.infer());

        // ForceBranch works only in detached mode so warn user
        git.checkout().setName("master").call();
        assertEquals(Version.valueOf("0.1.0-2+sha." + sha()), jGitFlowSemver.infer());
        verify(confLoggerMock).warn("Ignoring forceBranch as repository is not detached");

    }

    private String sha() throws IOException {
        return repository.resolve(Constants.HEAD).abbreviate(7).name();
    }

    private File appendToFile(String path, String content) throws IOException {
        final File file = new File(workDir, path);
        Files.write(content, file, Charsets.UTF_8);
        return file;
    }

}
