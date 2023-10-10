package enterprises.stardust.jsandbox.api.launch;

import enterprises.stardust.jsandbox.api.processor.JarProcessor;

public interface Launcher {
    LaunchResult launch(String mainClass, String[] args) throws Throwable;

    JarProcessor jarProcessor();
}
