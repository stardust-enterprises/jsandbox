package enterprises.stardust.jsandbox.api.processor;

import java.io.IOException;
import java.nio.file.Path;

public interface Preprocessor {
    Path processJar(Path inputJar) throws IOException;

    void registerJarProcessor(JarProcessor... processors);

    void registerEntryProcessor(JarEntryProcessor... processors);
}
