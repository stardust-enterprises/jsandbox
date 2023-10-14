package enterprises.stardust.jsandbox.defaults;

import enterprises.stardust.jsandbox.api.JSandbox;
import enterprises.stardust.jsandbox.api.launch.Launcher;
import enterprises.stardust.jsandbox.api.plugin.Plugin;
import enterprises.stardust.jsandbox.api.plugin.PluginContext;
import enterprises.stardust.jsandbox.api.plugin.PluginMetadata;
import enterprises.stardust.jsandbox.api.processor.Preprocessor;
import enterprises.stardust.jsandbox.defaults.process.log.JSandboxLoggingJarPostProcessor;
import enterprises.stardust.jsandbox.defaults.process.log.JSandboxLoggingJarProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Plugin.Info(
        id = "jsandbox-defaults",
        name = "JSandbox Defaults",
        version = "1.0.0"
)
public class JSandboxDefaultPlugin implements Plugin {
    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());
    private Logger launcherLogger;

    @Override
    public void install(PluginContext context) {
        JSandbox sandbox = context.sandbox();
        Launcher launcher = context.launcher();
        PluginMetadata metadata = context.metadata();

        LoggerFactory.getLogger("JSandbox").info(
                "Loaded {} plugins.",
                sandbox.pluginManager().getLoadedPlugins().size()
        );

        logger.info(
                "Installing {} v{}",
                metadata.name(),
                metadata.version()
        );

        this.launcherLogger =
                LoggerFactory.getLogger(launcher.getClass().getSimpleName());
        launcher.registerLaunchHook(ctx -> {
            String mainClass = ctx.mainClass();
            launcherLogger.info("Launching {}", mainClass);
        });

        Preprocessor preprocessor = sandbox.preprocessor();
        preprocessor.registerJarProcessor(
                new JSandboxLoggingJarProcessor(),
                new JSandboxLoggingJarPostProcessor()
        );
    }
}
