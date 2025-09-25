import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

// These test should run AFTER the jpa2puml-maven-plugin!
class MavenPluginTest {

    @Test
    fun `assert that jpa2puml-maven-plugin has created a puml`() {
        val target = File("target/")
        val pumls = target.listFiles { file -> file.name.endsWith(".puml") }

        assertTrue(pumls.isNotEmpty(), "contains more than one puml")
    }
}
