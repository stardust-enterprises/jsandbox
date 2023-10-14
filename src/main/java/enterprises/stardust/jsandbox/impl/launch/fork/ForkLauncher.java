package enterprises.stardust.jsandbox.impl.launch.fork;

import enterprises.stardust.jsandbox.api.launch.LaunchContext;
import enterprises.stardust.jsandbox.api.launch.LaunchResult;
import enterprises.stardust.jsandbox.impl.launch.HookableLauncher;
import enterprises.stardust.jsandbox.impl.util.JVMUtils;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class ForkLauncher extends HookableLauncher {
    @Override
    public LaunchResult launchImpl(LaunchContext context) throws Throwable {
        @Nullable String classpathString = context.classpath().stream()
                .map(Path::toAbsolutePath)
                .map(Path::toString)
                .reduce((a, b) -> a + ":" + b)
                .orElse(null);
        if (classpathString == null) {
            throw new RuntimeException("Failed to build classpath");
        }

        Path javaBinaryPath = JVMUtils.getJavaBinaryPath();
        String javaCommand = "java";
        if (javaBinaryPath != null) {
            javaCommand = javaBinaryPath.toString();
        }

        Process process = new ProcessBuilder()
                .command(javaCommand, "-cp", classpathString, context.mainClass())
                .inheritIO()
                .start();
        return new Result(process);
    }

    private static @Data class Result implements LaunchResult {
        private final Process process;

        @Override
        public boolean isFinished() {
            return !process.isAlive();
        }

        @Override
        public int getReturnValue() {
            return process.exitValue();
        }
    }
}
