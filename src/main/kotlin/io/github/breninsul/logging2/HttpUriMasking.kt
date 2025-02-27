package io.github.breninsul.logging2

import com.google.re2j.Pattern

interface HttpUriMasking {
    fun mask(uri: String?): String
}

open class HttpRegexUriMasking(
    protected open val fields: Collection<String>,
) : HttpUriMasking {
    protected open val emptyBody: String = ""
    protected open val maskedBody: String = "<MASKED>"
    protected open val regexList: Map<String, Collection<Pattern>> = fields.map {
        it to listOf(
            "($it)(=)([^&]*)(&)".toRE2Pattern(),
            "($it)(=)([^&]*)(\$)".toRE2Pattern(),
        )
    }.toMap()

    override fun mask(uri: String?): String {
        if (uri == null) {
            return emptyBody
        }
        val maskedMessage = StringBuilder(uri)
        val ranges = regexList
            .asSequence()
            .filter { uri.contains("""${it.key}=""") }
            .flatMap { it.value }
            .flatMap { regex ->
                val matcher = regex.matcher(maskedMessage)
                val ranges= mutableListOf<IntRange>()
                while (matcher.find()) {
                    val groupStart = matcher.start(3)
                    val groupEnd = matcher.end(3)-1
                    ranges.add(groupStart..groupEnd)
                }
                ranges}
            .sortedBy { it.last * -1 }
            .toList()
        ranges
            .asSequence()
            .filter { range -> ranges.filter { r -> r != range }.none { r -> r.first <= range.first && r.last >= range.last } }
            .forEach { range ->
                maskedMessage.replace(range.first, range.last + 1, maskedBody)
            }
        return maskedMessage.toString()
    }

    override fun hashCode(): Int = fields.joinToString(",").hashCode()
    override fun equals(other: Any?): Boolean = other != null && other.hashCode() == hashCode()
}
