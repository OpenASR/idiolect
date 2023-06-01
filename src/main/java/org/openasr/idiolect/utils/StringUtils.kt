package org.openasr.idiolect.utils

private val toCamelCaseRegex = Regex("\\s([a-z])")
private val toUpperCamelCaseRegex = Regex("\\s?\\b([a-z])")
private val fromCamelCaseRegex = Regex("([A-Z0-9]+)([a-z]*)")

fun String.capitalizeWord(): CharSequence =
    if (first().isUpperCase()) this
    else (StringBuilder(SingleChar(first().uppercaseChar())).append(substring(1)))

/**
 * eg: "to camel case" -> "toCamelCase"
 */
fun String.toCamelCase() =
    replace(toCamelCaseRegex) { SingleChar(it.groups[1]!!.value[0].uppercaseChar()) }

/**
 * eg: "to upper camel case" -> "ToUpperCamelCase"
 */
fun String.toUpperCamelCase() =
    replace(toUpperCamelCaseRegex) { SingleChar(it.groups[1]!!.value[0].uppercaseChar()) }

/**
 * For a (lower or upper) camelCase String returns a string of lower case words separated by space.
 * eg: "expandCamelCase" -> "split camel case"
 */
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

/**
 * For a (lower or upper) camelCase String returns a sequence of lower case Strings for each term.
 * eg: "splitCamelCase" -> arrayOf("split", "camel", "case")
 */
fun String.splitCamelCase(): Sequence<String> =
    fromCamelCaseRegex.findAll(capitalizeWord()).map {
        val word = StringBuilder(it.groups[2]!!.value)
        if (!it.groups[1]?.value.isNullOrEmpty()) {
            word.insert(0, it.groups[1]!!.value.lowercase())
        }
        word.toString()
    }

/**
 * Expands a camelCase file name to a speech friendly String, omitting the .suffix.
 * ".editorconfig", ".env", ".gitignore" are left intact
 *
 * eg: "StringUtils.kt" -> "string utils"
 */
fun speechFriendlyFileName(fileName: String): String {
    val dot = fileName.indexOf('.')

    val base = if (dot <= 0) fileName else fileName.substring(0, fileName.indexOf('.'))
    return base.expandCamelCase()
}

// TODO: investigate ai.grazie.nlp.similarity.Levenshtein etc
fun findMatchingPhrase(alternatives: Collection<String>, validPhrases: Collection<String>): String? {
    var bestMatch: String? = null
    var bestDistance = Int.MAX_VALUE

    for (utterance in alternatives) {
        for (phrase in validPhrases) {
            val distance = levenshteinDistance(utterance, phrase)
            if (distance < bestDistance) {
                bestDistance = distance
                bestMatch = phrase
            }
        }
    }

    return bestMatch
}
fun levenshteinDistance(s1: String, s2: String): Int {
    val m = s1.length
    val n = s2.length

    if (m == 0) {
        return n
    }
    if (n == 0) {
        return m
    }

    val d = Array(m + 1) { IntArray(n + 1) }

    for (i in 0..m) {
        d[i][0] = i
    }
    for (j in 0..n) {
        d[0][j] = j
    }

    for (j in 1..n) {
        for (i in 1..m) {
            val substitutionCost = if (s1[i - 1] == s2[j - 1]) 0 else 1
            d[i][j] = minOf(
                d[i - 1][j] + 1,
                d[i][j - 1] + 1,
                d[i - 1][j - 1] + substitutionCost
            )
        }
    }

    return d[m][n]
}


class SingleChar(private val char: Char) : CharSequence {
    override val length = 1
    override fun get(index: Int) = char
    override fun subSequence(startIndex: Int, endIndex: Int) = this
}
