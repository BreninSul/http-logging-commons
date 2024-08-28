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

    protected open val fieldsGroup get() = fields.joinToString("|")
    protected open val regex: Regex = "\"($fieldsGroup)\"\\s*:\\s*\"((\\\\\"|[^\"])*)\"".toRegex()

    override fun mask(message: String?): String {
        if (message == null) {
            return emptyBody
        }
        val ranges = regex.findAll(message).map { it.groups[2]!!.range }.sortedBy { it.last*-1 }

        val maskedMessage = StringBuilder(message)
        ranges.forEach { range ->
            maskedMessage.replace(range.first,range.last+1, maskedBody)
        }
        return maskedMessage.toString()
    }
}

open class HttpRegexFormUrlencodedBodyMasking(
    protected open val fields: List<String> ,
) : HttpBodyMasking {
    protected open val emptyBody: String = ""
    protected open val maskedBody: String = "<MASKED>"

    protected open val fieldsGroup get() = fields.joinToString("|")
    protected open val regex: Regex = "($fieldsGroup)(=)([^&]*)(?:&|\$)".toRegex()

    override fun mask(message: String?): String {
        if (message == null) {
            return emptyBody
        }
        val ranges = regex.findAll(message).map { it.groups[3]!!.range }.sortedBy { it.last*-1 }

        val maskedMessage = StringBuilder(message)
        ranges.forEach { range ->
            maskedMessage.replace(range.first,range.last+1, maskedBody)
        }
        return maskedMessage.toString()
    }
}
