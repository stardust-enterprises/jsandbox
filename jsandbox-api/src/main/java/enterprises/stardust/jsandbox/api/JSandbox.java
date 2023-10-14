package enterprises.stardust.jsandbox.api;

import enterprises.stardust.jsandbox.api.launch.LaunchResult;
import enterprises.stardust.jsandbox.api.launch.LaunchType;
import enterprises.stardust.jsandbox.api.plugin.PluginManager;
import enterprises.stardust.jsandbox.api.processor.Preprocessor;

import java.io.File;
import java.nio.file.Path;

public interface JSandbox {
    //PermissionManager permissionManager();
    PluginManager pluginManager();
    Preprocessor preprocessor();

    LaunchResult launch(String mainClass, String[] args) throws Throwable;

    static Builder builder() {
        return APIService.provider().provideBuilder();
    }

    interface Builder {
        Builder withClasspath(String... classpath);
        Builder withClasspath(File... classpath);
        Builder withClasspath(Path... classpath);

        Builder withPlugin(String... plugin);
        Builder withPlugin(File... plugin);
        Builder withPlugin(Path... plugin);

        Builder launchType(LaunchType launchType);

        JSandbox build();
    }
}