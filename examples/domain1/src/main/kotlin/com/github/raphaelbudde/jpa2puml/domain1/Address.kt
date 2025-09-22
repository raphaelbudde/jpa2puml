package com.github.raphaelbudde.jpa2puml.domain1

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Address {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue
    var id: Long? = null

    @Column(nullable = false)
    var name: String = ""

    @Column(nullable = false)
    var street: String = ""

    @Column(nullable = false)
    var city: String = ""
}
