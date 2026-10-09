package io.jenkins.plugins.artifactstab;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Functions;
import hudson.model.Run;
import java.util.Collection;
import java.util.Collections;
import jenkins.model.Tab;
import jenkins.model.TransientActionFactory;
import jenkins.model.experimentalflags.UserExperimentalFlag;

@Extension(ordinal = Integer.MAX_VALUE - 3)
public class ArtifactsTabFactory extends TransientActionFactory<Run> {

    // NewBuildPageUserExperimentalFlag is @Restricted(NoExternalUse), so look it up by name
    private static final String NEW_BUILD_PAGE_FLAG =
            "jenkins.model.experimentalflags.NewBuildPageUserExperimentalFlag";

    @Override
    public Class<Run> type() {
        return Run.class;
    }

    @NonNull
    @Override
    public Collection<? extends Tab> createFor(@NonNull Run target) {
        // null for anonymous users or if the flag class is gone
        Boolean isExperimentalUiEnabled = UserExperimentalFlag.getFlagValueForCurrentUser(NEW_BUILD_PAGE_FLAG);

        if (!Boolean.TRUE.equals(isExperimentalUiEnabled)) {
            return Collections.emptySet();
        }

        if (Functions.isArtifactsPermissionEnabled() && !target.hasPermission(Run.ARTIFACTS)) {
            return Collections.emptySet();
        }

        if (!target.getHasArtifacts()) {
            return Collections.emptySet();
        }

        return Collections.singleton(new ArtifactsTab(target));
    }
}
