import de.raphaelbudde.jpa2puml.Jpa2puml.version
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

// These test should run AFTER the jpa2puml-maven-plugin!
class MavenPluginSimpleTest {

    @Test
    fun `assert that generated puml look good`() {
        val pumlFile = File("target/maven-plugin-simple-example-$version.puml")

        assertThat(pumlFile).exists()

        val puml = pumlFile.readText()
        assertThat(puml).contains("' Generated with jpa2puml-")

        assertThat(puml).contains("entity de.raphaelbudde.jpa2puml.example.domain2.SimpleEntity")
            .withFailMessage { "Should only contain something from this class" }

        assertThat(puml).doesNotContain("domain1")
            .withFailMessage { "Should NOT contain something from the dependency jar (includeClasspathElements = false)" }
    }
}
