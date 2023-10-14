package enterprises.stardust.jsandbox.api.processor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;

public interface JarProcessor {
    default Path preprocess(Context context) throws IOException {
        return context.inputJar();
    }

    default Path postprocess(Context context) throws IOException {
        return context.inputJar();
    }

    default boolean shouldProcess(@NotNull Path jarFile) {
        return true;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Priority {
        int value();
    }

    interface Context {
        Path inputJar();
        Path transformStore();

        /**
         * @return the original file name of the jar file without any trailing extension
         */
        String originalFilename();
    }
}
