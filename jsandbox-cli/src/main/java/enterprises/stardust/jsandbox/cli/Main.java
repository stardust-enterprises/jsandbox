package enterprises.stardust.jsandbox.cli;

import enterprises.stardust.jsandbox.api.launch.LaunchType;
import enterprises.stardust.jsandbox.impl.JSandboxImpl;
import enterprises.stardust.jsandbox.api.JSandbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] programArgs) {
        Logger logger = LoggerFactory.getLogger("JSandbox/CLI");

        //TODO: permission options
        CliArguments args = new CliArguments();
        CommandLine.ParseResult result;
        try {
            result = new CommandLine(args)
                    .setUnmatchedArgumentsAllowed(true)
                    .parseArgs(programArgs);
        } catch (Exception e) {
            logger.error("An error occured parsing arguments.");
            CommandLine.usage(args, System.out);
            System.exit(1);
            return;
        }

        if (!result.errors().isEmpty()) {
            for (Exception error : result.errors()) {
                logger.error("An error occured parsing arguments.", error);
            }
            System.exit(1);
            return;
        }

        if (result.isUsageHelpRequested()) {
            CommandLine.usage(args, System.out);
            return;
        }

        if (result.isVersionHelpRequested()) {
            String cliVersion = Main.class.getPackage().getImplementationVersion();
            String apiImplVer = JSandbox.class.getPackage().getImplementationVersion();
            String apiSpecVer = JSandbox.class.getPackage().getSpecificationVersion();
            String implVer = JSandboxImpl.class.getPackage().getImplementationVersion();
            System.out.println("CLI version: " + cliVersion);
            System.out.println("JSandbox API version: " + apiImplVer + " (" + apiSpecVer + ")");
            System.out.println("JSandbox version: " + implVer);
            return;
        }

        if (args.getJars().isEmpty()) {
            logger.error("No JARs specified, aborting.");
            System.exit(1);
            return;
        }

        List<String> noOpt = result.unmatched();
        if (noOpt.isEmpty()) {
            logger.error("No main class specified, aborting.");
            System.exit(2);
            return;
        }
        String mainClass = noOpt.get(0);
        String[] arguments = noOpt.subList(1, noOpt.size()).toArray(new String[0]);

        logger.info("Preparing sandbox...");
        JSandbox sandbox = JSandbox.builder()
                .withClasspath(args.getJars().toArray(new File[0]))
                .withPlugin(
                        args.getPlugins() == null
                                ? new File[0]
                                : args.getPlugins().toArray(new File[0])
                )
                .launchType(LaunchType.DIRECT)
                .build();
        try {
            logger.info("Launching {} (args: {})", mainClass, String.join(" ", arguments));
            sandbox.launch(mainClass, arguments);
        } catch (ClassNotFoundException e) {
            logger.error("Class {} not found.", mainClass);
            System.exit(3);
        } catch (Throwable e) {
            logger.error("Error launching class {}", mainClass, e);
            System.exit(4);
        }
    }
}
