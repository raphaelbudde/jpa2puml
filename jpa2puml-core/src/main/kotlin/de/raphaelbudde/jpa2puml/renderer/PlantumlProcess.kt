package de.raphaelbudde.jpa2puml.renderer

import java.io.OutputStream

object CommandPlantumlRenderer : PlantumlRenderer {
    private const val COMMAND = "plantuml"

    override fun renderPlantuml(puml: String, format: PlantumlOutputFormat, outputStream: OutputStream) {
        val processBuilder = ProcessBuilder(COMMAND, "-pipe", "-t$format")

        // increase the max width of generated images, the default (4092) is too small for big diagrams.
        processBuilder.environment()["PLANTUML_LIMIT_SIZE"] = "32768"

        val process = processBuilder.start()
        process.outputStream.write(puml.toByteArray())
        process.outputStream.close()

        process.inputStream.copyTo(outputStream)
    }

    fun isInstalled(): Boolean {
        try {
            val version = ProcessBuilder(COMMAND, "-version")
                .start()
                .inputStream
                .bufferedReader()
                .readText()

            return version.contains("PlantUML version") &&
                version.contains("Installation seems OK") &&
                version.contains("File generation OK")
        } catch (_: Exception) {
        }
        return false
    }
}
