package com.github.raphaelbudde.jpa2puml.classes

import com.github.raphaelbudde.jpa2puml.domain.PumlClass
import com.github.raphaelbudde.jpa2puml.domain.PumlClassDiagram
import com.github.raphaelbudde.jpa2puml.domain.PumlClassType
import com.github.raphaelbudde.jpa2puml.domain.PumlField
import com.github.raphaelbudde.jpa2puml.domain.PumlRelation
import org.apache.bcel.classfile.Field
import org.apache.bcel.classfile.JavaClass
import org.apache.bcel.classfile.Signature

class PumlClassBuilder(
    private val excludedClassNamePatterns: List<Regex> = listOf(),
    private val excludedFieldPatterns: List<Regex> = listOf(),
    val drawInheritanceArrow: Boolean = false,
    var drawEnumArrow: Boolean = false,
) {
    fun buildClassDiagram(classes: Set<JavaClass>): PumlClassDiagram {
        val pumlClasses =
            classes
                .asSequence()
                .filter { clazz -> !clazz.excluded() }
                .filter { clazz -> clazz.annotationEntries.any { it.isClass() } }
                .sortedBy { clazz -> clazz.className }
                .map { clazz -> toPumlClass(clazz) }
                .toList()

        val usedEnumClasses = pumlClasses
            .flatMap { clazz ->
                clazz.fields
                    .filter { it.enum && it.type != null }
                    .map { it.type!! }
            }
            .toSet()

        val enums = classes.asSequence()
            .filter { clazz -> !clazz.excluded() }
            .filter { clazz -> clazz.isEnum }
            .filter { clazz -> usedEnumClasses.contains(clazz.className) }
            .sortedBy { clazz -> clazz.className }
            .map { clazz -> toPumlEnum(clazz) }.toList()

        return PumlClassDiagram(
            pumlClasses.plus(enums),
        )
    }

    private fun toPumlClass(clazz: JavaClass): PumlClass {
        require(clazz.annotationEntries.any { it.isClass() })

        val columnFields = findColumnFields(clazz)
        val manyToOneFields = findManyToOneRelations(clazz)
        val oneToOneFields = findOneToOneRelations(clazz)
        val oneToManyFields = findOneToManyRelations(clazz)
        val manyToManyFields = findManyToManyRelations(clazz)

        val type = if (clazz.isAbstract) {
            PumlClassType.abstract
        } else {
            PumlClassType.entity
        }

        val superclass = if (clazz.superclassName != "java.lang.Object" && clazz.superclassName != "java.lang.Enum") {
            clazz.superclassName
        } else {
            null
        }

        val superclassRelations = if (drawInheritanceArrow && superclass != null) {
            listOf(
                PumlRelation(
                    clazz.className,
                    superclass,
                    "-up-|>",
                ),
            )
        } else {
            listOf()
        }

        val enumRelations = if (drawEnumArrow) {
            columnFields
                .filter { it.enum && it.type != null }
                .map {
                    PumlRelation(
                        clazz.className,
                        it.type!!,
                        "..",
                    )
                }
        } else {
            listOf()
        }

        return PumlClass(
            type,
            clazz.className,
            superclass,
            null,
            listOf<PumlRelation>()
                .plus(oneToOneFields)
                .plus(manyToOneFields)
                .plus(oneToManyFields)
                .plus(manyToManyFields)
                .plus(superclassRelations)
                .plus(enumRelations)
                .distinct(),
            columnFields,
        )
    }

    private fun toPumlEnum(clazz: JavaClass): PumlClass {
        require(clazz.isEnum)

        val fields = clazz.fields
            .filter { field -> !field.excluded() }
            .map { field -> PumlField(field.name, null) }

        return PumlClass(
            PumlClassType.enum,
            clazz.className,
            clazz.superclassName,
            null,
            listOf(),
            fields,
        )
    }

    private fun findOneToOneRelations(entity: JavaClass): List<PumlRelation> {
        return entity.fields
            .filter { field -> field.annotationEntries.any { it.isOneToOne() || it.isEmbedded() } }
            .filter { field -> !field.excluded() }
            .map { field ->
                val isEmbedded = field.annotationEntries.any { it.isEmbedded() }
                val joinColumnAnnotation = field.annotationEntries.firstOrNull { it.isJoinColumn() }
                val nullable = if (isEmbedded) false else joinColumnAnnotation.isNullable()

                PumlRelation(
                    entity.className,
                    field.type.className,
                    "-->",
                    field.name,
                    fromLabel = if (nullable) "0..1" else "1",
                    toLabel = if (nullable) "0..1" else "1",
                )
            }
    }

    private fun findOneToManyRelations(entity: JavaClass): List<PumlRelation> {
        return entity.fields
            .filter { field -> field.annotationEntries.any { it.isOneToMany() } }
            .filter { field -> !field.excluded() }
            .map { field ->
                val signature = field.attributes.firstOrNull { it is Signature } as Signature?
                val typeParam = signature?.signature?.let { getTypeParametersFromSignature(it) }?.firstOrNull()
                    ?: throw IllegalArgumentException("Invalid @OneToMany type signature \"${signature?.signature}\".")

                PumlRelation(
                    entity.className,
                    typeParam,
                    "-->",
                    field.name,
                    fromLabel = "1",
                    toLabel = "0..*",
                )
            }
    }

    private fun findManyToManyRelations(entity: JavaClass): List<PumlRelation> {
        return entity.fields
            .filter { field -> field.annotationEntries.any { it.isManyToMany() } }
            .filter { field -> !field.excluded() }
            .map { field ->
                val signature = field.attributes.firstOrNull { it is Signature } as Signature?
                val typeParam = signature?.signature?.let { getTypeParametersFromSignature(it) }?.firstOrNull()
                    ?: throw IllegalArgumentException("Invalid @ManyToMany type signature \"${signature?.signature}\".")

                PumlRelation(
                    entity.className,
                    typeParam,
                    "-->",
                    field.name,
                    fromLabel = "1..*",
                    toLabel = "0..*",
                )
            }
    }

    private fun findManyToOneRelations(entity: JavaClass): List<PumlRelation> {
        return entity.fields
            .filter { field -> field.annotationEntries.any { it.isManyToOne() } }
            .filter { field -> !field.excluded() }
            .map { field ->
                val joinColumnAnnotation = field.annotationEntries.firstOrNull { it.isJoinColumn() }

                PumlRelation(
                    entity.className,
                    field.type.className,
                    "-->",
                    field.name,
                    fromLabel = "0..*",
                    toLabel = if (joinColumnAnnotation.isNullable()) "0..1" else "1",
                )
            }
    }

    private fun findColumnFields(entity: JavaClass): List<PumlField> {
        return entity.fields
            .filter { field -> field.annotationEntries.any { it.isColumn() } }
            .filter { field -> !field.excluded() }
            .map { field ->
                val columnAnnotation = field.annotationEntries.firstOrNull { it.isColumn() }
                val idAnnotation = field.annotationEntries.firstOrNull { it.isId() }
                val enumAnnotation = field.annotationEntries.firstOrNull { it.isEnumerated() }

                PumlField(
                    name = field.name,
                    type = field.type.className,
                    nullable = columnAnnotation.isNullable(),
                    stereotype = idAnnotation?.let { "PK" },
                    enum = enumAnnotation != null,
                )
            }
    }

    private fun Field.excluded(): Boolean = excludedFieldPatterns.any {
        it.matches(this.name)
    }

    private fun JavaClass.excluded(): Boolean = excludedClassNamePatterns.any {
        it.matches(this.className)
    }
}
