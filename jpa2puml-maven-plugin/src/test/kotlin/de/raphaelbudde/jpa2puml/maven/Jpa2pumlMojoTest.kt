package de.raphaelbudde.jpa2puml.maven

import org.apache.maven.plugin.testing.AbstractMojoTestCase
import org.apache.maven.project.MavenProject
import java.io.File

class Jpa2pumlMojoTest : AbstractMojoTestCase() {

    override fun setUp() {
        super.setUp()
    }

    override fun tearDown() {
        super.tearDown()
    }

    fun testRunMojo() {
        val pom = getTestFile(getBasedir(), "src/test/resources/test-pom.xml")
        assertNotNull(pom)
        assertTrue(pom.exists())

        val mockClassDir = File("./test-classes")
        val project = MavenProject()

        val mojo = lookupConfiguredMojo(project, "jpa2puml")
        setVariableValueToObject(mojo, "classesDirectory", mockClassDir)

        mojo.execute()
    }
}
