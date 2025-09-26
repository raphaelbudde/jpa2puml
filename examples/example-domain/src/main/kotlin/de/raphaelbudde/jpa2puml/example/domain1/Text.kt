package de.raphaelbudde.jpa2puml.example.domain1

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Text {

    @Column(nullable = false)
    var name: String = ""

    @Column(nullable = false)
    var description: String = ""
}
