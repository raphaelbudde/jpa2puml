package com.github.raphaelbudde.jpa2puml.domain

data class PumlRelation(
    val source: String,
    val target: String,
    val relationType: String = "--",
    val label: String? = null,
    val fromLabel: String? = null, // x |fooId: UUID| --> Foo
    val toLabel: String? = null,
) {
    fun renderGlobal(): String = listOfNotNull(
        source,
        fromLabel?.let { "\"$fromLabel\"" },
        relationType,
        toLabel?.let { "\"$toLabel\"" },
        target,
        label?.let { ": $label" },
    ).joinToString(" ") + "\n"
}
