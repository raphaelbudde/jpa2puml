import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

// These test should run AFTER the jpa2puml-maven-plugin!
class MavenPluginFullTest {

    @Test
    fun `assert that jpa2puml-maven-plugin has created files`() {
        val target = File("target/")
        val pumls = target.listFiles { file -> file.name.endsWith(".puml") }
        assertTrue(pumls.isNotEmpty(), "contains more than one puml")

        assertTrue(File("target/aaa.puml").exists(), "has aaa.puml")
        assertTrue(File("target/aaa.svg").exists(), "has aaa.svg")
        assertTrue(File("target/aaa.png").exists(), "has aaa.png")
        assertTrue(File("target/bbb/bbb.puml").exists(), "has bbb/bbb.puml")
    }

    @Test
    fun `assert that generated puml look good`() {
        val pumlFile = File("target/aaa.puml")
        val puml = pumlFile.readText()

        assertThat(puml).contains("' Generated with jpa2puml-")

        assertThat(puml).contains("entity de.raphaelbudde.jpa2puml.example.domain3.SimpleEntity")
            .withFailMessage { "Should contain something for this class" }

        assertThat(puml).contains("abstract de.raphaelbudde.jpa2puml.example.domain1.AbstractEntity")
            .withFailMessage { "Should contain something from the dependency jar (includeClasspathElements = true)" }
    }

    // TODO: test more options: excluded*, draw*, etc
}
