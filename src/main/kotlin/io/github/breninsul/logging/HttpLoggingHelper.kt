package io.github.breninsul.logging

import io.github.breninsul.logging.HttpConfigHeaders.TECHNICAL_HEADERS
import java.util.*
import java.util.function.Supplier
import java.util.logging.Level
import java.util.logging.Logger

open class HttpLoggingHelper(
    protected open val properties: HttpLoggerProperties,
    protected open val requestBodyMaskers: List<HttpRequestBodyMasking>,
    protected open val responseBodyMaskers: List<HttpResponseBodyMasking>,
    javaLoggingLevel: JavaLoggingLevel = JavaLoggingLevel.INFO,
    open val logger: Logger = Logger.getLogger(HttpLoggingHelper::class.java.name),
    open val headerFormat: String = "\n===========================%name% %type% begin===========================",
    open val footerFormat: String = "===========================%name% %type% end  ===========================",
    open val newLineFormat: String = "=",
    open val maskedFormat:String="<MASKED>",
    open val tooBigBodyFormat:String="<TOO BIG %contentLength% bytes>"

) {
    protected open val loggingLevel: Level = javaLoggingLevel.javaLevel



    /**
     * Retrieves the ID string for logging purposes.
     *
     * This method generates a random integer between 0 and 10,000,000, pads
     * the integer with leading zeros to reach a length of 7 digits, and
     * returns a substring of the padded integer. The returned ID string
     * consists of the first four characters of the substring, followed by a
     * hyphen, and then the fifth and subsequent characters of the substring.
     *
     * @return The formatted ID string.
     */
    open fun getIdString(): String {
        val integer = random.nextInt(10000000)
        val leftPad = integer.toString().padStart(7, '0')
        return leftPad.substring(0, 4) + '-' + leftPad.substring(5)
    }
    /**
     * Retrieves the ID string for logging purposes.
     *
     * @param rqId The request ID.
     * @param type The type of the log message.
     * @return The formatted ID string if it is included in logging, otherwise
     *     null.
     */
    protected open fun getIdString(rqId: String, type: Type): String? {
        return if (type.properties().idIncluded) formatLine("ID", rqId)
        else null

    }

    /**
     * Retrieves the URI string for logging purposes.
     *
     * @param logEnabledForRequest Indicates if logging is enabled for the
     *     request.
     * @param uri The URI string.
     * @param type The type of the log message (Request or Response).
     * @return The formatted URI string if logging is enabled for the request
     *     and the type properties indicate that the URI should be included in
     *     the log message, otherwise null.
     */
     open fun getUriString(logEnabledForRequest: Boolean?, uri: String, type: Type): String? {
        return if (logEnabledForRequest ?: type.properties().uriIncluded) formatLine("URI", uri)
        else null
    }

    /**
     * Retrieves the result of the "Took" operation as a formatted string.
     *
     * @param logEnabledForRequest Indicates if logging is enabled for the
     *     request.
     * @param startTime The start time of the operation.
     * @param type The type of the log message.
     * @return The formatted "Took" string if it is included in logging,
     *     otherwise null.
     */
     open fun getTookString(logEnabledForRequest: Boolean?, startTime: Long, type: Type): String? {
        return if (logEnabledForRequest ?: type.properties().tookTimeIncluded) formatLine("Took", "${System.currentTimeMillis() - startTime} ms")
        else null
    }

    /**
     * Retrieves the formatted headers string based on the given Headers object
     * and type.
     *
     * @param logEnabledForRequest Indicates if logging is enabled for the
     *     request.
     * @param headers The HttpHeaders object containing the headers
     *     information.
     * @param type The Type of the log message (Request or Response).
     * @return The formatted headers string if headersIncluded is true for the
     *     given type, otherwise null.
     */
     open fun getHeadersString(logEnabledForRequest: Boolean?, headers: Map<String,List<String>>, type: Type): String? {
        val maskHeaders = getMaskedHeaders(type)
        return if (logEnabledForRequest ?: type.properties().bodyIncluded) formatLine("Headers", headers.getHeadersString(maskHeaders))
        else null
    }

    protected  open fun getMaskedHeaders(type: Type) = when (type) {
        Type.REQUEST -> properties.request.mask.maskHeaders
        Type.RESPONSE -> properties.response.mask.maskHeaders
    }

    protected open fun Map<String,List<String>>.getHeadersString(maskingHeaders:List<String>) =
        (this.asSequence()
            .filter { h->!TECHNICAL_HEADERS.any { th->th.contentEquals(h.key) } }
            .map { "${it.key}:${if(maskingHeaders.any { m->m.contentEquals(it.key,true) }) maskedFormat else it.value.joinToString(",")}" }
            .joinToString(";"))
    /**
     * Retrieves the body string based on the given body and type.
     *
     * @param logEnabledForRequest Indicates if logging is enabled for the
     *     request.
     * @param bodySupplier The body string Supplier to be included in the log message.
     * @param type The type of the log (Request or Response).
     * @return The formatted body string if bodyIncluded is true for the given
     *     type, otherwise null.
     */
     open fun getBodyString(logEnabledForRequest: Boolean?, bodySupplier: Supplier<String?>, type: Type): String? {
        val maskers=when(type){
            Type.REQUEST->requestBodyMaskers
            Type.RESPONSE->responseBodyMaskers
        }
        return if (logEnabledForRequest ?: type.properties().bodyIncluded) formatLine("Body", maskers.fold(bodySupplier.get()){b,it->it.mask(b)})
        else null
    }

    /**
     * Formats a line of log message with name and value.
     *
     * @param name The name of the line.
     * @param value The value of the line.
     * @return The formatted line.
     */
     open fun formatLine(name: String, value: String?): String {
        val lineStart = "${newLineFormat}${name}".padEnd(properties.newLineColumnSymbols, ' ')
        return "${lineStart}: $value"
    }
    /**
     * Retrieves the log settings for the given type.
     *
     * @return The log settings for the type.
     */
    protected open fun Type.properties(): HttpLoggerProperties.LogSettings {
        return when (this) {
            Type.REQUEST -> properties.request
            Type.RESPONSE -> properties.response
        }
    }

    /**
     * Enum class representing the type of the log message. It can be either
     * Request or Response.
     */
    enum class Type(
        val stringTemplateType: String,
    ) {
        REQUEST("Request"),
        RESPONSE("Response"),
    }
    companion object{
        /** A random number generator. */
        protected val random = Random()
    }
}
