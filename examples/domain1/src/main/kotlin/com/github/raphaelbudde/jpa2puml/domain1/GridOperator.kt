package com.github.raphaelbudde.jpa2puml.domain1

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToOne

@Entity
class GridOperator(
    @Column
    var mastrNr: String? = null,

    @OneToOne
    @JoinColumn(name = "address_id", nullable = true)
    var address: Address? = null,

    @ManyToMany
    var transformers: List<Transformer>? = null,
) : AbstractEntity()
