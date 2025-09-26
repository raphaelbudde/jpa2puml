package de.raphaelbudde.jpa2puml.example.domain1

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Version
import java.time.ZonedDateTime
import java.util.*

@MappedSuperclass
abstract class AbstractEntity(id: UUID? = null, created: ZonedDateTime? = null, version: Long? = null) {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    var id: UUID = id ?: UUID.randomUUID()

    @Embedded
    var text: Text? = null

    @Version
    @Column(name = "version")
    var version: Long? = version

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: ZonedDateTime? = created
}
