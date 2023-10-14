package enterprises.stardust.jsandbox.impl.launch;

import enterprises.stardust.jsandbox.api.launch.LaunchContext;
import enterprises.stardust.jsandbox.api.launch.LaunchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Accessors(fluent = true, chain = false)
public @Data class LaunchContextImpl implements LaunchContext {
    private String mainClass;
    private Set<Path> classpath;
    private List<String> args;
    private LaunchType launchType;
}
