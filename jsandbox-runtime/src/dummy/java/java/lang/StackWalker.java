package java.lang;

import java.util.function.Function;
import java.util.stream.Stream;

public class StackWalker {
    public static StackWalker getInstance(Option option) {
        throw new LinkageError("nope");
    }

    public static StackWalker getInstance() {
        throw new LinkageError("nope");
    }

    public interface StackFrame {
        Class<?> getDeclaringClass();
    }

    public enum Option {
        RETAIN_CLASS_REFERENCE
    }

    public <T> T walk(Function<? super Stream<StackWalker.StackFrame>, ? extends T> function) {
        throw new LinkageError("nope");
    }

    public Class<?> getCallerClass() {
        throw new LinkageError("nope");
    }
}
