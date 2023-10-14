package enterprises.stardust.jsandbox.impl.util;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JVMUtils {
    public static @Nullable Path getJavaHome() {
        Path javaHome = findJavaHome();
        if (javaHome == null) {
            return null;
        }
        if (Files.notExists(javaHome)) {
            return null;
        }
        if (javaHome.getFileName().toString().equals("jre")) {
            return javaHome.getParent();
        }
        return javaHome;
    }

    private static @Nullable Path findJavaHome() {
        String javaHome = System.getProperty("java.home");
        if (javaHome != null) {
            Path javaHomePath = Paths.get(javaHome);
            if (Files.exists(javaHomePath)) {
                return javaHomePath;
            }
        }
        String javaHomeAlt = System.getenv("JAVA_HOME");
        if (javaHomeAlt != null) {
            Path javaHomePath = Paths.get(javaHomeAlt);
            if (Files.exists(javaHomePath)) {
                return javaHomePath;
            }
        }
        Path javaCommand = findFromCommand(System.getProperty("java.command"));
        if (javaCommand != null) {
            return javaCommand;
        }
        Path sunJavaCommand = findFromCommand(System.getProperty("sun.java.command"));
        if (sunJavaCommand != null) {
            return sunJavaCommand;
        }
        String sblp = System.getProperty("sun.boot.library.path");
        if (sblp != null) {
            Path sblpPath = Paths.get(sblp);
            if (Files.exists(sblpPath) && sblpPath.getFileName().toString().startsWith("lib")) {
                return sblpPath.getParent();
            }
        }
        return null;
    }

    private static @Nullable Path findFromCommand(String cmd) {
        if (cmd == null) {
            return null;
        }
        String[] split = cmd.split(" ");
        if (split.length == 0) {
            return null;
        }
        String command = split[0];
        if (command.startsWith("\"") && command.endsWith("\"")) {
            command = command.substring(1, command.length() - 1);
        }
        if (command.startsWith("'") && command.endsWith("'")) {
            command = command.substring(1, command.length() - 1);
        }
        int extIndex = split[0].lastIndexOf(".");
        if (extIndex != -1) {
            command = command.substring(0, extIndex);
        }
        if (command.endsWith("java") || command.endsWith("javaw")) {
            Path javaExecutable = Paths.get(split[0]);
            if (javaExecutable.isAbsolute()) {
                return javaExecutable.getParent().getParent();
            }
        }
        return null;
    }

    public static @Nullable Path getJavaBinaryPath() {
        Path jvmPath = getJavaHome();
        if (jvmPath == null) {
            return null;
        }
        Path javaBinaryPath = findJavaBinaryPath(jvmPath);
        if (javaBinaryPath != null) {
            return javaBinaryPath;
        }
        Path jrePath = jvmPath.resolve("jre");
        if (Files.exists(jrePath)) {
            return findJavaBinaryPath(jrePath);
        }
        return null;
    }

    private static @Nullable Path findJavaBinaryPath(Path jvmHome) {
        Path bin = jvmHome.resolve("bin");
        if (Files.notExists(bin)) {
            return null;
        }
        Path javaBin = bin.resolve("java");
        if (Files.exists(javaBin)) {
            return javaBin;
        }
        Path javaBinAlt = bin.resolve("java.exe");
        if (Files.exists(javaBinAlt)) {
            return javaBinAlt;
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getJavaHome());
        System.out.println(getJavaBinaryPath());
    }
}
