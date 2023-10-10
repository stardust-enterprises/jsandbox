package enterprises.stardust.jsandbox.impl.launch;

import enterprises.stardust.jsandbox.impl.launch.internal.InternalClassLoader;
import enterprises.stardust.jsandbox.api.launch.LaunchResult;
import enterprises.stardust.jsandbox.api.launch.Launcher;
import enterprises.stardust.jsandbox.api.processor.JarProcessor;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;

public class InternalLauncher implements Launcher {
    private final InternalClassLoader loader;

    public InternalLauncher(Set<URI> classpath) {
        this.loader = new InternalClassLoader(classpath.stream().map(it -> {
            try {
                return it.toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }).toArray(URL[]::new), this.getClass().getClassLoader());
    }

    @Override
    public LaunchResult launch(String mainClass, String[] args) throws Throwable {
        Thread thread = Thread.currentThread();

        ClassLoader old = thread.getContextClassLoader();
        thread.setContextClassLoader(loader);

        Class<?> main = loader.loadClass(mainClass);
        main.getMethod("main", String[].class).invoke(null, (Object) args);

        thread.setContextClassLoader(old);

        return () -> true;
    }

    @Override
    public JarProcessor jarProcessor() {
        return null;
    }
}
