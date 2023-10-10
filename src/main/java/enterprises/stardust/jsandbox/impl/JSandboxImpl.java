package enterprises.stardust.jsandbox.impl;

import enterprises.stardust.jsandbox.api.JSandbox;
import enterprises.stardust.jsandbox.api.launch.Launcher;
import enterprises.stardust.jsandbox.impl.launch.ForkLauncher;
import enterprises.stardust.jsandbox.impl.launch.InternalLauncher;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

@ToString
@EqualsAndHashCode
public final class JSandboxImpl implements JSandbox {
    private final Launcher launcher;

    private JSandboxImpl(Set<@NonNull URI> classpath, Set<@NonNull URI> processors, boolean fork) {
        this.launcher = fork
                ? new ForkLauncher(classpath)
                : new InternalLauncher(classpath);
    }

    @Override
    public Launcher launcher() {
        return this.launcher;
    }

    public static class Builder implements JSandbox.Builder {
        private final Set<URI> classpath = new HashSet<>();
        private final Set<URI> processors = new HashSet<>();
        private boolean fork = false;

        @Override
        public JSandbox.Builder withClasspath(URI... classpath) {
            this.classpath.addAll(Arrays.asList(classpath));
            return this;
        }

        @Override
        public JSandbox.Builder withClasspath(URL... classpath) {
            List<URI> uris = new ArrayList<>();
            for (URL url : classpath) {
                try {
                    uris.add(url.toURI());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return this.withClasspath(uris.toArray(new URI[0]));
        }

        @Override
        public JSandbox.Builder withClasspath(String... classpath) {
            List<URI> uris = new ArrayList<>();
            for (String url : classpath) {
                try {
                    uris.add(new URI(url));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return this.withClasspath(uris.toArray(new URI[0]));
        }

        @Override
        public JSandbox.Builder withClasspath(File... classpath) {
            List<URI> uris = new ArrayList<>();
            for (File url : classpath) {
                try {
                    uris.add(url.toURI());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return this.withClasspath(uris.toArray(new URI[0]));
        }

        @Override
        public JSandbox.Builder withClasspath(Path... classpath) {
            List<URI> uris = new ArrayList<>();
            for (Path url : classpath) {
                try {
                    uris.add(url.toUri());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return this.withClasspath(uris.toArray(new URI[0]));
        }

        @Override
        public JSandbox.Builder withProcessor(URI... classpath) {
            this.processors.addAll(Arrays.asList(classpath));
            return this;
        }

        @Override
        public JSandbox.Builder withProcessor(URL... classpath) {
            List<URI> uris = new ArrayList<>();
            for (URL url : classpath) {
                try {
                    uris.add(url.toURI());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return this.withProcessor(uris.toArray(new URI[0]));
        }

        @Override
        public JSandbox.Builder withProcessor(String... classpath) {
            List<URI> uris = new ArrayList<>();
            for (String url : classpath) {
                try {
                    uris.add(new URI(url));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return this.withProcessor(uris.toArray(new URI[0]));
        }

        @Override
        public JSandbox.Builder withProcessor(File... classpath) {
            List<URI> uris = new ArrayList<>();
            for (File url : classpath) {
                try {
                    uris.add(url.toURI());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return this.withProcessor(uris.toArray(new URI[0]));
        }

        @Override
        public JSandbox.Builder withProcessor(Path... classpath) {
            List<URI> uris = new ArrayList<>();
            for (Path url : classpath) {
                try {
                    uris.add(url.toUri());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return this.withProcessor(uris.toArray(new URI[0]));
        }

        @Override
        public JSandbox.Builder fork(boolean fork) {
            this.fork = fork;
            return this;
        }

        @Override
        public JSandbox build() {
            return new JSandboxImpl(
                    Collections.unmodifiableSet(this.classpath),
                    Collections.unmodifiableSet(this.processors),
                    this.fork
            );
        }
    }
}
