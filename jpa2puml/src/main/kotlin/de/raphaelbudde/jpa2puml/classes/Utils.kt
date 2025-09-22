package de.raphaelbudde.jpa2puml.classes

/**
 * e.g. "com.foo.Bar" -> "Bar"
 */
fun String.withoutPackage(): String = this
    .split(".")
    .last()

/**
 * Signature might be "Ljava/util/List<+Lde/raphaelbudde/jpa2puml/domain1/LineElement;>;"
 * return will be listOf("de.raphaelbudde.jpa2puml.domain1.LineElement")
 */
fun getTypeParametersFromSignature(signature: String): List<String> {
    val matchResult =
        Regex("L(.+)<(.*)>;")
            .matchEntire(signature)

    require(
        matchResult != null &&
            matchResult.groupValues.size == 3,
    ) { "Invalid argument, invalid format. Expecting L...<Lfoo.bar;>" }

    return matchResult.groupValues[2]
        .split(";")
        .map { getSingleTypeParameterFromSignature(it) }
        .filter { it.isNotBlank() }
}

/**
 * +Lde/raphaelbudde/jpa2puml/domain1/LineElement -> de.raphaelbudde.jpa2puml.domain1.LineElement
 */
private fun getSingleTypeParameterFromSignature(signature: String): String = signature
    .trimStart('+')
    .trimStart('L')
    .replace('/', '.')
    .trimEnd(';')
