package com.quicksign.jgitflowsemver.dsl;

/**
 * The holder for <em>pre-release identifiers</em> according to <em>semantic versioning</em>.
 *
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class PreReleaseIdentifiers {
    /**
     * Helper method to allow <em>keyword-based configuration</em> of the <code>release</code> property
     *
     * @param release
     */
    public void release(final String release) {
        setRelease(release);
    }

    /**
     * Helper method to allow <em>keyword-based configuration</em> of the <code>preRelease</code> property
     *
     * @param preRelease
     */
    public void preRelease(final String preRelease) {
        setPreRelease(preRelease);
    }

    /**
     * Helper method to allow <em>keyword-based configuration</em> of the <code>develop</code> property
     *
     * @param develop
     */
    public void develop(final String develop) {
        setDevelop(develop);
    }

    /**
     * Helper method to allow <em>keyword-based configuration</em> of the <code>feature</code> property
     *
     * @param feature
     */
    public void feature(final String feature) {
        setFeature(feature);
    }

    /**
     * Helper method to allow <em>keyword-based configuration</em> of the <code>hotfix</code> property
     *
     * @param hotfix
     */
    public void hotfix(final String hotfix) {
        setHotfix(hotfix);
    }

    /**
     * Helper method to allow <em>keyword-based configuration</em> of the <code>support</code> property
     *
     * @param support
     */
    public void support(final String support) {
        setSupport(support);
    }

    /**
     * Helper method to allow <em>keyword-based configuration</em> of the <code>detachedHead</code> property
     *
     * @param detachedHead
     */
    public void detachedHead(final String detachedHead) {
        setDetachedHead(detachedHead);
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getPreRelease() {
        return preRelease;
    }

    public void setPreRelease(String preRelease) {
        this.preRelease = preRelease;
    }

    public String getDevelop() {
        return develop;
    }

    public void setDevelop(String develop) {
        this.develop = develop;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getHotfix() {
        return hotfix;
    }

    public void setHotfix(String hotfix) {
        this.hotfix = hotfix;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    public String getDetachedHead() {
        return detachedHead;
    }

    public void setDetachedHead(String detachedHead) {
        this.detachedHead = detachedHead;
    }

    /**
     * The static text that is used to indicate that the current branch is the <strong>production release</strong>
     * branch according to <em>Gitflow</em>'s semantics
     */
    private String release = "";
    /**
     * The static text that is used to indicate that the current branch is a <strong>pre-release / next
     * release</strong> branch according to <em>Gitflow</em>'s semantics
     */
    private String preRelease = "pre";
    /**
     * The static text that is used to indicate that the current branch is the <strong>develop</strong> branch according
     * to <em>Gitflow</em>'s semantics
     */
    private String develop = "dev";
    /**
     * The static text that is used to indicate that the current branch is a <strong>feature</strong> branch according
     * to <em>Gitflow</em>'s semantics
     */
    private String feature = "feature";
    /**
     * The static text that is used to indicate that the current branch is a <strong>hotfix</strong> branch according
     * to <em>Gitflow</em>'s semantics
     */
    private String hotfix = "fix";
    /**
     * The static text that is used to indicate that the current branch is a <strong>support</strong> branch according
     * to <em>Gitflow</em>'s semantics
     */
    private String support = "support";
    /**
     * The static text that is used to indicate that the current commit is a <strong>detached head</strong>
     */
    private String detachedHead = "detached";
}
