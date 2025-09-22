package de.raphaelbudde.jpa2puml.domain

import de.raphaelbudde.jpa2puml.classes.withoutPackage

data class PumlClass(
    val classType: PumlClassType = PumlClassType.`class`,
    val name: String,
    val stereotype: String? = null, // <<$sterotype>>
    val notes: String? = null,
    val relations: List<PumlRelation>,
    val fields: List<PumlField>,
) {
    fun renderClass(): String {
        val stereotypeStr =
            if (stereotype != null && classType != PumlClassType.enum) {
                "<<${stereotype.withoutPackage()}>> "
            } else {
                ""
            }

        return "$classType $name $stereotypeStr{\n" +
            fields
                .joinToString("\n") {
                    "  " + it.renderField()
                } +
            "\n}\n"
    }

    fun renderRelationsAndNotes(): String = listOfNotNull(
        renderNotes(),
        relations.joinToString("") { it.renderGlobal() },
        fields.mapNotNull { it.renderFieldNotes(this) }.joinToString(""),
    ).joinToString("")

    private fun renderNotes(): String? = if (!notes.isNullOrBlank()) {
        "note right of ${this.name}\n" +
            "  ${notes}\n" +
            "end note\n"
    } else {
        null
    }
}
