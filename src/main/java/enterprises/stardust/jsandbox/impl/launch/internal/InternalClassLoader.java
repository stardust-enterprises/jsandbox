package enterprises.stardust.jsandbox.impl.launch.internal;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InternalClassLoader extends URLClassLoader {
    private final Set<String> restrictedPackages = new HashSet<>();

    public InternalClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        Collections.addAll(
                restrictedPackages,

                // Restrict jsandbox so that it can't detect being in a sandbox
                "enterprises.stardust.jsandbox.",
                "jsandbox.rt.internal."
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
        // FIXME: Test code to "undo" RedirectionTransformer
        //        Note: This doesn't seem to work as the following class bypasses this
        //        java.base/java.lang.invoke.MethodHandleNatives#resolve
        if (name.startsWith("jsandbox.rt.java.")) {
            System.out.println(name + " => " + name.substring("jsandbox.rt.".length()));
            return super.loadClass(name.substring("jsandbox.rt.".length()), resolve);
        }
        return super.loadClass(name, resolve);
    }
}
