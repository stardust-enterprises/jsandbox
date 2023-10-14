package enterprises.stardust.jsandbox.api.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Plugin {
    void install(PluginContext context);

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Info {
        String id();

        String name() default "";

        String version();
    }
}
