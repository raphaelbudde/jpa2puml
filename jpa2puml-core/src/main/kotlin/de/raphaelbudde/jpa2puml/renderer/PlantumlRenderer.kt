package de.raphaelbudde.jpa2puml.renderer

import java.io.OutputStream

interface PlantumlRenderer {
    fun renderPlantuml(puml: String, format: PlantumlOutputFormat, outputStream: OutputStream)
}
