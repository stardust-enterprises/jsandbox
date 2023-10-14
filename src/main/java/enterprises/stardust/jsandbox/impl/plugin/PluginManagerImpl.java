package enterprises.stardust.jsandbox.impl.plugin;

import enterprises.stardust.jsandbox.api.plugin.PluginManager;
import enterprises.stardust.jsandbox.api.plugin.PluginMetadata;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PluginManagerImpl implements PluginManager {
    private final Map<String, PluginMetadata> pluginMetadataMap =
            new HashMap<>();

    void registerPlugin(PluginMetadata metadata) {
        if (pluginMetadataMap.containsKey(metadata.id())) {
            throw new RuntimeException("Plugin with id " +
                    metadata.id() + " already registered");
        }
        pluginMetadataMap.put(metadata.id(), metadata);
    }

    @Override
    public Set<PluginMetadata> getLoadedPlugins() {
        return Collections.unmodifiableSet(new HashSet<>(pluginMetadataMap.values()));
    }

    @Override
    public @Nullable PluginMetadata getPluginMetadata(String id) {
        return pluginMetadataMap.getOrDefault(id, null);
    }
}
