package enterprises.stardust.jsandbox.impl.launch;

import enterprises.stardust.jsandbox.api.launch.LaunchContext;
import enterprises.stardust.jsandbox.api.launch.LaunchResult;
import enterprises.stardust.jsandbox.api.launch.Launcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class HookableLauncher implements Launcher {
    private final List<Hook> hookList = new ArrayList<>();

    @Override
    public LaunchResult launch(LaunchContext context) throws Throwable {
        hookList.forEach(it -> it.preLaunch(context));
        return launchImpl(context);
    }

    public abstract LaunchResult launchImpl(LaunchContext context) throws Throwable;

    @Override
    public void registerLaunchHook(Hook... hooks) {
        Collections.addAll(hookList, hooks);
    }
}
