package enterprises.stardust.jsandbox.api.plugin;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface PluginManager {
    Set<PluginMetadata> getLoadedPlugins();

    @Nullable PluginMetadata getPluginMetadata(String id);

    default boolean isPluginLoaded(String id) {
        return getPluginMetadata(id) != null;
    }
}
