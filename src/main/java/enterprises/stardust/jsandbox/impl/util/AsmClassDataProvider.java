package enterprises.stardust.jsandbox.impl.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.reflect.Modifier;
import java.util.*;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ASM5;

public enum AsmClassDataProvider {
    INSTANCE(AsmClassDataProvider.class.getClassLoader());

    private final Map<String, ClData> clDataHashMap;
    private final ClassLoader classLoader;

    AsmClassDataProvider(ClassLoader classLoader) {
        this.clDataHashMap = new HashMap<>();
        this.clDataHashMap.put("java/lang/Object", ObjectCLData.INSTANCE);
        this.classLoader = classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader;
    }

    public static class ClData implements ClassData {
        final String name;
        String superClass;
        int access;

        private ClData(String name) {
            this.name = name;
            this.access = ACC_PUBLIC;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isInterface() {
            return Modifier.isInterface(this.access);
        }

        @Override
        public boolean isFinal() {
            return Modifier.isFinal(this.access);
        }

        @Override
        public boolean isPublic() {
            return Modifier.isPublic(this.access);
        }

        @Override
        public boolean isCustom() {
            return false;
        }

        @Override
        public ClData getSuperclass() {
            return null;
        }

        @Override
        public boolean isAssignableFrom(ClassData clData) {
            while (clData != null) {
                if (clData == this) return true;
                clData = clData.getSuperclass();
            }
            return false;
        }

    }

    private static final class ObjectCLData extends ClData {
        public static final ObjectCLData INSTANCE = new ObjectCLData();

        private ObjectCLData() {
            super("java/lang/Object");
        }

        @Override
        public boolean isAssignableFrom(ClassData clData) {
            return clData == this;
        }
    }

    class ClData2 extends ClData {
        List<String> interfaces;
        List<String> guessedSup;
        boolean custom = false;

        private ClData2(String name) {
            super(name);
        }

        public ClData getSuperclass() {
            if (this.superClass == null) return null;
            return getClassData(this.superClass);
        }

        @Override
        public boolean isAssignableFrom(ClassData clData) {
            if (clData == null) return false;
            if (clData instanceof ClData2) {
                if (((ClData2) clData).interfaces != null) {
                    for (String cl : ((ClData2) clData).interfaces) {
                        if (this.isAssignableFrom(getClassData(cl))) {
                            return true;
                        }
                    }
                }
                if (((ClData2) clData).guessedSup != null) {
                    for (String cl : ((ClData2) clData).guessedSup) {
                        if (this.isAssignableFrom(getClassData(cl))) {
                            return true;
                        }
                    }
                }
            }
            do {
                if (clData == this) return true;
                clData = clData.getSuperclass();
            } while (clData != null);
            return false;
        }

        @Override
        public boolean isCustom() {
            return custom;
        }
    }

    private ClData getClassData(String clName) {
        clName = clName.replace('.', '/');
        ClData clData = clDataHashMap.get(clName);
        if (clData != null) return clData;
        clData = new ClData2(clName);
        try {
            ClassReader classReader = new ClassReader(Objects.requireNonNull(this.classLoader.getResourceAsStream(clName + ".class")));
            final ClData2 tClData = (ClData2) clData;
            classReader.accept(new ClassVisitor(ASM5) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    tClData.access = access;
                    tClData.superClass = superName;
                    if (interfaces != null && interfaces.length != 0) {
                        tClData.interfaces = Arrays.asList(interfaces);
                    }
                }
            }, ClassReader.SKIP_CODE);
        } catch (Exception e) {
            clData.superClass = "java/lang/Object";
        }
        clDataHashMap.put(clName, clData);
        return clData;
    }

    public ClassWriter newClassWriter() {
        return new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS) {
            @Override
            protected String getCommonSuperClass(String type1, String type2) {
                if (type1.equals(type2)) return type1;
                if (type1.equals("java/lang/Object") || type2.equals("java/lang/Object")) return "java/lang/Object";
                try {
                    ClData c, d;
                    try {
                        c = getClassData(type1);
                        d = getClassData(type2);
                    } catch (Exception e) {
                        throw new RuntimeException(e.toString());
                    }
                    if (c.isAssignableFrom(d)) {
                        return type1;
                    }
                    if (d.isAssignableFrom(c)) {
                        return type2;
                    }
                    if (c.isInterface() || d.isInterface()) {
                        return "java/lang/Object";
                    } else {
                        do {
                            c = c.getSuperclass();
                        } while (!c.isAssignableFrom(d));
                        return c.getName().replace('.', '/');
                    }
                } catch (Exception e) {
                    return "java/lang/Object";
                }
            }
        };
    }

    public void addClasses(Map<String, byte[]> classes) {
        for (Map.Entry<String, byte[]> entry : classes.entrySet())
            if (entry.getKey().endsWith(".class")) {
                String name = entry.getKey().substring(0, entry.getKey().length() - 6);
                ClData2 clData = new ClData2(name);
                try {
                    ClassReader classReader = new ClassReader(entry.getValue());
                    classReader.accept(new ClassVisitor(ASM5) {
                        @Override
                        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                            clData.access = access;
                            clData.superClass = superName;
                            clData.custom = true;
                            if (interfaces != null && interfaces.length != 0) {
                                clData.interfaces = Arrays.asList(interfaces);
                            }
                        }
                    }, ClassReader.SKIP_CODE);
                } catch (Exception e) {
                    System.err.println("Invalid Class => " + name);
                    clData.superClass = "java/lang/Object";
                }
                clDataHashMap.put(name, clData);
            }
    }

    public interface ClassData {
        String getName();

        ClassData getSuperclass();

        boolean isAssignableFrom(ClassData clData);

        boolean isInterface();

        boolean isFinal();

        boolean isPublic();

        boolean isCustom();
    }
}