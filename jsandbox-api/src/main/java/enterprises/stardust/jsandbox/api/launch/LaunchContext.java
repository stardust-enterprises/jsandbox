package enterprises.stardust.jsandbox.api.launch;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public interface LaunchContext {
    Set<Path> classpath();
    void classpath(Set<Path> classpath);

    String mainClass();
    void mainClass(String mainClass);

    List<String> args();
    void args(List<String> args);

    LaunchType launchType();
    void launchType(LaunchType launchType);
}
