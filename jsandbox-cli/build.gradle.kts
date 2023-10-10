import com.github.jengelman.gradle.plugins.shadow.transformers.Transformer
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipOutputStream
import org.codehaus.plexus.util.IOUtil
import java.io.ByteArrayOutputStream
import java.util.jar.JarFile.MANIFEST_NAME
import java.util.jar.Manifest as JManifest

plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

version = "0.1.0"

val include: Configuration by configurations.creating {
    isTransitive = false
    description = "Shaded dependencies"
    val implementation by configurations
    implementation.extendsFrom(this)
}

dependencies {
    include(project(":jsandbox-api"))
    include(project(":"))

    include("info.picocli:picocli:4.7.5")
}

application {
    mainClass.set("enterprises.stardust.jsandbox.cli.Main")
}

tasks {
    jar {
        val mainClass = application.mainClass.get()
        manifest.attributes["Main-Class"] = mainClass

        val pkg = mainClass.substringBeforeLast('.')
            .replace('.', '/')
        manifest.attributes(
            "$pkg/",
            "Implementation-Title" to "jsandbox-cli",
            "Implementation-Version" to project.version.toString(),
            "Implementation-Vendor" to "Stardust Enterprises"
        )
    }

    shadowJar {
        manifest.inheritFrom(jar.get().manifest)

        configurations = listOf(include)
        transformers = listOf(ManifestMergeTransformer())
        duplicatesStrategy = DuplicatesStrategy.WARN
    }

    build.get().dependsOn(shadowJar)
}

class ManifestMergeTransformer : Transformer {
    private var manifest: JManifest? = null

    override fun getName(): String =
        "ManifestMergeTransformer"

    override fun canTransformResource(element: FileTreeElement?): Boolean =
        MANIFEST_NAME.equals(element?.relativePath?.pathString, ignoreCase = true)

    override fun transform(context: TransformerContext) {
        if (manifest == null) {
            manifest = JManifest(context.`is`)
        } else {
            val toMerge = JManifest(context.`is`)
            for ((key, value) in toMerge.entries.entries) {
                manifest!!.entries[key] = value
            }
        }
        @Suppress("DEPRECATION")
        IOUtil.close(context.`is`)
    }

    override fun hasTransformedResource(): Boolean =
        true

    override fun modifyOutputStream(os: ZipOutputStream, preserveFileTimestamps: Boolean) {
        val entry = ZipEntry(MANIFEST_NAME)
        entry.time = TransformerContext.getEntryTimestamp(preserveFileTimestamps, entry.time)
        os.putNextEntry(entry)
        manifest?.let {
            os.write(ByteArrayOutputStream().use { bos ->
                it.write(bos)
                bos.toByteArray()
            })
        }
    }
}