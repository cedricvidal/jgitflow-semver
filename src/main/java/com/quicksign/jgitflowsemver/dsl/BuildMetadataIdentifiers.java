package com.quicksign.jgitflowsemver.dsl;

/**
 * The holder for <em>build metadata identifiers</em> according to <em>semantic versioning</em>.
 *
 * @author Max Käufer
 * @author <a href="mailto:cedric.vidal@quicksign.com">Cedric Vidal, Quicksign</a>
 */
public class BuildMetadataIdentifiers {
    /**
     * Helper method to allow <em>keyword-based configuration</em> of the <code>sha</code> property
     *
     * @param sha
     */
    public BuildMetadataIdentifiers sha(final String sha) {
        setSha(sha);
        return this;
    }

    /**
     * Helper method to allow <em>keyword-based configuration</em> of the <code>dirty</code> property
     *
     * @param dirty
     */
    public BuildMetadataIdentifiers dirty(final String dirty) {
        setDirty(dirty);
        return this;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getDirty() {
        return dirty;
    }

    public void setDirty(String dirty) {
        this.dirty = dirty;
    }

    /**
     * The static text that is used before the actual <em>SHA</em> of the current commit
     */
    private String sha = "sha";
    /**
     * The static text that is appended to indicate that the repository is dirty
     */
    private String dirty = "dirty";
}
