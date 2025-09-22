package com.github.raphaelbudde.jpa2puml.domain1

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Text {

    @Column(nullable = false)
    var name: String = ""

    @Column(nullable = false)
    var description: String = ""
}
