package enterprises.stardust.jsandbox.impl.launch.internal;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public class InternalClassLoader extends URLClassLoader {
    private final Set<String> restrictedPackages = new HashSet<>();

    public InternalClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.restrictedPackages.add(
                // Restrict jsandbox so that it can't detect being in a sandbox
                "enterprises.stardust.jsandbox."
        );

        // TODO: Restrict other packages
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        for (String restrictedPackage : this.restrictedPackages) {
            if (name.startsWith(restrictedPackage)) {
                throw new ClassNotFoundException(name);
            }
        }
        return super.loadClass(name, resolve);
    }
}
