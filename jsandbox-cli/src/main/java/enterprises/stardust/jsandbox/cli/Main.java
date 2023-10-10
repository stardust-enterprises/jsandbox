package enterprises.stardust.jsandbox.cli;

import enterprises.stardust.jsandbox.impl.JSandboxImpl;
import enterprises.stardust.jsandbox.api.JSandbox;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] programArgs) {
        //TODO: permission options
        CliArguments args = new CliArguments();
        CommandLine.ParseResult result;
        try {
            result = new CommandLine(args)
                    .setUnmatchedArgumentsAllowed(true)
                    .parseArgs(programArgs);
        } catch (Exception e) {
            System.err.println("CLI Error: " + e.getMessage());
            CommandLine.usage(args, System.out);
            System.exit(1);
            return;
        }
        if (!result.errors().isEmpty()) {
            for (Exception error : result.errors()) {
                System.err.println("CLI Error: " + error.getMessage());
                error.printStackTrace(System.err);
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
            System.err.println("No JARs specified, aborting.");
            System.exit(1);
            return;
        }

        List<String> noOpt = result.unmatched();
        if (noOpt.isEmpty()) {
            System.err.println("No main class specified, aborting.");
            System.exit(2);
            return;
        }
        String mainClass = noOpt.get(0);
        String[] arguments = noOpt.subList(1, noOpt.size()).toArray(new String[0]);

        JSandbox sandbox = JSandbox.builder()
                .withClasspath(args.getJars().toArray(new File[0]))
                .withProcessor(
                        args.getProcessors() == null
                                ? new File[0]
                                : args.getProcessors().toArray(new File[0])
                )
                .fork(false)
                .build();
        try {
            System.out.println("Launching class " + mainClass + ", args=\"" + String.join(" ", arguments) + "\"");
            sandbox.launch(mainClass, arguments);
        } catch (ClassNotFoundException e) {
            System.err.println("Class " + mainClass + " not found.");
            System.exit(3);
        } catch (Throwable e) {
            System.err.println("Error launching class " + mainClass + ": " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(4);
        }
    }
}
