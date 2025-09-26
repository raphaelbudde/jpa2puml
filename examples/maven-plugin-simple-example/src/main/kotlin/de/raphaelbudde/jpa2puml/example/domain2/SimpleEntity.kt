package de.raphaelbudde.jpa2puml.example.domain2

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class SimpleEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue
    var id: Long? = null

    @Column(nullable = false)
    var name: String = ""
}
