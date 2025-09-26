package de.raphaelbudde.jpa2puml.classes

import de.raphaelbudde.jpa2puml.Jpa2puml.version
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class JavaClassFinderTest {
    @Test
    fun fromJar() {
        val classes =
            JavaClassFinder()
                .findClassFiles(File("../examples/example-domain/target/example-domain-$version.jar"))

        assertThat(classes).hasSize(8)
    }

    @Test
    fun fromDirectory() {
        val classes =
            JavaClassFinder()
                .findClassFiles(File("../examples/example-domain/target/classes/de/raphaelbudde/jpa2puml/example/domain1"))

        assertThat(classes).hasSize(8)

        val classNames = classes.map { it.className }
        assertThat(classNames).contains("de.raphaelbudde.jpa2puml.example.domain1.AbstractEntity")
        assertThat(classNames).contains("de.raphaelbudde.jpa2puml.example.domain1.GridOperator")
        assertThat(classNames).contains("de.raphaelbudde.jpa2puml.example.domain1.LineElement")
        assertThat(classNames).contains("de.raphaelbudde.jpa2puml.example.domain1.Transformer")
        assertThat(classNames).contains("de.raphaelbudde.jpa2puml.example.domain1.LineElementType")
        assertThat(classNames).contains("de.raphaelbudde.jpa2puml.example.domain1.Address")
        assertThat(classNames).contains("de.raphaelbudde.jpa2puml.example.domain1.UnusedEnum")
        assertThat(classNames).contains("de.raphaelbudde.jpa2puml.example.domain1.Text")
    }
}
