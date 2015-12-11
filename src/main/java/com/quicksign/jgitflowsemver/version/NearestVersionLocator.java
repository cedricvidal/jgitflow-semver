package com.quicksign.jgitflowsemver.version;

import com.github.zafarkhaja.semver.ParseException;
import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.quicksign.jgitflowsemver.util.JGitUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

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
     * @param git      the repository to locate the tag in
     * @return the version corresponding to the nearest tag
     */
    public NearestVersion locate(final Git git) throws GitAPIException, IOException {
        // Look for in progress release branch
        List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        final Ref releaseInProgress = Iterables.find(branches, new Predicate<Ref>() {
            @Override
            public boolean apply(Ref input) {
                return input.getName().startsWith("refs/heads/release/");
            }
        }, null);


        // If a release is in progress
        NearestVersion nearestVersion = null;
        if(releaseInProgress != null) {
            Version releaseInProgressVersion = null;
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found in progress release branch " + releaseInProgress.getName());
            }
            try {
                releaseInProgressVersion = Version.valueOf(releaseInProgress.getName().substring("refs/heads/release/".length()));
            } catch (ParseException e) {
                if(LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Ignoring release branch (name is not semver)" + releaseInProgress.getName());
                }
            }

            if(releaseInProgressVersion != null) {
                final ObjectId releaseId = releaseInProgress.getObjectId();

                int distance = distanceFromReleaseMergeBase(git, releaseId);

                // If we're at least 1 commit away from release branch merge base
                // then consider the release branch merge base as the nearest version
                // otherwise fallback to locate nearest release tag
                if(distance > 0) {
                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Extracted version {} from release branch name {} at {} [MERGE_BASE+{}]", releaseInProgressVersion.getNormalVersion(), releaseId, distance);
                    }
                    nearestVersion = new NearestVersion(releaseInProgressVersion, distance);
                } else {
                    LOGGER.debug("On or behind release branch merge base so doing as if there was no release in progress");
                }
            }
        }

        // If nearest version is not a release branch merge base then locate nearest release tag
        if(nearestVersion == null) {
            nearestVersion = localTag(git);
            final ObjectId releaseId = nearestVersion.getObjectId();
            if(releaseId != null) {
                final int distance = distanceFromReleaseMergeBase(git, releaseId);
                nearestVersion = new NearestVersion(nearestVersion.getAny(), distance);
            }
        }

        return nearestVersion;
    }

    private int distanceFromReleaseMergeBase(Git git, ObjectId releaseId) throws IOException, GitAPIException {
        final ObjectId head = git.getRepository().resolve(Constants.HEAD);
        final ObjectId mergeBase = JGitUtil.mergeBase(git.getRepository(), releaseId, head);
        return size(git.log().addRange(mergeBase, head).call());
    }

    private NearestVersion localTag(Git git) throws IOException, GitAPIException {
        Repository repository = git.getRepository();
        final String string = repository.getConfig().getString(CONFIG_SECTION_GITFLOW, CONFIG_SUBSECTION_PREFIX, CONFIG_VERSION_TAG);
        final String versionPrefix = (string != null) ? string : DEFAULT_PREFIX_VERSION;

        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Locating tags on branch " + repository.getBranch());
        }

        final ObjectId head = repository.resolve(Constants.HEAD);

        Version minVersion = null;
        Integer minDistance = Integer.MAX_VALUE;
        ObjectId minObjectId = null;

        for(Ref tag : git.tagList().call()) {
            tag = repository.peel(tag);
            ObjectId tagCommit = tag.getPeeledObjectId();
            if(tagCommit == null) {
                tagCommit = tag.getObjectId();
            }
            String tagName = tag.getName();
            tagName = tagName.substring(tagName.lastIndexOf('/') + 1);
            Version version = null;

            try {
                final String versionName = tagName.substring(0, 1).equals(versionPrefix) ? tagName.substring(1) : tagName;
                version = Version.valueOf(versionName);
            } catch (Exception e) {
                if(LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Ignoring tag " + tagName + " at " + tag.getObjectId().abbreviate(7).name() + "(not semver)");
                }
            }

            if (version != null) {
                int distance = 0;
                Version candidateVersion = null;
                if (tagCommit.equals(head)) {
                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Extracted version {} from tag {} at {} [HEAD+{}] (on HEAD)", version.getNormalVersion(), tagName, tag.getObjectId().abbreviate(7).name(), distance);
                    }
                    candidateVersion = version;
                } else {

                    if (JGitUtil.isAncestorOf(repository, tagCommit, head)) {
                        candidateVersion = version;

                        distance = size(git.log().addRange(tagCommit, head).call());

                        if(LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Extracted version {} from tag {} at {} [HEAD+{}] (ancestor of HEAD)", version.getNormalVersion(), tagName, tag.getObjectId().abbreviate(7).name(), distance);
                        }

                    } else {
                        if(LOGGER.isTraceEnabled()) {
                            LOGGER.trace("Ignoring tag " + tagName + " at " + tag.getObjectId().abbreviate(7).name() + " (not an ancestor of HEAD)");
                        }
                    }

                }

                if(distance < minDistance && candidateVersion != null) {
                    minVersion = candidateVersion;
                    minObjectId = tagCommit;
                    minDistance = distance;
                }

            }
        }

        Version anyVersion = minVersion != null ? minVersion : Version.valueOf("0.0.0");
        int distanceFromAny = minVersion != null ? minDistance : size(git.log().call());

        if(LOGGER.isInfoEnabled()) {
            LOGGER.info("Closest tag is {}{}" + " [HEAD+{}]", anyVersion.getNormalVersion(), minObjectId != null ? " at " + minObjectId.abbreviate(7).name() : "", distanceFromAny);
        }

        return new NearestVersion(anyVersion, distanceFromAny).objectId(minObjectId);
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
