package de.raphaelbudde.jpa2puml.renderer

import net.sourceforge.plantuml.FileFormat

@Suppress("EnumEntryName")
enum class PlantumlOutputFormat {
    png,
    svg,
    pdf,
    txt,
    puml,
    ;

    companion object {
        fun fromFilename(filename: String): PlantumlOutputFormat? = PlantumlOutputFormat
            .entries
            .firstOrNull { filename.endsWith(it.name, true) }
    }

    val plantumlFileFormat: FileFormat?
        get() = when (this) {
            png -> FileFormat.PNG
            svg -> FileFormat.SVG
            pdf -> FileFormat.PDF
            txt -> FileFormat.UTXT
            else -> null
        }
}
