package enterprises.stardust.jsandbox.impl.processor;

import enterprises.stardust.jsandbox.api.processor.JarEntryProcessor;
import enterprises.stardust.jsandbox.api.processor.JarProcessor;
import enterprises.stardust.jsandbox.api.processor.Preprocessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

public class PreprocessorImpl implements Preprocessor {
    private final List<JarProcessor> jarProcessorList = new ArrayList<>();
    private final List<JarEntryProcessor> processorList = new ArrayList<>();

    public Set<Path> runProcessors(Set<Path> classpath) {
        return classpath.stream().map(it -> {
            try {
                return this.processJar(it);
            } catch (IOException e) {
                throw new RuntimeException(
                        "Error while processing jarfile: " + it.toString(),
                        e
                );
            }
        }).collect(Collectors.toSet());
    }

    @Override
    public Path processJar(Path inputJar) throws IOException {
        if (jarProcessorList.isEmpty() && processorList.isEmpty()) {
            return inputJar;
        }

        String originalFilename = inputJar.getFileName().toString();
        int idx;
        if ((idx = originalFilename.lastIndexOf('.')) != -1) {
            originalFilename = originalFilename.substring(0, idx);
        }
        Path transformStore = Files.createTempDirectory("jsandbox-transform");

        //TODO: Cache system
        Path currentTarget = inputJar.toAbsolutePath();
        JarProcessorContextImpl context = new JarProcessorContextImpl(
                currentTarget,
                transformStore,
                originalFilename
        );
        for (JarProcessor jarProcessor : jarProcessorList) {
            if (jarProcessor.shouldProcess(currentTarget)) {
                Path potentialTarget = jarProcessor.preprocess(context);
                if (potentialTarget != null && !potentialTarget.equals(currentTarget)) {
                    Files.deleteIfExists(currentTarget);
                    currentTarget = potentialTarget;
                    context.inputJar(currentTarget);
                }
            }
        }

        JarFile jarFile = new JarFile(currentTarget.toFile());

        Enumeration<JarEntry> preCheck = jarFile.entries();
        boolean needsProcessing = false;
        outer:
        while (preCheck.hasMoreElements()) {
            JarEntry entry = preCheck.nextElement();
            for (JarEntryProcessor processor : processorList) {
                if (processor.shouldProcess(entry.getName())) {
                    needsProcessing = true;
                    break outer;
                }
            }
        }

        Path currentOutput = inputJar;
        if (needsProcessing) {
            Enumeration<JarEntry> entries = jarFile.entries();
            boolean modified = false;
            JarOutputStream jarOutputStream = new JarOutputStream(Files.newOutputStream(currentOutput));
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                JarEntry newEntry = new JarEntry(entry.getName());
                jarOutputStream.putNextEntry(newEntry);
                if (!entry.isDirectory()) {
                    for (JarEntryProcessor processor : processorList) {
                        if (processor.shouldProcess(entry.getName())) {
                            InputStream inputStream = jarFile.getInputStream(entry);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int read;
                            while ((read = inputStream.read(buffer)) != -1) {
                                baos.write(buffer, 0, read);
                            }
                            buffer = baos.toByteArray();

                            byte[] processed = processor.process(entry.getName(), buffer);
                            if (processed != null) {
                                jarOutputStream.write(processed);
                            } else {
                                jarOutputStream.write(buffer);
                            }
                        }
                    }
                }
                jarOutputStream.closeEntry();
            }
            jarOutputStream.close();
        }
        jarFile.close();

        context.inputJar(currentOutput);
        for (JarProcessor jarProcessor : jarProcessorList) {
            if (jarProcessor.shouldProcess(currentOutput)) {
                Path potentialOutput = jarProcessor.postprocess(context);
                if (potentialOutput != null && !potentialOutput.equals(currentOutput)) {
                    Files.deleteIfExists(currentOutput);
                    currentOutput = potentialOutput;
                    context.inputJar(currentOutput);
                }
            }
        }

        return currentOutput;
    }

    @Override
    public void registerJarProcessor(JarProcessor... processors) {
        Collections.addAll(jarProcessorList, processors);
    }

    @Override
    public void registerEntryProcessor(JarEntryProcessor... processors) {
        Collections.addAll(processorList, processors);
    }
}
