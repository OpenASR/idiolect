package org.openasr.idear.utils

private val toCamelCaseRegex = Regex("\\s([a-z])")
private val toUpperCamelCaseRegex = Regex("\\s?\\b([a-z])")
private val fromCamelCaseRegex = Regex("([A-Z0-9]+)([a-z]*)")

fun String.capitalizeWord(): CharSequence =
    if (first().isUpperCase()) this
    else (StringBuilder(SingleChar(first().uppercaseChar())).append(substring(1)))

fun String.toCamelCase() =
    replace(toCamelCaseRegex) { SingleChar(it.groups[1]!!.value[0].uppercaseChar()) }

fun String.toUpperCamelCase() =
    replace(toUpperCamelCaseRegex) { SingleChar(it.groups[1]!!.value[0].uppercaseChar()) }

fun String.expandCamelCase() =
    StringBuilder(substring(1))
        .insert(0, this[0].lowercaseChar())
        .replace(fromCamelCaseRegex) { m ->
            if (m.groups[1]?.value.isNullOrEmpty())
                m.groups[2]!!.value
            else StringBuilder(SingleChar(' '))
                    .append(SingleChar(m.groups[1]!!.value[0].lowercaseChar()))
                    .append(m.groups[2]!!.value)
        }

fun String.splitCamelCase(): Sequence<String> =
    fromCamelCaseRegex.findAll(capitalizeWord())
        .map {
            val word = StringBuilder(it.groups[2]!!.value)
            if (!it.groups[1]?.value.isNullOrEmpty()) {
                word.insert(0, it.groups[1]!!.value.lowercase())
            }
            word.toString()
        }

class SingleChar(private val char: Char) : CharSequence {
    override val length = 1
    override fun get(index: Int) = char
    override fun subSequence(startIndex: Int, endIndex: Int) = this
}
