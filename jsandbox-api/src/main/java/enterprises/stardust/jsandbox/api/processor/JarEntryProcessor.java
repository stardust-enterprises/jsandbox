package enterprises.stardust.jsandbox.api.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@FunctionalInterface
public interface JarEntryProcessor {
    byte @Nullable [] process(@NotNull String entryPath, byte @NotNull [] buffer);

    default boolean shouldProcess(@NotNull String entryPath) {
        return true;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Priority {
        int value();
    }
}
