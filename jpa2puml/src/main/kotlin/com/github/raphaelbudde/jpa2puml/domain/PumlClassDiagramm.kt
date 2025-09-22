package com.github.raphaelbudde.jpa2puml.domain

import java.io.StringWriter

class PumlClassDiagram(val classes: List<PumlClass>) {
    fun render(header: String? = null): String {
        val writer = StringWriter()
        writer.write("@startuml\n")

        if (header != null) {
            writer.write(header)
            writer.write("\n")
        }

        // writer.write("!theme sketchy-outline\n")
        writer.write("!pragma useIntermediatePackages false\n")
        writer.write("hide methods\n")
        writer.write("hide empty fields\n")
        writer.write("skinparam groupInheritance 2\n")

        writer.write("\n")

        writer.write(renderClasses())

        writer.write("\n")
        writer.write("@enduml\n")

        return writer.toString()
    }

    private fun renderClasses(): String {
        val local = classes.joinToString("\n") { it.renderClass() }
        val global = classes.joinToString("") { it.renderRelationsAndNotes() }

        return local + "\n" + global
    }
}
