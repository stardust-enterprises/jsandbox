import org.gradle.internal.impldep.com.google.api.services.storage.Storage.Projects.HmacKeys.Create

version = "0.1.0+java-8"

sourceSets {
    val dummy by creating {
        java.srcDir("src/dummy/java")
        resources.srcDir("src/dummy/resources")
    }
    getByName("main") {
        compileClasspath += dummy.output
    }
}

tasks {
    val cci = create("createClassIndex", CreateClassIndex::class) {
        classDirs.setFrom(sourceSets.main.get().output)
        filters.set(listOf(
            "jsandbox/rt/internal/",
            "jsandbox/rt/launcher/",
            "enterprises/stardust/jsandbox/",
        ))
        indexFile.set(buildDir.resolve("tmp/classIndex"))
    }
    jar {
        from(cci.indexFile) {
            into("META-INF/jsandbox")
        }
    }
}

open class CreateClassIndex : DefaultTask() {
    @get:InputFiles
    val classDirs: ConfigurableFileCollection =
        project.objects.fileCollection()

    @get:Input
    val filters: ListProperty<String> =
        project.objects.listProperty<String>()

    @get:OutputFile
    val indexFile: RegularFileProperty =
        project.objects.fileProperty()

    @TaskAction
    fun createClassIndex() {
        indexFile.get().asFile.parentFile.mkdirs()
        val classes = mutableListOf<String>()
        classDirs.forEach { file ->
            classes.addAll(file.walkTopDown().filter { it.isFile }.map { it.relativeTo(file).path })
        }
        classes.removeIf { e -> !e.endsWith(".class") || filters.get().any { e.startsWith(it) } }
        indexFile.get().asFile.writeText(classes.joinToString("\n"))
    }
}
