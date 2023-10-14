package enterprises.stardust.jsandbox.api.launch;

public interface Launcher {
    LaunchResult launch(LaunchContext context) throws Throwable;

    void registerLaunchHook(Hook... hooks);

    interface Hook {
        void preLaunch(LaunchContext context);
    }
}
