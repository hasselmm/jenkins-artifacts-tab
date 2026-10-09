package io.jenkins.plugins.artifactstab;

import hudson.model.Actionable;
import jenkins.model.Tab;

/**
 * Tab linking to the archived artifacts of a build on the new build page.
 * <p>
 * The empty {@code jenkins/model/Tab/action.jelly} from core is inherited,
 * so this action does not show up in the classic sidepanel.
 */
public class ArtifactsTab extends Tab {

    public ArtifactsTab(Actionable object) {
        super(object);
    }

    @Override
    public String getIconFileName() {
        return "symbol-cube";
    }

    @Override
    public String getDisplayName() {
        return "Artifacts";
    }

    @Override
    public String getUrlName() {
        // Served by Run#doArtifact (directory browser)
        return "artifact";
    }
}
