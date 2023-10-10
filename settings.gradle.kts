rootProject.name = "jsandbox"

arrayOf("api", "cli", "runtime").forEach { module ->
    include(":${rootProject.name}-$module")
}