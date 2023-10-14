package enterprises.stardust.jsandbox.impl.plugin;

import enterprises.stardust.jsandbox.api.plugin.Plugin;
import enterprises.stardust.jsandbox.api.plugin.PluginMetadata;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ServiceLoader;
import java.util.Set;

public class PluginLoader {
    private static final ServiceLoader<Plugin> INTERNAL_LOADER =
            ServiceLoader.load(Plugin.class, PluginLoader.class.getClassLoader());

    private final PluginManagerImpl pluginManager;
    private final URLClassLoader pluginClassLoader;

    public PluginLoader(PluginManagerImpl pluginManager, Set<Path> pluginPaths) {
        this.pluginManager = pluginManager;
        this.pluginClassLoader = new URLClassLoader(pluginPaths.stream().map(it -> {
            try {
                return it.toUri().toURL();
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }).toArray(URL[]::new), getClass().getClassLoader());
    }

    public void loadPlugins() {
        for (Plugin plugin : INTERNAL_LOADER) {
            loadPlugin(plugin, true);
        }
        for (Plugin plugin : ServiceLoader.load(Plugin.class, pluginClassLoader)) {
            loadPlugin(plugin);
        }
    }

    private void loadPlugin(Plugin plugin) {
        loadPlugin(plugin, false);
    }

    private void loadPlugin(Plugin plugin, boolean allowInternal) {
        Class<?> pluginClass = plugin.getClass();
        if (!allowInternal && pluginClass.getClassLoader() != pluginClassLoader) {
            return;
        }
        Plugin.Info info = pluginClass.getAnnotation(Plugin.Info.class);
        if (info == null) {
            throw new RuntimeException(
                    "Plugin class " +
                            pluginClass.getName() +
                            " does not have @" +
                            Plugin.Info.class.getSimpleName() +
                            " annotation"
            );
        }
        PluginMetadata metadata = PluginMetadataImpl.from(info, plugin);
        checkMetadata(metadata, pluginClass);
        pluginManager.registerPlugin(metadata);
    }

    private void checkMetadata(PluginMetadata metadata, Class<?> pluginClass) {
        final String message = "Plugin class " + pluginClass.getName() + " has invalid ";
        if (metadata.id().isEmpty()) {
            throw new RuntimeException(message + "id");
        }
        if (metadata.name().isEmpty()) {
            throw new RuntimeException(message + "name");
        }
        if (metadata.version().isEmpty()) {
            throw new RuntimeException(message + "version");
        }
    }
}
