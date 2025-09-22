package com.github.raphaelbudde.jpa2puml

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.eagerOption
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.raphaelbudde.jpa2puml.classes.JavaClassFinder
import com.github.raphaelbudde.jpa2puml.classes.PumlClassBuilder
import com.github.raphaelbudde.jpa2puml.process.PlantumlOutputType
import com.github.raphaelbudde.jpa2puml.process.PlantumlProcess.isInstalled
import com.github.raphaelbudde.jpa2puml.process.PlantumlProcess.transformPumlTo
import org.slf4j.simple.SimpleLogger
import java.io.File
import java.util.*

class Jpa2Puml : CliktCommand() {

    private val outputs by option("-o", "--out")
        .help(
            "Output file; - for stdout; e.g. domain.puml write to domain.puml; e.g. domain.png tries to invoke plantuml and render png. Supported formats: ${PlantumlOutputType.entries.joinToString()}",
        ).multiple()
        .check(
            { "invalid output extension; Supported formats: ${PlantumlOutputType.entries.joinToString()}" },
            { outputs -> outputs.all { filename -> filename == "-" || PlantumlOutputType.fromFilename(filename) != null } },
        )

    private val outputTxt by option("--txt")
        .help("Default renderer txt")
        .flag()

    private val inheritanceArrow by option("-i", "--inheritance-arrow")
        .help("Draw a line to parent class")
        .flag()

    private val enumArrow by option("-e", "--enum-arrow")
        .help("Draw a line from class to used enums")
        .flag()

    private val excludedDirectoryPatterns by option("--excluded-directories")
        .help("Excluded directories with the given Regex")
        .convert { Regex(it) }
        .multiple()

    private val excludedFilesPatterns by option("--excluded-files")
        .help("Excluded class files with the given Regex")
        .convert { Regex(it) }
        .multiple()

    private val excludedClassNamePatterns by option("--excluded-classes")
        .help("Excluded classes with the given Regex")
        .convert { Regex(it) }
        .multiple()

    private val excludedFieldPatterns by option("--excluded-fields")
        .help("Excluded fields with the given Regex")
        .convert { Regex(it) }
        .multiple()

    private val verbose by option("-v", "--verbose").flag().help("Verbose")
    private val fileOrDirectory by argument().file(mustBeReadable = true).help(".jar file or a directory with .class or .jar files")

    private val defaultPlantumlOutputType: PlantumlOutputType
        get() = if (outputTxt) PlantumlOutputType.txt else PlantumlOutputType.puml

    init {
        eagerOption("-V", "--version", help = "Show the version and exit") {
            throw PrintMessage("jpa2puml-$version")
        }
    }

    override fun run() {
        if (verbose) {
            System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "Debug")
        }

        val classes =
            JavaClassFinder(
                excludedDirectoryPatterns,
                excludedFilesPatterns,
            ).findClassFiles(fileOrDirectory)

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

        outputs
            .ifEmpty { listOf("-") }
            .forEach { filename ->
                val format =
                    PlantumlOutputType.fromFilename(filename)
                        ?: defaultPlantumlOutputType

                val outputStream =
                    if (filename == "-") {
                        System.out
                    } else {
                        File(filename).outputStream()
                    }

                if (format == PlantumlOutputType.puml) {
                    outputStream.write(puml.toByteArray())
                } else {
                    try {
                        transformPumlTo(puml, format, outputStream)
                    } catch (e: Exception) {
                        if (!isInstalled()) {
                            throw UsageError(
                                """
                                    Can't generate $format, PlantUML is NOT installed!

                                    Try to install PlantUML via your package manager, e.g.

                                    brew install plantuml

                                    apt install plantuml

                                    pacman -S plantuml

                                """.trimIndent(),
                            )
                        } else {
                            throw e
                        }
                    }
                }
            }
    }

    companion object {
        val version: String
            get() {
                val properties = Properties()
                properties.load(this::class.java.classLoader.getResourceAsStream("version.properties"))

                return properties.getProperty("version")
            }
    }
}

fun main(args: Array<String>) = Jpa2Puml().main(args)
