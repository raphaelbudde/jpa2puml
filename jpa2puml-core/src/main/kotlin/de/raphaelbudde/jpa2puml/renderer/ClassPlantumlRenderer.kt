package de.raphaelbudde.jpa2puml.renderer

import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import java.io.OutputStream

object ClassPlantumlRenderer : PlantumlRenderer {
    override fun renderPlantuml(puml: String, format: PlantumlOutputFormat, outputStream: OutputStream) {
        if (format == null || format == PlantumlOutputFormat.puml || format.plantumlFileFormat == null) {
            outputStream.write(puml.toByteArray())
        } else {
            val reader = SourceStringReader(puml)
            val fileFormatOption = FileFormatOption(format.plantumlFileFormat)
            reader.outputImage(outputStream, fileFormatOption)
        }
    }
}
