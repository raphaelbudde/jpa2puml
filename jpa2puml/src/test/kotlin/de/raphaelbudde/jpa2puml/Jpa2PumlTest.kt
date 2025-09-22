package com.github.raphaelbudde.jpa2puml

import com.github.raphaelbudde.jpa2puml.classes.JavaClassFinder
import com.github.raphaelbudde.jpa2puml.classes.PumlClassBuilder
import com.github.raphaelbudde.jpa2puml.domain.PumlClass
import com.github.raphaelbudde.jpa2puml.domain.PumlClassDiagram
import org.apache.bcel.classfile.JavaClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class Jpa2PumlTest {
    private lateinit var classes: Set<JavaClass>
    private lateinit var classDiagram: PumlClassDiagram
    private lateinit var classDiagramFull: PumlClassDiagram

    @BeforeEach
    fun init() {
        classes =
            JavaClassFinder().findClassFiles(File("../examples/domain1/target/classes/com/github/raphaelbudde/jpa2puml/domain1"))
        classDiagram = PumlClassBuilder(
            excludedFieldPatterns = listOf(Regex("\\$.*")), // ignore $VALUES and $ENTRIES from kotlin enums
        ).buildClassDiagram(classes)

        classDiagramFull = PumlClassBuilder(
            excludedFieldPatterns = listOf(Regex("\\$.*")), // ignore $VALUES and $ENTRIES from kotlin enums
            drawInheritanceArrow = true,
            drawEnumArrow = true,
        ).buildClassDiagram(classes)
    }

    private fun getPumlClass(className: String): PumlClass? = classDiagram.classes.find { it.name.endsWith(className) }

    private fun getPumlClassFull(className: String): PumlClass? = classDiagramFull.classes.find { it.name.endsWith(className) }

    @Test
    fun renderAbstractEntity() {
        val abstractEntity = getPumlClass("AbstractEntity")

        assertThat(abstractEntity!!.renderClass()).isEqualTo(
            """abstract com.github.raphaelbudde.jpa2puml.domain1.AbstractEntity {
  id: UUID <<PK>>
  version: Long?
  createdAt: ZonedDateTime
}
""",
        )
        assertThat(abstractEntity.renderRelationsAndNotes()).isEqualTo(
            """com.github.raphaelbudde.jpa2puml.domain1.AbstractEntity "1" --> "1" com.github.raphaelbudde.jpa2puml.domain1.Text : text
""",
        )
    }

    @Test
    fun renderGridOperator() {
        val gridOperator = getPumlClass("GridOperator")
        assertThat(gridOperator!!.renderClass()).isEqualTo(
            """entity com.github.raphaelbudde.jpa2puml.domain1.GridOperator <<AbstractEntity>> {
  mastrNr: String?
}
""",
        )

        assertThat(gridOperator.renderRelationsAndNotes()).isEqualTo(
            """com.github.raphaelbudde.jpa2puml.domain1.GridOperator "0..1" --> "0..1" com.github.raphaelbudde.jpa2puml.domain1.Address : address
com.github.raphaelbudde.jpa2puml.domain1.GridOperator "1..*" --> "0..*" com.github.raphaelbudde.jpa2puml.domain1.Transformer : transformers
""",
        )
    }

    @Test
    fun renderAddress() {
        val abstractEntity = getPumlClass("Address")
        assertThat(abstractEntity!!.renderClass()).isEqualTo(
            """entity com.github.raphaelbudde.jpa2puml.domain1.Address {
  id: Long <<PK>>
  name: String
  street: String
  city: String
}
""",
        )
    }

    @Test
    fun renderTransformer() {
        val transformer = getPumlClass("Transformer")
        assertThat(transformer!!.renderClass()).isEqualTo(
            """entity com.github.raphaelbudde.jpa2puml.domain1.Transformer <<AbstractEntity>> {

}
""",
        )

        assertThat(transformer.renderRelationsAndNotes()).isEqualTo(
            """com.github.raphaelbudde.jpa2puml.domain1.Transformer "0..*" --> "1" com.github.raphaelbudde.jpa2puml.domain1.GridOperator : gridOperator
com.github.raphaelbudde.jpa2puml.domain1.Transformer "1" --> "0..*" com.github.raphaelbudde.jpa2puml.domain1.LineElement : lineElements
""",
        )
    }

    @Test
    fun renderLineElement() {
        val lineElement = getPumlClass("LineElement")!!
        val lineElementFull = getPumlClassFull("LineElement")!!

        assertThat(lineElement.renderClass()).isEqualTo(
            """entity com.github.raphaelbudde.jpa2puml.domain1.LineElement <<AbstractEntity>> {
  type: LineElementType?
  typeB: LineElementType?
}
""",
        )

        assertThat(lineElement.renderRelationsAndNotes()).isEqualTo(
            """com.github.raphaelbudde.jpa2puml.domain1.LineElement "0..*" --> "0..1" com.github.raphaelbudde.jpa2puml.domain1.LineElement : relatedLineElement
""",
        )

        assertThat(lineElementFull.renderRelationsAndNotes()).isEqualTo(
            """com.github.raphaelbudde.jpa2puml.domain1.LineElement "0..*" --> "0..1" com.github.raphaelbudde.jpa2puml.domain1.LineElement : relatedLineElement
com.github.raphaelbudde.jpa2puml.domain1.LineElement -up-|> com.github.raphaelbudde.jpa2puml.domain1.AbstractEntity
com.github.raphaelbudde.jpa2puml.domain1.LineElement .. com.github.raphaelbudde.jpa2puml.domain1.LineElementType
""",
        )
    }

    @Test
    fun renderEnum() {
        val abstractEntity = getPumlClass("LineElementType")
        assertThat(abstractEntity!!.renderClass()).isEqualTo(
            """enum com.github.raphaelbudde.jpa2puml.domain1.LineElementType {
  TYPE1
  TYPE2
}
""",
        )
    }

    @Test
    fun toPumlRender() {
        val expectedPuml = Jpa2PumlTest::class.java.getResource("/domain1.puml")!!.readText()
        val generatedPuml = classDiagram.render()

        assertThat(generatedPuml).isEqualTo(expectedPuml)
    }

    @Test
    fun toPumlRenderFull() {
        val expectedPuml = Jpa2PumlTest::class.java.getResource("/domain1-full.puml")!!.readText()
        val generatedPuml = classDiagramFull.render()

        assertThat(generatedPuml).isEqualTo(expectedPuml)
    }
}
