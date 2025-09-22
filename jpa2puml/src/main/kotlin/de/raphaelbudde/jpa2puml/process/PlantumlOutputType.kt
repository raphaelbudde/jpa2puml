package de.raphaelbudde.jpa2puml.process

@Suppress("EnumEntryName")
enum class PlantumlOutputType {
    png,
    svg,
    pdf,
    txt,
    puml,
    ;

    companion object {
        fun fromFilename(filename: String): PlantumlOutputType? = PlantumlOutputType
            .entries
            .firstOrNull { filename.endsWith(it.name, true) }
    }
}
