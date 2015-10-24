package com.quicksign.jgitflowsemver;

import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static org.junit.Assert.*;

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


    @Before
    public void setup() throws GitAPIException, IOException {
        workDir = workTempFolder.getRoot();
        gitDir = new File(workDir, ".git");
        git = Git.init().setDirectory(workTempFolder.getRoot()).setGitDir(gitDir).call();
        repository = git.getRepository();
        assertNotNull(repository.getRef(Constants.HEAD));
        assertTrue(git.status().call().isClean());
        jGitFlowSemver = new JGitFlowSemver(gitDir);
    }

    @Test
    public void test() throws Exception {
        git.add().addFilepattern(appendToFile("README", "Line 1\n").getName()).call();
        git.commit().setMessage("Line 1").call();

        assertEquals(Version.valueOf("0.0.0-1+sha." + sha()), jGitFlowSemver.infer());

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
