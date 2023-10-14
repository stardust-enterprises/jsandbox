package enterprises.stardust.jsandbox.defaults.process.log;

import enterprises.stardust.jsandbox.api.processor.JarEntryProcessor;
import enterprises.stardust.jsandbox.api.processor.JarProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

@JarEntryProcessor.Priority(Integer.MAX_VALUE)
public class JSandboxLoggingJarProcessor implements JarProcessor {
    static final Logger LOGGER = LoggerFactory.getLogger("JarProcessor");

    @Override
    public Path preprocess(Context context) throws IOException {
        LOGGER.trace("Preprocessing {} ({})", context.inputJar(), context.originalFilename());
        return JarProcessor.super.preprocess(context);
    }

    @Override
    public Path postprocess(Context context) throws IOException {
        LOGGER.trace("Postprocessing {}", context.inputJar());
        return JarProcessor.super.postprocess(context);
    }
}
