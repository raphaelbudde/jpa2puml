package de.raphaelbudde.jpa2puml.maven

import de.raphaelbudde.jpa2puml.Jpa2Puml.Companion.version
import de.raphaelbudde.jpa2puml.classes.JavaClassFinder
import de.raphaelbudde.jpa2puml.classes.PumlClassBuilder
import de.raphaelbudde.jpa2puml.process.PlantumlOutputType
import de.raphaelbudde.jpa2puml.process.PlantumlProcess.transformPumlTo
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import org.apache.maven.project.MavenProject
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.collections.forEach
import kotlin.collections.ifEmpty

/**
 * A Maven plugin which creates puml from classes.
 * This Mojo analyzes the compiled classes directory and generates puml.
 */
@Mojo(
    name = "jpa2puml",
    defaultPhase = LifecyclePhase.PROCESS_CLASSES,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
class Jpa2pumlMojo : AbstractMojo() {

    @Parameter(defaultValue = $$"${project}", readonly = true, required = true)
    private lateinit var project: MavenProject

    @Parameter(defaultValue = $$"${project.build.outputDirectory}", required = true, readonly = true)
    private lateinit var classesDirectory: File

    @Parameter(defaultValue = $$"${project.build.directory}", required = true, readonly = true)
    private lateinit var directory: File

    @Parameter(name = "excludedDirectories", required = false)
    private lateinit var excludedDirectories: List<String>

    @Parameter(name = "excludedFiles", required = false)
    private lateinit var excludedFiles: List<String>

    @Parameter(name = "excludedClassNames", required = false)
    private lateinit var excludedClassNames: List<String>

    @Parameter(name = "excludedFields", required = false)
    private lateinit var excludedFields: List<String>

    @Parameter(name = "outputFiles", required = false, defaultValue = $$"${project.artifactId}-${project.version}.puml")
    private var outputFiles: List<String>? = null

    override fun execute() {
        if (!(classesDirectory?.exists() ?: false)) {
            log.warn("Output directory does not exist: $classesDirectory")
            return
        }

        val excludedDirectoryPatterns: List<Regex> = excludedDirectories.map { Regex(it) }
        val excludedFilesPattern: List<Regex> = excludedFiles.map { Regex(it) }
        val excludedClassNamePatterns: List<Regex> = excludedClassNames.map { Regex(it) }
        val excludedFieldPatterns: List<Regex> = excludedFields.map { Regex(it) }

        val inheritanceArrow = true
        val enumArrow = true

        val classes =
            JavaClassFinder(
                excludedDirectoryPatterns,
                excludedFilesPattern,
            ).findClassFiles(classesDirectory)

        val pumlClassDiagram =
            PumlClassBuilder(
                excludedClassNamePatterns = excludedClassNamePatterns,
                excludedFieldPatterns = listOf<Regex>()
                    .plus(Regex("\\$.*")) // ignore $VALUES and $ENTRIES from kotlin enums
                    .plus(excludedFieldPatterns),
                drawInheritanceArrow = inheritanceArrow,
                drawEnumArrow = enumArrow,
            ).buildClassDiagram(classes)

        val puml = pumlClassDiagram.render(
            "' Generated with jpa2puml-$version\n",
        )

        (outputFiles ?: listOf())
            .forEach { file ->
                val format = PlantumlOutputType.fromFilename(file)

                if (format != null) {
                    File(file).parentFile?.mkdirs()

                    val outputStream = File(file).outputStream()

                    if (format == PlantumlOutputType.puml) {
                        outputStream.write(puml.toByteArray())
                        outputStream.flush()
                        outputStream.close()
                    } else {
                        transformPumlTo(puml, format, outputStream)
                    }
                } else {
                    log.warn("Unsupported file type: $file")
                }
            }

        log.info("jpa2puml complete.")
    }
}
