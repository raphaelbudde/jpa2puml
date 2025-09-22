package com.github.raphaelbudde.jpa2puml.domain1

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.*

@Entity
class LineElement(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true)
    var relatedLineElement: LineElement? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    var type: LineElementType,

    @Enumerated(EnumType.STRING)
    @Column(name = "typeB")
    var typeB: LineElementType,

    id: UUID? = null,
) : AbstractEntity(id)

enum class LineElementType {
    TYPE1,
    TYPE2,
}
