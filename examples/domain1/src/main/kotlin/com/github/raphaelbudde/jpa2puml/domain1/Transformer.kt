package com.github.raphaelbudde.jpa2puml.domain1

import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import java.util.*

@Entity
class Transformer(

    @ManyToOne
    @JoinColumn(nullable = false)
    var gridOperator: GridOperator = GridOperator(),

    @OneToMany
    var lineElements: List<LineElement> = listOf(),
    id: UUID? = null,
) : AbstractEntity(id)
