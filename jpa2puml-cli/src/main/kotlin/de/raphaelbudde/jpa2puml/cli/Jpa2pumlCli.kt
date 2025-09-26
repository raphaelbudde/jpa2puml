package de.raphaelbudde.jpa2puml.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.eagerOption
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import de.raphaelbudde.jpa2puml.Jpa2puml
import de.raphaelbudde.jpa2puml.Jpa2puml.version
import de.raphaelbudde.jpa2puml.Jpa2pumlOutput
import de.raphaelbudde.jpa2puml.Jpa2pumlSettings
import de.raphaelbudde.jpa2puml.renderer.PlantumlOutputFormat

class Jpa2PumlCli : CliktCommand() {

    private val outputs by option("-o", "--out")
        .help(
            "Output file; - for stdout; e.g. domain.puml write to domain.puml; e.g. domain.png tries to invoke plantuml and render png. Supported formats: ${PlantumlOutputFormat.entries.joinToString()}",
        )
        .convert {
            if (it == "-") {
                Jpa2pumlOutput(null, defaultPlantumlOutputFormat)
            } else {
                Jpa2pumlOutput(
                    filename = it,
                    format = PlantumlOutputFormat.fromFilename(it)
                        ?: throw UsageError(
                            "Unsupported output format. Supported formats:  ${PlantumlOutputFormat.entries.joinToString()}",
                        ),
                )
            }
        }
        .multiple()

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

    private val fileOrDirectories by argument()
        .file(mustBeReadable = true)
        .help(".jar file or a directory with .class or .jar files")
        .multiple()

    private val defaultPlantumlOutputFormat: PlantumlOutputFormat
        get() = if (outputTxt) PlantumlOutputFormat.txt else PlantumlOutputFormat.puml

    init {
        eagerOption("-V", "--version", help = "Show the version and exit") {
            throw PrintMessage("jpa2puml-$version")
        }
    }

    override fun run() {
        val settings = Jpa2pumlSettings(
            outputs = outputs.ifEmpty {
                listOf(Jpa2pumlOutput(null, defaultPlantumlOutputFormat))
            },
            inputs = fileOrDirectories,

            verbose = verbose,

            drawEnumArrow = enumArrow,
            drawInheritanceArrow = inheritanceArrow,

            addPumlHeader = true,

            excludedFilesPatterns = excludedFilesPatterns,
            excludedDirectoryPatterns = excludedDirectoryPatterns,
            excludedFieldPatterns = excludedFieldPatterns,
            excludedClassNamePatterns = excludedClassNamePatterns,
        )

        Jpa2puml.run(settings)
    }
}

fun main(args: Array<String>) = Jpa2PumlCli().main(args)
