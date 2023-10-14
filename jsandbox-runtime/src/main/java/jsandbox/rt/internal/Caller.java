package jsandbox.rt.internal;

import lombok.SneakyThrows;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Caller {
    private static final Provider PROVIDER;

    private Caller() {
    }

    public static Class<?> get() {
        return get(2);
    }

    public static Class<?> get(int depth) {
        return PROVIDER.get(depth + 1);
    }

    static {
        Provider tmpProvider;
        try {
            SecurityManager.class.getName();
            tmpProvider = new SecurityManagerProvider();
        } catch (Throwable ignored2) {
            try {
                StackWalker.class.getName();
                tmpProvider = new StackWalkerProvider();
            } catch (Throwable ignored3) {
                tmpProvider = new StackTraceProvider();
            }
        }
        PROVIDER = tmpProvider;
    }

    private interface Provider {
        Class<?> get(int depth);
    }

    @SuppressWarnings({"removal", "RedundantSuppression"})
    private static class SecurityManagerProvider extends SecurityManager implements Provider {
        @Override
        public Class<?> get(int depth) {
            return getClassContext()[depth + 1];
        }
    }

    private static class StackWalkerProvider implements Provider {
        @Override
        public Class<?> get(int depth) {
            return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                    .walk(s -> s.skip(depth + 1).findFirst().get().getDeclaringClass());
        }
    }

    private static class StackTraceProvider implements Provider {
        @SneakyThrows
        @Override
        public Class<?> get(int depth) {
            return Class.forName(
                    Thread.currentThread().getStackTrace()[depth + 2].getClassName()
            );
        }
    }
}
