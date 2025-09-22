package de.raphaelbudde.jpa2puml.classes

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UtilsKtTest {
    @Test
    fun getTypeParametersFromSignatureTest() {
        assertThat(
            getTypeParametersFromSignature("Ljava/util/List<+Lde/raphaelbudde/jpa2puml/domain1/LineElement;>;"),
        ).isEqualTo(listOf("de.raphaelbudde.jpa2puml.domain1.LineElement"))

        assertThat(
            getTypeParametersFromSignature("Ljava/util/ArrayList<+Lde/raphaelbudde/jpa2puml/domain1/LineElement;>;"),
        ).isEqualTo(listOf("de.raphaelbudde.jpa2puml.domain1.LineElement"))
    }
}
