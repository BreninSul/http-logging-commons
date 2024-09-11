package io.github.breninsul.logging

interface HttpUriMasking {
    fun mask(uri: String?): String
}

open class HttpRegexUriMasking(
    protected open val fields: List<String>,
) : HttpUriMasking {
    protected open val emptyBody: String = ""
    protected open val maskedBody: String = "<MASKED>"
    protected open val regexList: List<Regex> = fields.flatMap {
        listOf(
            "($it)(=)([^&]*)(&)".toRegex(),
            "($it)(=)([^&]*)(\$)".toRegex()
        )
    }

    override fun mask(uri: String?): String {
        if (uri == null) {
            return emptyBody
        }
        val maskedMessage = StringBuilder(uri)
        regexList.forEach { regex ->

            val ranges = regex.findAll(maskedMessage).map { it.groups[3]!!.range }.sortedBy { it.last * -1 }

            ranges.forEach { range ->
                maskedMessage.replace(range.first, range.last + 1, maskedBody)
            }
        }
        return maskedMessage.toString()
    }
}
