package de.raphaelbudde.jpa2puml.maven

import de.raphaelbudde.jpa2puml.Jpa2puml
import de.raphaelbudde.jpa2puml.Jpa2pumlOutput
import de.raphaelbudde.jpa2puml.Jpa2pumlSettings
import de.raphaelbudde.jpa2puml.renderer.PlantumlOutputFormat
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import org.apache.maven.project.MavenProject
import java.io.File

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

    /**
     * scan in all dependencies for JPA code
     */
    @Parameter(name = "includeClasspathElements", required = false)
    private var includeClasspathElements: Boolean = false

    /**
     * regex of directory names to exclude
     */
    @Parameter(name = "excludedDirectories", required = false)
    private var excludedDirectories: List<String>? = null

    /**
     * regex of file names to exclude
     */
    @Parameter(name = "excludedFiles", required = false)
    private var excludedFiles: List<String>? = null

    /**
     * regex of class names to exclude
     */
    @Parameter(name = "excludedClassNames", required = false)
    private var excludedClassNames: List<String>? = null

    /**
     * regex of field names to exclude
     */
    @Parameter(name = "excludedFields", required = false)
    private var excludedFields: List<String>? = null

    /**
     * draw an arrow to enums
     */
    @Parameter(name = "drawEnumArrow", required = false)
    private var drawEnumArrow: Boolean = false

    /**
     * draw an arrow for inheritance
     */
    @Parameter(name = "drawInheritanceArrow", required = false)
    private var drawInheritanceArrow: Boolean = false

    /**
     * list of output files. Output type will be guessed by file extension (e.g. puml, png, svg, pdf)
     */
    @Parameter(
        name = "outputFiles",
        required = false,
        defaultValue = $$"${project.build.directory}/${project.artifactId}-${project.version}.puml"
    )
    private var outputFiles: List<String>? = null

    override fun execute() {
        log.info("jpa2puml-maven-plugin started")

        val settings = Jpa2pumlSettings(
            outputs = outputFiles
                ?.mapNotNull { filename ->
                    val format = PlantumlOutputFormat.fromFilename(filename)
                    if (format != null) {
                        Jpa2pumlOutput(filename, format)
                    } else {
                        log.warn("Unsupported output format $filename. Ignoring.")
                        null
                    }
                } ?: listOf(),

            inputs = if (includeClasspathElements) {
                project.compileClasspathElements.map { File(it) }
            } else {
                listOf(File(project.build.outputDirectory))
            },

            verbose = false,

            drawEnumArrow = drawEnumArrow,
            drawInheritanceArrow = drawInheritanceArrow,

            addPumlHeader = true,

            excludedDirectoryPatterns = excludedDirectories?.map { Regex(it) } ?: emptyList(),
            excludedFilesPatterns = excludedFiles?.map { Regex(it) } ?: emptyList(),
            excludedClassNamePatterns = excludedClassNames?.map { Regex(it) } ?: emptyList(),
            excludedFieldPatterns = excludedFields?.map { Regex(it) } ?: emptyList(),
        )

        log.debug("Run jpa2puml-maven-plugin with settings: $settings")
        Jpa2puml.run(settings)

        log.info("jpa2puml-maven-plugin complete")
    }
}
