package enterprises.stardust.jsandbox.impl;

import enterprises.stardust.jsandbox.api.JSandbox;
import enterprises.stardust.jsandbox.api.launch.LaunchContext;
import enterprises.stardust.jsandbox.api.launch.LaunchResult;
import enterprises.stardust.jsandbox.api.launch.LaunchType;
import enterprises.stardust.jsandbox.api.launch.Launcher;
import enterprises.stardust.jsandbox.api.plugin.PluginContext;
import enterprises.stardust.jsandbox.api.plugin.PluginManager;
import enterprises.stardust.jsandbox.api.plugin.PluginMetadata;
import enterprises.stardust.jsandbox.api.processor.Preprocessor;
import enterprises.stardust.jsandbox.impl.launch.LaunchContextImpl;
import enterprises.stardust.jsandbox.impl.launch.fork.ForkLauncher;
import enterprises.stardust.jsandbox.impl.launch.internal.InternalLauncher;
import enterprises.stardust.jsandbox.impl.plugin.PluginContextImpl;
import enterprises.stardust.jsandbox.impl.plugin.PluginLoader;
import enterprises.stardust.jsandbox.impl.plugin.PluginManagerImpl;
import enterprises.stardust.jsandbox.impl.processor.PreprocessorImpl;
import lombok.NonNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class JSandboxImpl implements JSandbox {
    private final Set<@NonNull Path> classpath;
    private final PreprocessorImpl preprocessor;
    private final PluginManagerImpl pluginManager;
    private final Launcher launcher;

    public JSandboxImpl(Set<@NonNull Path> classpath, Set<@NonNull Path> plugins, LaunchType launchType) {
        this.launcher = launchType == LaunchType.FORKED
                ? new ForkLauncher()
                : new InternalLauncher(launchType == LaunchType.THREADED);
        this.preprocessor = new PreprocessorImpl();

        // Load Plugins
        PluginLoader pluginLoader = new PluginLoader(
                this.pluginManager = new PluginManagerImpl(),
                plugins
        );
        pluginLoader.loadPlugins();
        for (PluginMetadata loadedPlugin : this.pluginManager.getLoadedPlugins()) {
            PluginContext context = new PluginContextImpl(
                    this,
                    launcher,
                    loadedPlugin
            );
            loadedPlugin.plugin().install(context);
        }

        // Run Preprocessors
        this.classpath = preprocessor.runProcessors(classpath);
    }

    @Override
    public LaunchResult launch(String mainClass, String[] args) throws Throwable {
        LaunchContext context = new LaunchContextImpl(
                mainClass,
                Collections.unmodifiableSet(classpath),
                Arrays.asList(args),
                LaunchType.DIRECT
        );
        return launcher.launch(context);
    }

    @Override
    public PluginManager pluginManager() {
        return pluginManager;
    }

    @Override
    public Preprocessor preprocessor() {
        return preprocessor;
    }

    public static class Builder implements JSandbox.Builder {
        private final Set<Path> classpath = new HashSet<>();
        private final Set<Path> plugins = new HashSet<>();
        private LaunchType launchType;

        @Override
        public JSandbox.Builder withClasspath(String... classpath) {
            this.classpath.addAll(
                    Arrays.stream(classpath)
                            .map(File::new)
                            .map(File::toPath)
                            .collect(Collectors.toList())
            );
            return this;
        }

        @Override
        public JSandbox.Builder withClasspath(File... classpath) {
            this.classpath.addAll(
                    Arrays.stream(classpath)
                            .map(File::toPath)
                            .collect(Collectors.toList())
            );
            return this;
        }

        @Override
        public JSandbox.Builder withClasspath(Path... classpath) {
            Collections.addAll(this.classpath, classpath);
            return this;
        }

        @Override
        public JSandbox.Builder withPlugin(String... plugin) {
            this.plugins.addAll(
                    Arrays.stream(plugin)
                            .map(File::new)
                            .map(File::toPath)
                            .collect(Collectors.toList())
            );
            return this;
        }

        @Override
        public JSandbox.Builder withPlugin(File... plugin) {
            this.plugins.addAll(
                    Arrays.stream(plugin)
                            .map(File::toPath)
                            .collect(Collectors.toList())
            );
            return this;
        }

        @Override
        public JSandbox.Builder withPlugin(Path... plugin) {
            Collections.addAll(this.plugins, plugin);
            return this;
        }

        @Override
        public JSandbox.Builder launchType(LaunchType launchType) {
            this.launchType = launchType;
            return this;
        }

        @Override
        public JSandbox build() {
            return new JSandboxImpl(
                    Collections.unmodifiableSet(this.classpath),
                    Collections.unmodifiableSet(this.plugins),
                    this.launchType
            );
        }
    }
}
