package enterprises.stardust.jsandbox.api.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public interface ClassTransformingProcessor extends JarEntryProcessor {
    @Override
    default byte @Nullable [] process(@NotNull String entryPath, byte @NotNull [] buffer) {
        if (!entryPath.endsWith(".class")) {
            return null;
        }
        String className = entryPath
                .substring(0, entryPath.length() - ".class".length())
                .replace('/', '.');
        byte[] newBuffer = processClass(className, buffer);
        if (newBuffer == null || Arrays.equals(newBuffer, buffer)) {
            return null;
        }
        return newBuffer;
    }

    byte @Nullable [] processClass(@NotNull String className, byte @NotNull [] buffer);

    @Override
    default boolean shouldProcess(@NotNull String entryPath) {
        return entryPath.endsWith(".class") &&
                !entryPath.endsWith("module-info.class") &&
                shouldProcessClass(
                        entryPath.substring(0, entryPath.length() - ".class".length())
                                .replace('/', '.')
                );
    }

    default boolean shouldProcessClass(@NotNull String className) {
        return true;
    }
}
