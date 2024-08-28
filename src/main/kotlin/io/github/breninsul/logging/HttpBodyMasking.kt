package io.github.breninsul.logging

interface HttpBodyMasking {
    fun mask(message: String?): String
}

interface HttpRequestBodyMasking : HttpBodyMasking

interface HttpResponseBodyMasking : HttpBodyMasking

open class HttpRequestBodyMaskingDelegate(
    protected open val delegate: HttpBodyMasking,
) : HttpRequestBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
}

open class HttpResponseBodyMaskingDelegate(
    protected open val delegate: HttpBodyMasking
) : HttpResponseBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
}

open class HttpRegexJsonBodyMasking(
    protected open val fields: List<String>
) : HttpBodyMasking {
    protected open val emptyBody: String = ""
    protected open val maskedBody: String = "<MASKED>"
    protected open val regexList: List<Regex> = fields.map { "\"($it)\"\\s*:\\s*\"([^\"]*)\"".toRegex() }

    override fun mask(message: String?): String {
        if (message == null) {
            return emptyBody
        }
        val maskedMessage = StringBuilder(message)
        regexList.forEach { regex ->
            val ranges = regex.findAll(maskedMessage).map { it.groups[2]!!.range }.sortedBy { it.last * -1 }
            ranges.forEach { range ->
                maskedMessage.replace(range.first, range.last + 1, maskedBody)
            }
        }
        return maskedMessage.toString()
    }
}

open class HttpRegexFormUrlencodedBodyMasking(
    protected open val fields: List<String>,
) : HttpBodyMasking {
    protected open val emptyBody: String = ""
    protected open val maskedBody: String = "<MASKED>"
    protected open val regexList: List<Regex> = fields.flatMap {
        listOf(
            "($it)(=)([^&]*)(&)".toRegex(),
            "($it)(=)([^&]*)(\$)".toRegex()
        )
    }

    override fun mask(message: String?): String {
        if (message == null) {
            return emptyBody
        }
        val maskedMessage = StringBuilder(message)
        regexList.forEach { regex ->

            val ranges = regex.findAll(maskedMessage).map { it.groups[3]!!.range }.sortedBy { it.last * -1 }

            ranges.forEach { range ->
                maskedMessage.replace(range.first, range.last + 1, maskedBody)
            }
        }
        return maskedMessage.toString()
    }
}
