package de.raphaelbudde.jpa2puml.domain

import de.raphaelbudde.jpa2puml.classes.withoutPackage

/**
 * note right of A::field
 */
data class PumlField(
    val name: String,
    val type: String? = null,
    val notes: String? = null,
    val nullable: Boolean = true,
    val stereotype: String? = null, // e.g. <<PK>>
    val enum: Boolean = false,
) {
    fun renderField(): String {
        val stereotypeStr =
            if (stereotype != null) {
                " <<${stereotype.withoutPackage()}>>"
            } else {
                ""
            }

        val fieldStr =
            if (type != null) {
                "$name: ${formatType(type, nullable)}"
            } else {
                name
            }

        return fieldStr + stereotypeStr
    }

    fun renderFieldNotes(pumlClass: PumlClass): String? = if (!notes.isNullOrBlank()) {
        "note right of ${pumlClass.name}::${this.name}\n" +
            "  ${notes}\n" +
            "end note\n"
    } else {
        null
    }

    private fun formatType(name: String, nullable: Boolean): String = if (nullable) {
        name.withoutPackage() + "?"
    } else {
        name.withoutPackage()
    }
}
