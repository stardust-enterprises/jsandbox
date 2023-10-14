plugins {
    id("io.freefair.lombok") version "8.4" apply false
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.freefair.lombok")

    repositories {
        mavenCentral()
    }

    dependencies {
        val implementation by configurations
        implementation("org.jetbrains:annotations:24.0.1")
        implementation("org.slf4j:slf4j-api:2.0.9")
    }

    configure<JavaPluginExtension> {
        withJavadocJar()
        withSourcesJar()

        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }
}

group = "enterprises.stardust"
version = "0.0.1"

subprojects {
    group = rootProject.group.toString() + "." + rootProject.name
    version = version ?: rootProject.version.toString()
}

dependencies {
    val api by configurations
    api(project(":jsandbox-api"))
}

tasks {
    getByName<Jar>("jar") {
        val pkg = "${project.group}.${project.name}.impl".replace('.', '/')
        manifest.attributes(
            "$pkg/",
            "Specification-Title" to "JSandbox API",
            "Specification-Version" to "1",
            "Specification-Vendor" to "Stardust Enterprises",
            "Implementation-Title" to "JSandbox",
            "Implementation-Version" to project.version.toString(),
            "Implementation-Vendor" to "Stardust Enterprises"
        )
    }
}