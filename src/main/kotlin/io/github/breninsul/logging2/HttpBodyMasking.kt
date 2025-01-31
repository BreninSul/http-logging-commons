package io.github.breninsul.logging2

interface HttpBodyMasking {
    fun mask(message: String?): String
    fun type():HttpBodyType
}

interface HttpRequestBodyMasking : HttpBodyMasking

interface HttpResponseBodyMasking : HttpBodyMasking

open class HttpRequestBodyMaskingDelegate(
    protected open val delegate: HttpBodyMasking,
) : HttpRequestBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
    override fun type(): HttpBodyType =delegate.type()
    override fun toString(): String = delegate.toString()
    override fun hashCode(): Int =delegate.hashCode()
    override fun equals(other: Any?): Boolean = delegate == other
}
fun HttpBodyMasking.toHttpRequestBodyMasking():HttpRequestBodyMasking=HttpRequestBodyMaskingDelegate(this)

open class HttpResponseBodyMaskingDelegate(
    protected open val delegate: HttpBodyMasking
) : HttpResponseBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
    override fun type(): HttpBodyType =delegate.type()
    override fun toString(): String = delegate.toString()
    override fun hashCode(): Int =delegate.hashCode()
    override fun equals(other: Any?): Boolean = delegate == other
}
fun HttpBodyMasking.toHttpResponseBodyMasking():HttpResponseBodyMasking=HttpResponseBodyMaskingDelegate(this)

open class HttpRegexJsonBodyMasking(
    protected open val fields: Collection<String>
) : HttpBodyMasking {
    protected open val emptyBody: String = ""
    protected open val maskedBody: String = "<MASKED>"
    protected open val regexList: Collection<Regex> = fields.map { "\"($it)\"\\s*:\\s*\"([^\"]*)\"".toRegex() }

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

    override fun type(): HttpBodyType =JsonBodyType

    override fun hashCode(): Int =fields.joinToString(",").hashCode()
    override fun equals(other: Any?): Boolean =other!=null&&other.hashCode()==hashCode()
}

open class HttpRegexFormBodyMasking(
    protected open val fields: Collection<String>,
) : HttpBodyMasking {
    protected open val emptyBody: String = ""
    protected open val maskedBody: String = "<MASKED>"
    protected open val regexList: Collection<Regex> = fields.flatMap {
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

    override fun type(): HttpBodyType =FormBodyType

    override fun hashCode(): Int =fields.joinToString(",").hashCode()
    override fun equals(other: Any?): Boolean =other!=null&&other.hashCode()==hashCode()
}


open class HttpBodyType(val type:String){
    override fun hashCode()=type.hashCode()
    override fun equals(other: Any?)= type == other
    override fun toString()=type
}
object JsonBodyType:HttpBodyType("json")
object FormBodyType:HttpBodyType("form")