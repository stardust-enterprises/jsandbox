package enterprises.stardust.jsandbox.defaults.process.restrict;

import enterprises.stardust.jsandbox.api.processor.ClassTransformingProcessor;
import enterprises.stardust.jsandbox.impl.util.AsmClassDataProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//FIXME: does this handle arrays correctly?
public class RedirectionTransformer implements ClassTransformingProcessor {
    private static final Set<String> REDIRECT_FROM = new HashSet<>(Arrays.asList(
            "java/",
            "javax/",
            "jdk/",
            "sun/",
            "com/sun/"
    ));
    private static final String REDIRECT_TO = "jsandbox/rt/";
    private static final Set<String> EXCEPTIONS = new HashSet<>(Arrays.asList(
            // Object itself, no can do
            "java/lang/Object",

            // String type, because it's embedded in the language itself
            "java/lang/String",

            // Primitive types
            "java/lang/Boolean",
            "java/lang/Byte",
            "java/lang/Character",
            "java/lang/Double",
            "java/lang/Float",
            "java/lang/Integer",
            "java/lang/Long",
            "java/lang/Short"
    ));

    @Override
    public byte @Nullable [] processClass(@NotNull String className, byte @NotNull [] buffer) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(buffer);
        classReader.accept(classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        boolean changesApplied = false;
        String superName = classNode.superName;
        if (shouldRedirect(superName)) {
            classNode.superName = redirect(superName);
            changesApplied = true;
        }

        for (MethodNode methodNode : classNode.methods) {
            String type, newType;
            // Change Method signatures
            type = methodNode.desc;
            newType = updateMethodDesc(type);
            if (!type.equals(newType)) {
                methodNode.desc = newType;
                changesApplied = true;
            }

            // Change instructions
            for (AbstractInsnNode insnNode : methodNode.instructions) {
                if (insnNode instanceof TypeInsnNode) {
                    TypeInsnNode typeInsnNode = (TypeInsnNode) insnNode;
                    type = typeInsnNode.desc;
                    if (shouldRedirect(type)) {
                        typeInsnNode.desc = redirect(type);
                        changesApplied = true;
                    }
                } else if (insnNode instanceof MethodInsnNode) {
                    MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
                    // type + owner
                    type = methodInsnNode.desc;
                    newType = updateMethodDesc(type);
                    if (!type.equals(newType)) {
                        methodInsnNode.desc = newType;
                        changesApplied = true;
                    }
                    type = methodInsnNode.owner;
                    if (shouldRedirect(type)) {
                        methodInsnNode.owner = redirect(type);
                        changesApplied = true;
                    }
                } else if (insnNode instanceof FieldInsnNode) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) insnNode;
                    type = fieldInsnNode.desc;
                    newType = updateDesc(type);
                    if (!type.equals(newType)) {
                        fieldInsnNode.desc = newType;
                        changesApplied = true;
                    }
                    type = fieldInsnNode.owner;
                    if (shouldRedirect(type)) {
                        fieldInsnNode.owner = redirect(type);
                        changesApplied = true;
                    }
                } else if (insnNode instanceof InvokeDynamicInsnNode) {
                    System.out.println("DEBUG REDIRECTION 1");
                    InvokeDynamicInsnNode invokeDynamicInsnNode = (InvokeDynamicInsnNode) insnNode;
                    type = invokeDynamicInsnNode.desc;
                    newType = updateMethodDesc(type);
                    if (!type.equals(newType)) {
                        invokeDynamicInsnNode.desc = newType;
                        changesApplied = true;
                    }
                    boolean localChanges = false;
                    String ownerDesc = invokeDynamicInsnNode.bsm.getOwner();
                    if (shouldRedirect(ownerDesc)) {
                        ownerDesc = redirect(ownerDesc);
                        localChanges = true;
                    }
                    type = invokeDynamicInsnNode.bsm.getDesc();
                    newType = updateMethodDesc(type);
                    if (!type.equals(newType)) {
                        type = newType;
                        localChanges = true;
                    }
                    if (localChanges) {
                        invokeDynamicInsnNode.bsm = new Handle(
                                invokeDynamicInsnNode.bsm.getTag(),
                                ownerDesc,
                                invokeDynamicInsnNode.bsm.getName(),
                                type,
                                invokeDynamicInsnNode.bsm.isInterface()
                        );
                        changesApplied = true;
                    }
                } else if (insnNode instanceof MultiANewArrayInsnNode) {
                    System.out.println("DEBUG REDIRECTION 2");
                    MultiANewArrayInsnNode multiANewArrayInsnNode = (MultiANewArrayInsnNode) insnNode;
                    type = multiANewArrayInsnNode.desc;
                    newType = updateDesc(type);
                    if (!type.equals(newType)) {
                        multiANewArrayInsnNode.desc = newType;
                        changesApplied = true;
                    }
                } else if (insnNode instanceof LdcInsnNode) {
                    LdcInsnNode ldcInsnNode = (LdcInsnNode) insnNode;
                    Object cst = ldcInsnNode.cst;
                    if (cst instanceof Type) {
                        Type cstType = (Type) cst;
                        type = cstType.getDescriptor();
                        newType = updateDesc(type);
                        if (!type.equals(newType)) {
                            ldcInsnNode.cst = Type.getType(newType);
                            changesApplied = true;
                        }
                    }
                }
            }
        }

        if (!changesApplied) {
            return null;
        }

        ClassWriter classWriter = AsmClassDataProvider.INSTANCE.newClassWriter();
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private boolean shouldRedirect(String typeName) {
        if (EXCEPTIONS.contains(typeName)) {
            return false;
        }
        for (String redirectFrom : REDIRECT_FROM) {
            if (typeName.startsWith(redirectFrom)) {
                return true;
            }
        }
        return false;
    }

    private String redirect(String typeName) {
        for (String redirectFrom : REDIRECT_FROM) {
            if (typeName.startsWith(redirectFrom)) {
                return typeName.replaceFirst(redirectFrom, REDIRECT_TO + redirectFrom);
            }
        }
        return typeName;
    }


    private String updateMethodDesc(String desc) {
        boolean localChanges = false;
        Type returnType = Type.getReturnType(desc);
        String internalName = returnType.getInternalName();
        if (shouldRedirect(internalName)) {
            returnType = Type.getObjectType(redirect(returnType.getInternalName()));
            localChanges = true;
        }
        Type[] argumentTypes = Type.getArgumentTypes(desc);
        for (int i = 0; i < argumentTypes.length; i++) {
            Type argumentType = argumentTypes[i];
            internalName = argumentType.getInternalName();
            if (shouldRedirect(internalName)) {
                argumentTypes[i] = Type.getObjectType(redirect(argumentType.getInternalName()));
                localChanges = true;
            }
        }
        if (localChanges) {
            desc = Type.getMethodDescriptor(returnType, argumentTypes);
        }
        return desc;
    }

    private String updateDesc(String desc) {
        String internalName = Type.getType(desc).getInternalName();
        if (shouldRedirect(internalName)) {
            return "L" + redirect(internalName) + ";";
        }
        return desc;
    }
}
