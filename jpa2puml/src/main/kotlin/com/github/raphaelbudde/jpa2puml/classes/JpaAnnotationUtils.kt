package com.github.raphaelbudde.jpa2puml.classes

import org.apache.bcel.classfile.AnnotationEntry

private val classAnnotations =
    setOf(
        "Ljakarta/persistence/Entity;",
        "Ljakarta/persistence/Embeddable;",
        "Ljakarta/persistence/MappedSuperclass;",
        "Ljavax/persistence/Entity;",
        "Ljavax/persistence/Embeddable;",
        "Ljavax/persistence/MappedSuperclass;",
        "Lorg/springframework/data/relational/core/mapping/Table;",
    )

private val columnAnnotations =
    setOf(
        "Ljakarta/persistence/Column;",
        "Ljavax/persistence/Column;",
        "Lorg/springframework/data/relational/core/mapping/Column;",
    )

private val idAnnotations =
    setOf(
        "Ljakarta/persistence/Id;",
        "Ljavax/persistence/Id;",
        "Lorg/springframework/data/annotation/Id;",
    )

private val oneToOneAnnotations =
    setOf(
        "Ljakarta/persistence/OneToOne;",
        "Ljavax/persistence/OneToOne;",
    )

private val oneToManyAnnotations =
    setOf(
        "Ljakarta/persistence/OneToMany;",
        "Ljavax/persistence/OneToMany;",
        "Lorg/springframework/data/relational/core/mapping/MappedCollection;",
    )

private val manyToOneAnnotations =
    setOf(
        "Ljakarta/persistence/ManyToOne;",
        "Ljavax/persistence/ManyToOne;",
    )

private val manyToManyAnnotations =
    setOf(
        "Ljakarta/persistence/ManyToMany;",
        "Ljavax/persistence/ManyToMany;",
    )

private val joinColumnAnnotations =
    setOf(
        "Ljakarta/persistence/JoinColumn;",
        "Ljavax/persistence/JoinColumn;",
    )

private val enumeratedAnnotations =
    setOf(
        "Ljakarta/persistence/Enumerated;",
        "Ljavax/persistence/Enumerated;",
    )

private val embeddedAnnotations =
    setOf(
        "Ljakarta/persistence/Embedded;",
        "Ljavax/persistence/Embedded;",
        "Lorg/springframework/data/relational/core/mapping/Embedded;",
    )

fun AnnotationEntry.isClass(): Boolean = classAnnotations.contains(this.annotationType)

fun AnnotationEntry.isColumn(): Boolean = columnAnnotations.contains(this.annotationType)

fun AnnotationEntry.isId(): Boolean = idAnnotations.contains(this.annotationType)

fun AnnotationEntry.isOneToOne(): Boolean = oneToOneAnnotations.contains(this.annotationType)

fun AnnotationEntry.isOneToMany(): Boolean = oneToManyAnnotations.contains(this.annotationType)

fun AnnotationEntry.isManyToOne(): Boolean = manyToOneAnnotations.contains(this.annotationType)

fun AnnotationEntry.isManyToMany(): Boolean = manyToManyAnnotations.contains(this.annotationType)

fun AnnotationEntry.isJoinColumn(): Boolean = joinColumnAnnotations.contains(this.annotationType)

fun AnnotationEntry.isEnumerated(): Boolean = enumeratedAnnotations.contains(this.annotationType)

fun AnnotationEntry.isEmbedded(): Boolean = embeddedAnnotations.contains(this.annotationType)

fun AnnotationEntry?.isNullable(): Boolean = this
    ?.let { it.toString().indexOf("nullable=false") == -1 }
    ?: true
