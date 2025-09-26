package de.raphaelbudde.jpa2puml

import de.raphaelbudde.jpa2puml.classes.JavaClassFinder
import de.raphaelbudde.jpa2puml.classes.PumlClassBuilder
import de.raphaelbudde.jpa2puml.renderer.ClassPlantumlRenderer.renderPlantuml
import de.raphaelbudde.jpa2puml.renderer.PlantumlOutputFormat
import org.slf4j.simple.SimpleLogger
import java.io.File
import java.util.Properties

data class Jpa2pumlOutput(val filename: String? = null, val format: PlantumlOutputFormat = PlantumlOutputFormat.puml)

data class Jpa2pumlSettings(
    /**
     * list of output files.
     * filename null will print to STDOUT.
     * file and parent directories will be created.
     */
    val outputs: List<Jpa2pumlOutput>,

    /**
     * list of .jar files or directories (including .class or .jar files)
     */
    val inputs: List<File>,

    /** draws a line to the parent class */
    val drawInheritanceArrow: Boolean = false,

    /** draws a line from class to used enums */
    val drawEnumArrow: Boolean = false,

    /** add "generated with... " header */
    val addPumlHeader: Boolean = true,

    /** Excluded directories with the given Regex */
    val excludedDirectoryPatterns: List<Regex> = emptyList(),

    /** Excluded class files with the given Regex */
    val excludedFilesPatterns: List<Regex> = emptyList(),

    /** Excluded classes with the given Regex */
    val excludedClassNamePatterns: List<Regex> = emptyList(),

    /** Excluded fields with the given Regex */
    val excludedFieldPatterns: List<Regex> = emptyList(),

    /** verbose output */
    val verbose: Boolean = false,
)

object Jpa2puml {

    fun run(settings: Jpa2pumlSettings) {
        if (settings.verbose) {
            System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "Debug")
        }

        val classes =
            JavaClassFinder(
                settings.excludedDirectoryPatterns,
                settings.excludedFilesPatterns,
            ).findClassFiles(settings.inputs)

        val pumlClassDiagram =
            PumlClassBuilder(
                excludedClassNamePatterns = settings.excludedClassNamePatterns,
                excludedFieldPatterns = listOf<Regex>()
                    .plus(Regex("\\$.*")) // ignore $VALUES and $ENTRIES from kotlin enums
                    .plus(settings.excludedFieldPatterns),
                drawInheritanceArrow = settings.drawInheritanceArrow,
                drawEnumArrow = settings.drawEnumArrow,
            ).buildClassDiagram(classes)

        val puml = pumlClassDiagram.render(
            if (settings.addPumlHeader) "' Generated with jpa2puml-$version\n" else null,
        )

        settings.outputs
            .forEach { output ->
                val outputStream =
                    if (output.filename == null) {
                        System.out
                    } else {
                        File(output.filename).parentFile?.mkdirs() // create parent directories
                        File(output.filename).outputStream()
                    }

                renderPlantuml(puml, output.format, outputStream)
            }
    }

    val version: String
        get() {
            val properties = Properties()
            properties.load(this::class.java.classLoader.getResourceAsStream("version.properties"))

            return properties.getProperty("version")
        }
}
