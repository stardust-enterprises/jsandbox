package enterprises.stardust.jsandbox.impl.plugin;

import enterprises.stardust.jsandbox.api.plugin.Plugin;
import enterprises.stardust.jsandbox.api.plugin.PluginMetadata;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = false)
public @Data class PluginMetadataImpl implements PluginMetadata {
    private final String id;
    private final String name;
    private final String version;
    private final Plugin plugin;

    static PluginMetadata from(Plugin.Info info, Plugin plugin) {
        return new PluginMetadataImpl(info.id(), info.name(), info.version(), plugin);
    }
}
