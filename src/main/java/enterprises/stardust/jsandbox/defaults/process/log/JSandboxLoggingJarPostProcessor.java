package enterprises.stardust.jsandbox.defaults.process.log;

import enterprises.stardust.jsandbox.api.processor.JarProcessor;

import java.io.IOException;
import java.nio.file.Path;

@JarProcessor.Priority(Integer.MIN_VALUE)
public class JSandboxLoggingJarPostProcessor implements JarProcessor {
    @Override
    public Path postprocess(Context context) throws IOException {
        JSandboxLoggingJarProcessor.LOGGER.trace("Final jar file: {}", context.inputJar());
        return JarProcessor.super.postprocess(context);
    }
}
