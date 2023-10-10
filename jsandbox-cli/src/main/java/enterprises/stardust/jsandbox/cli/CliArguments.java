package enterprises.stardust.jsandbox.cli;

import picocli.CommandLine;

import java.io.File;
import java.util.List;

@CommandLine.Command(
        name = "jsandbox"
)
public class CliArguments {
    @CommandLine.Option(names = {"--jar", "-j"}, description = "Path to JAR file(s) to add to the runtime classpath", required = true)
    private List<File> jar;

    @CommandLine.Option(names = {"--processor", "-p"}, description = "Path to JAR file(s) to lookup class processor in")
    private List<File> processor;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message")
    private boolean helpRequested;

    @CommandLine.Option(names = {"-v", "--version"}, versionHelp = true, description = "Print version information")
    private boolean versionRequested;

    public List<File> getJars() {
        return jar;
    }

    public List<File> getProcessors() {
        return processor;
    }
}
