package enterprises.stardust.jsandbox.impl.launch;

import enterprises.stardust.jsandbox.api.launch.LaunchResult;
import enterprises.stardust.jsandbox.api.launch.Launcher;
import enterprises.stardust.jsandbox.api.processor.JarProcessor;

import java.net.URI;
import java.util.Set;

public class ForkLauncher implements Launcher {
    private final Set<URI> classpath;

    public ForkLauncher(Set<URI> classpath) {
        this.classpath = classpath;
    }

    @Override
    public LaunchResult launch(String mainClass, String[] args) throws Throwable {
        Process process = new ProcessBuilder()
                .command(
                        "java",
                        "-cp",
                        classpath.stream()
                                .map(URI::toString)
                                .reduce((a, b) -> a + ":" + b)
                                .orElse(""),
                        mainClass
                )
                .inheritIO()
                .start();
        return () -> !process.isAlive();
    }

    @Override
    public JarProcessor jarProcessor() {
        return null;
    }
}
