package jsandbox.rt.java.lang;

import enterprises.stardust.jsandbox.rt.annotation.Proxy;
import jsandbox.rt.internal.Caller;

@Proxy(java.lang.Class.class)
@SuppressWarnings("unchecked")
public class Class<T> {
    private final java.lang.Class<T> object;

    private Class(java.lang.Class<T> object) {
        this.object = object;
    }

    public static <N> Class<N> forName(String className) throws ClassNotFoundException {
        java.lang.Class<?> caller = Caller.get();
        java.lang.Class<N> internalClass = (java.lang.Class<N>) java.lang.Class.forName(
                className,
                true,
                caller.getClassLoader()
        );
        return new Class<>(internalClass);
    }

    public static <N> Class<N> forName(String className, ClassLoader classLoader, boolean initialize) throws ClassNotFoundException {
        java.lang.Class<?> caller = Caller.get();
        java.lang.Class<N> internalClass = (java.lang.Class<N>) java.lang.Class.forName(
                className,
                initialize,
                classLoader == null ? caller.getClassLoader() : classLoader
        );
        return new Class<>(internalClass);
    }

    public String getName() {
        return object.getName();
    }

    public String getCanonicalName() {
        return object.getCanonicalName();
    }

    public Class<? super T> getSuperclass() {
        java.lang.Class<? super T> superclass = object.getSuperclass();
        return superclass == null ? null : new Class<>(superclass);
    }

    public Class<?>[] getInterfaces() {
        java.lang.Class<?>[] interfaces = object.getInterfaces();
        Class<?>[] result = new Class<?>[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            result[i] = new Class<>(interfaces[i]);
        }
        return result;
    }

    public Class<?> getComponentType() {
        java.lang.Class<?> componentType = object.getComponentType();
        return componentType == null ? null : new Class<>(componentType);
    }

    public T newInstance() throws InstantiationException, IllegalAccessException {
        return object.newInstance();
    }

    public T cast(Object obj) {
        return object.cast(obj);
    }

    public boolean desiredAssertionStatus() {
        return object.desiredAssertionStatus();
    }

    public boolean isAnnotation() {
        return object.isAnnotation();
    }

    public boolean isSynthetic() {
        return object.isSynthetic();
    }

    public boolean isAnonymousClass() {
        return object.isAnonymousClass();
    }
}
