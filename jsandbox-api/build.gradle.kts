version = "1.0.0"

tasks.jar.get().manifest.attributes(
    "enterprises/stardust/jsandbox/api/",
    "Specification-Title" to "JSandbox API",
    "Specification-Version" to "1",
    "Specification-Vendor" to "Stardust Enterprises",
    "Implementation-Title" to "JSandbox API",
    "Implementation-Version" to project.version,
    "Implementation-Vendor" to "Stardust Enterprises"
)