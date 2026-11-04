package io.jenkins.plugins.artifactstab;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Functions;
import hudson.model.Run;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import jenkins.model.Tab;
import jenkins.model.TransientActionFactory;
import jenkins.model.experimentalflags.UserExperimentalFlag;

@Extension(ordinal = Integer.MAX_VALUE - 3)
public class ArtifactsTabFactory extends TransientActionFactory<Run> {

    // NewBuildPageUserExperimentalFlag is @Restricted(NoExternalUse), so look it up by name
    private static final String NEW_BUILD_PAGE_FLAG =
            "jenkins.model.experimentalflags.NewBuildPageUserExperimentalFlag";

    // Finished builds do not gain artifacts later, so their result can be cached.
    // Weak keys, so this cache does not keep builds in memory.
    private final Map<Run<?, ?>, Boolean> hasArtifactsCache = Collections.synchronizedMap(new WeakHashMap<>());

    @Override
    public Class<Run> type() {
        return Run.class;
    }

    // Without this the default of Action.class makes Jenkins consult this factory for every
    // getAction(SomeAction.class) on any build, e.g. once per test case in JUnit's getPreviousResult().
    @Override
    public Class<ArtifactsTab> actionType() {
        return ArtifactsTab.class;
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

        if (!hasArtifacts(target)) {
            return Collections.emptySet();
        }

        return Collections.singleton(new ArtifactsTab(target));
    }

    private boolean hasArtifacts(Run<?, ?> run) {
        if (run.isBuilding()) {
            // Pipelines may archive artifacts at any time while running
            return run.getHasArtifacts();
        }

        final var cached = hasArtifactsCache.get(run);

        if (cached != null) {
            return cached;
        }

        // Computed outside the lock, so that slow artifact managers do not serialize all requests
        final var result = run.getHasArtifacts();
        hasArtifactsCache.put(run, result);
        return result;
    }
}
