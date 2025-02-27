package io.github.breninsul.logging2

import com.google.re2j.Pattern

interface HttpBodyMasking {
    fun mask(message: String?): String
    fun type(): HttpBodyType
}

interface HttpRequestBodyMasking : HttpBodyMasking

interface HttpResponseBodyMasking : HttpBodyMasking

open class HttpRequestBodyMaskingDelegate(
    protected open val delegate: HttpBodyMasking,
) : HttpRequestBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
    override fun type(): HttpBodyType = delegate.type()
    override fun toString(): String = delegate.toString()
    override fun hashCode(): Int = delegate.hashCode()
    override fun equals(other: Any?): Boolean = delegate == other
}

fun HttpBodyMasking.toHttpRequestBodyMasking(): HttpRequestBodyMasking = HttpRequestBodyMaskingDelegate(this)

open class HttpResponseBodyMaskingDelegate(
    protected open val delegate: HttpBodyMasking
) : HttpResponseBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
    override fun type(): HttpBodyType = delegate.type()
    override fun toString(): String = delegate.toString()
    override fun hashCode(): Int = delegate.hashCode()
    override fun equals(other: Any?): Boolean = delegate == other
}

fun HttpBodyMasking.toHttpResponseBodyMasking(): HttpResponseBodyMasking = HttpResponseBodyMaskingDelegate(this)

open class HttpRegexJsonBodyMasking(
    protected open val fields: Collection<String>
) : HttpBodyMasking {
    protected open val emptyBody: String = ""
    protected open val maskedBody: String = "<MASKED>"
    protected open val regexList: Map<String, Collection<Pattern>> =
        fields.map { f ->
            f to listOf(
                //int or bool
                """"($f)"\s*:\s*([+-]?\d+|true|false)\s*(,|\})""".toRE2Pattern(),
                // string
                """"($f)"\s*:\s*"((\\"|[^"])*)"""".toRE2Pattern(),
                // array
                """"($f)"\s*:\s*\[(\s*(?:"(?:\\.|[^"\\])*"\s*,?\s*)*)\]""".toRE2Pattern(),
                // object
                """"($f)"\s*:\s*\{([^{}]*(?:\{[^{}]*\}[^{}]*)*)\}""".toRE2Pattern()

            )
        }.toMap()

    override fun mask(message: String?): String {
        if (message == null) {
            return emptyBody
        }
        val maskedMessage = StringBuilder(message)
        val ranges = regexList
            .asSequence()
            .filter { message.contains(""""${it.key}"""") }
            .flatMap { it.value }
            .flatMap { regex ->
                val matcher = regex.matcher(maskedMessage)
                val ranges= mutableListOf<IntRange>()
                while (matcher.find()) {
                    val groupStart = matcher.start(2)
                    val groupEnd = matcher.end(2)-1
                    ranges.add(groupStart..groupEnd)
                }
                ranges}
            .sortedBy { it.last * -1 }
            .toList()
        ranges
            .filter { range -> ranges.filter { r -> r != range }.none { r -> r.first <= range.first && r.last >= range.last } }
            .asSequence()
            .forEach { range ->
                maskedMessage.replace(range.first, range.last + 1, maskedBody)
            }
        return maskedMessage.toString()
    }

    override fun type(): HttpBodyType = JsonBodyType

    override fun hashCode(): Int = (javaClass.simpleName+fields.joinToString(",")).hashCode()
    override fun equals(other: Any?): Boolean = other != null && other.hashCode() == hashCode()
}

open class HttpRegexFormBodyMasking(
    protected open val fields: Collection<String>,
) : HttpBodyMasking {
    protected open val emptyBody: String = ""
    protected open val maskedBody: String = "<MASKED>"
    protected open val regexList: Map<String, Collection<Pattern>> = fields.map { f ->
        f to listOf(
            "($f)(=)([^&]*)(&)".toRE2Pattern(),
            "($f)(=)([^&]*)(\$)".toRE2Pattern()
        )
    }.toMap()

    override fun mask(message: String?): String {
        if (message == null) {
            return emptyBody
        }
        val maskedMessage = StringBuilder(message)
        val ranges = regexList
            .asSequence()
            .filter { message.contains("""${it.key}=""") }
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

    override fun type(): HttpBodyType = FormBodyType

    override fun hashCode(): Int = (javaClass.simpleName+fields.joinToString(",")).hashCode()
    override fun equals(other: Any?): Boolean = other != null && other.hashCode() == hashCode()
}


