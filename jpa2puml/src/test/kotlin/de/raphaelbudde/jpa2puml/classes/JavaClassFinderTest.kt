package com.github.raphaelbudde.jpa2puml.classes

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class JavaClassFinderTest {
    @Test
    fun fromJar() {
        val classes =
            JavaClassFinder()
                .findClassFiles(File("../examples/domain1/target/examples-domain1-1.2.0-SNAPSHOT.jar"))

        assertThat(classes).hasSize(8)
    }

    @Test
    fun fromDirectory() {
        val classes =
            JavaClassFinder()
                .findClassFiles(File("../examples/domain1/target/classes/com/github/raphaelbudde/jpa2puml/domain1"))

        assertThat(classes).hasSize(8)

        val classNames = classes.map { it.className }
        assertThat(classNames).contains("com.github.raphaelbudde.jpa2puml.domain1.AbstractEntity")
        assertThat(classNames).contains("com.github.raphaelbudde.jpa2puml.domain1.GridOperator")
        assertThat(classNames).contains("com.github.raphaelbudde.jpa2puml.domain1.LineElement")
        assertThat(classNames).contains("com.github.raphaelbudde.jpa2puml.domain1.Transformer")
        assertThat(classNames).contains("com.github.raphaelbudde.jpa2puml.domain1.LineElementType")
        assertThat(classNames).contains("com.github.raphaelbudde.jpa2puml.domain1.Address")
        assertThat(classNames).contains("com.github.raphaelbudde.jpa2puml.domain1.UnusedEnum")
        assertThat(classNames).contains("com.github.raphaelbudde.jpa2puml.domain1.Text")
    }
}
