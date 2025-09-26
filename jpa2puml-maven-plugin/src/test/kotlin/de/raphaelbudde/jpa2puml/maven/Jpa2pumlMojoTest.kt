package de.raphaelbudde.jpa2puml.maven

import de.raphaelbudde.jpa2puml.Jpa2puml
import de.raphaelbudde.jpa2puml.Jpa2pumlSettings
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.verify
import org.apache.maven.plugin.testing.AbstractMojoTestCase
import org.apache.maven.project.MavenProject
import org.assertj.core.api.Assertions.assertThat
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

        project.build.outputDirectory = mockClassDir.absolutePath

        val mojo = lookupConfiguredMojo(project, "jpa2puml")

        setVariableValueToObject(mojo, "includeClasspathElements", true)
        setVariableValueToObject(mojo, "excludedDirectories", listOf("dir.*"))
        setVariableValueToObject(mojo, "excludedFiles", listOf("file.*"))
        setVariableValueToObject(mojo, "excludedClassNames", listOf("name.*"))
        setVariableValueToObject(mojo, "excludedFields", listOf("field.*"))
        setVariableValueToObject(mojo, "drawEnumArrow", true)
        setVariableValueToObject(mojo, "drawInheritanceArrow", true)
        setVariableValueToObject(mojo, "outputFiles", listOf("a.puml", "b.png"))

        mockkObject(Jpa2puml)
        val settingsSlot = slot<Jpa2pumlSettings>()
        every { Jpa2puml.run(capture(settingsSlot)) } returns Unit

        mojo.execute()

        verify { Jpa2puml.run(any()) }

        assertThat(settingsSlot.captured.addPumlHeader).isTrue
        assertThat(settingsSlot.captured.drawEnumArrow).isTrue
        assertThat(settingsSlot.captured.drawInheritanceArrow).isTrue
        assertThat(settingsSlot.captured.outputs).hasSize(2)
        assertThat(settingsSlot.captured.inputs).hasSize(1)
        assertThat(settingsSlot.captured.excludedDirectoryPatterns).hasSize(1)
        assertThat(settingsSlot.captured.excludedFilesPatterns).hasSize(1)
        assertThat(settingsSlot.captured.excludedClassNamePatterns).hasSize(1)
        assertThat(settingsSlot.captured.excludedFieldPatterns).hasSize(1)
    }
}
