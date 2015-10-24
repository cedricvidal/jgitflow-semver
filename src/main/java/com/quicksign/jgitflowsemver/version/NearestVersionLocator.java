package com.quicksign.jgitflowsemver.version;

import com.github.zafarkhaja.semver.Version;
import com.quicksign.jgitflowsemver.util.JGitUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.google.common.collect.Iterables.size;

/**
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class NearestVersionLocator {

    /**
     * Locate the nearest version in the given repository starting from the <strong>current HEAD</strong>.
     * <p>
     * <p>
     * All <em>tag</em> names are parsed to determine if they are valid version strings.
     * Tag names can begin with <code>DEFAULT_PREFIX_VERSION</code> (which will be stripped off).
     * </p>
     * <p>
     * <p>
     * The nearest tag is determined by getting a commit log between the tag and {@code HEAD}.
     * The version tag with the smallest log from a pure count of commits will have its version returned.
     * <strong>If two version tags have a log of the same size, the versions will be compared to find the one with the
     * highest precedence according to semver rules</strong>.
     * For example, {@code 1.0.0} has higher precedence than {@code 1.0.0-rc.2}.
     * For tags with logs of the same size and versions of the same precedence it is undefined which will be returned.
     * </p>
     * <p>
     * <p>
     * Two versions will be returned: the <em>"any"</em> version and the <em>"normal"</em> version.
     * <em>"Any"</em> is the absolute nearest tagged version.
     * <em>"Normal"</em> is the nearest tagged version that <strong>does not</strong> include a <em>pre-release</em>
     * segment.
     * </p>
     *
     * @param repository      the repository to locate the tag in
     * @return the version corresponding to the nearest tag
     */
    public NearestVersion locate(final Repository repository) throws GitAPIException, IOException {
        final String string = repository.getConfig().getString(CONFIG_SECTION_GITFLOW, CONFIG_SUBSECTION_PREFIX, CONFIG_VERSION_TAG);
        final String versionPrefix = (string != null) ? string : DEFAULT_PREFIX_VERSION;

        LOGGER.debug("Locate beginning on branch: " + repository.getBranch());

        final ObjectId head = repository.resolve(Constants.HEAD);

        Version minVersion = null;
        Integer minDistance = Integer.MAX_VALUE;

        for(Ref tag : new Git(repository).tagList().call()) {
            tag = repository.peel(tag);
            ObjectId tagCommit = tag.getPeeledObjectId();
            String tagName = tag.getName();
            tagName = tagName.substring(tagName.lastIndexOf('/') + 1);
            Version version = Version.valueOf(tagName.substring(0, 1).equals(versionPrefix) ? tagName.substring(1) : tagName);
            LOGGER.debug("Tag " + tagName + " (" + tag.getObjectId().abbreviate(7) + ") parsed as " + version + " version.");

            if (version != null) {
                int distance = 0;
                Version candidateVersion = null;
                if (tagCommit.equals(head)) {
                    LOGGER.debug("Tag " + tagName + " is at head. Including as candidate.");
                    candidateVersion = version;
                } else {

                    if (JGitUtil.isAncestorOf(repository, tagCommit, head)) {
                        LOGGER.debug("Tag " + String.valueOf(tagName) + " is an ancestor of HEAD. Including as a candidate.");
                        candidateVersion = version;

                        distance = size(new Git(repository).log().addRange(tagCommit, head).call());

                        LOGGER.debug("Reachable commits after tag " + tag.getName() + ": {}", distance);

                    } else {
                        LOGGER.debug("Tag " + tagName + " is not an ancestor of HEAD. Excluding as a candidate.");
                    }

                }

                if(distance < minDistance && candidateVersion != null) {
                    minVersion = candidateVersion;
                    minDistance = distance;
                }

            }
        }

        Version anyVersion = minVersion != null ? minVersion : Version.valueOf("0.0.0");
        int distanceFromAny = minVersion != null ? minDistance : size(new Git(repository).log().call());

        return new NearestVersion(anyVersion, distanceFromAny);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NearestVersionLocator.class);

    /**
     * The name of the config section of <em>Gitflow</em> plugins in <code>.git/config</code>
     */
    private static final String CONFIG_SECTION_GITFLOW = "gitflow";
    /**
     * The name of the config sub-section for prefixes of <em>Gitflow</em> plugins in <code>.git/config</code>
     */
    private static final String CONFIG_SUBSECTION_PREFIX = "prefix";
    /**
     * The name of the config key for the prefix of version tags of <em>Gitflow</em> plugins in <code>.git/config</code>
     */
    private static final String CONFIG_VERSION_TAG = "versionTag";
    /**
     * The default value of the config value of <code>CONFIG_VERSION_TAG</code>.
     * This is used if the respective sections are missing in <code>.git/config</code>, e.g. if You are not using a
     * <em>Gitflow</em> plugin for <em>Git</em>.
     */
    private static final String DEFAULT_PREFIX_VERSION = "v";
}
