package enterprises.stardust.jsandbox.impl.launch.internal;

import enterprises.stardust.jsandbox.api.launch.LaunchContext;
import enterprises.stardust.jsandbox.api.launch.LaunchResult;
import enterprises.stardust.jsandbox.impl.launch.HookableLauncher;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

@RequiredArgsConstructor
public class InternalLauncher extends HookableLauncher {
    private final boolean threaded;

    @Override
    public LaunchResult launchImpl(LaunchContext context) throws Throwable {
        Thread thread = Thread.currentThread();

        ClassLoader old = thread.getContextClassLoader();
        InternalClassLoader loader = new InternalClassLoader(
                context.classpath().stream().map(Path::toUri).map(it -> {
                    try {
                        return it.toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray(URL[]::new),
                old
        );
        thread.setContextClassLoader(loader);

        Class<?> mainClass = loader.loadClass(context.mainClass());
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle mainMethod = lookup.findStatic(mainClass, "main", MethodType.methodType(void.class, String[].class));
        //noinspection ConfusingArgumentToVarargsMethod
        mainMethod.invokeExact(context.args().toArray(new String[0]));

        thread.setContextClassLoader(old);

        return new Result(0);
    }

    private static @Data class Result implements LaunchResult {
        private final int returnValue;
        private final boolean finished = true;
    }
}
