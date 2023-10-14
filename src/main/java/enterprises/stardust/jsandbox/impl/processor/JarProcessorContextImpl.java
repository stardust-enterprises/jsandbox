package enterprises.stardust.jsandbox.impl.processor;

import enterprises.stardust.jsandbox.api.processor.JarProcessor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.file.Path;

@Accessors(fluent = true, chain = false)
@AllArgsConstructor
public @Data class JarProcessorContextImpl implements JarProcessor.Context {
    private Path inputJar;
    private final Path transformStore;
    private final String originalFilename;
}
