package enterprises.stardust.jsandbox.api.plugin;

import org.jetbrains.annotations.NotNull;

public interface PluginMetadata {
    @NotNull String id();

    @NotNull String name();

    @NotNull String version();

    @NotNull Plugin plugin();

    default @NotNull String pluginClass() {
        return plugin().getClass().getName();
    }
}
