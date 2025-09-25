package de.raphaelbudde.jpa2puml.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class Jpa2pumlPluginExtension(
    val message: String = "Hallo"
)

class Jpa2pumlGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        var extension: Jpa2pumlPluginExtension? = project.extensions
            .create("jpa2puml", Jpa2pumlPluginExtension::class.java)

        project.task("jpa2puml")
            .doLast { task -> println("Hello Gradle!") }
    }
}
