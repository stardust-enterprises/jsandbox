package enterprises.stardust.jsandbox.api.plugin;

import enterprises.stardust.jsandbox.api.JSandbox;
import enterprises.stardust.jsandbox.api.launch.Launcher;

public interface PluginContext {
    JSandbox sandbox();

    Launcher launcher();

    PluginMetadata metadata();
}
