package enterprises.stardust.jsandbox.api;

import enterprises.stardust.jsandbox.api.launch.Launcher;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

public interface JSandbox {
    //PermissionManager permissionManager();
    Launcher launcher();

    default void launch(String mainClass, String[] args) throws Throwable {
        launcher().launch(mainClass, args);
    }

    static Builder builder() {
        return APIService.provider().provideBuilder();
    }

    interface Builder {
        Builder withClasspath(URI... classpath);
        Builder withClasspath(URL... classpath);
        Builder withClasspath(String... classpath);
        Builder withClasspath(File... classpath);
        Builder withClasspath(Path... classpath);

        Builder withProcessor(URI... classpath);
        Builder withProcessor(URL... classpath);
        Builder withProcessor(String... classpath);
        Builder withProcessor(File... classpath);
        Builder withProcessor(Path... classpath);

        Builder fork(boolean fork);

        JSandbox build();
    }
}