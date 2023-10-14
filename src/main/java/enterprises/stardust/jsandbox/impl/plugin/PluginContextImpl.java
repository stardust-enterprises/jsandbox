package enterprises.stardust.jsandbox.impl.plugin;

import enterprises.stardust.jsandbox.api.JSandbox;
import enterprises.stardust.jsandbox.api.launch.Launcher;
import enterprises.stardust.jsandbox.api.plugin.PluginContext;
import enterprises.stardust.jsandbox.api.plugin.PluginMetadata;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = false)
public @Data class PluginContextImpl implements PluginContext {
    private final JSandbox sandbox;
    private final Launcher launcher;
    private final PluginMetadata metadata;
}
