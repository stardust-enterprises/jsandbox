package enterprises.stardust.jsandbox.defaults.process.runtime;

import enterprises.stardust.jsandbox.api.processor.ClassTransformingProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RuntimeBuilderTransformer implements ClassTransformingProcessor {
    @Override
    public byte @Nullable [] processClass(@NotNull String className, byte @NotNull [] buffer) {
        //TODO: find all types declared and take only those from the standard rt
        //      to bundle into our own runtime, in order to have a minimalist-ish runtime
        //TODO: dynamically add that runtime into the input jar(s)??? sandbox-on-the-go???????
        //      could potentially be cool idk :3
        return null;
    }
}
