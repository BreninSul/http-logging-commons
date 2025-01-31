package io.github.breninsul.logging2

import io.github.breninsul.logging2.HttpConfigHeaders.TECHNICAL_HEADERS
import java.util.*
import java.util.function.Function
import java.util.function.Supplier
import java.util.logging.Level
import java.util.logging.Logger

/**
 * A helper class for HTTP logging with configurable masking for URIs,
 * request bodies, and response bodies. It provides flexible formatting
 * options for headers, footers, and other log outputs, and supports
 * adjustable logging levels for HTTP requests and responses.
 *
 * @param name The name associated with the logger.
 * @param properties The configuration properties for HTTP logging.
 * @param uriMaskersGetFunction Function for generating URI maskers based
 *    on configuration.
 * @param requestBodyJsonKeyMaskersGetFunction Function for generating JSON
 *    key maskers for request bodies.
 * @param requestBodyFormKeyMaskersGetFunction Function for generating form
 *    key maskers for request bodies.
 * @param responseBodyJsonKeyMaskersGetFunction Function for generating
 *    JSON key maskers for response bodies.
 * @param responseBodyFormKeyMaskersGetFunction Function for generating
 *    form key maskers for response bodies.
 * @param logger The logger instance used for logs.
 * @param headerFormat The format string for log headers, including
 *    placeholders for dynamic content.
 * @param footerFormat The format string for log footers, including
 *    placeholders for dynamic content.
 * @param newLineFormat The format used for new lines in log outputs.
 * @param maskedFormat The placeholder used for masked sensitive data.
 * @param tooBigBodyFormat The placeholder used for bodies that exceed the
 *    maximum allowed length.
 */
open class HttpLoggingHelper(
    protected open val name: String,
    protected open val properties: HttpLoggingProperties,
    protected open val uriMaskersGetFunction: Function<Collection<String>, Collection<HttpUriMasking>> = Function { listOf(HttpRegexUriMasking(it)) },
    protected open val bodyKeyMaskersGetFunctions: Map<HttpBodyType, Function<Collection<String>, Collection<HttpBodyMasking>>> = mapOf(
        JsonBodyType to Function { listOf(HttpRegexJsonBodyMasking(it)) },
        FormBodyType to Function { listOf(HttpRegexFormBodyMasking(it)) }
    ),
    protected open val logger: Logger = Logger.getLogger(HttpLoggingHelper::class.java.name),
    protected open val headerFormat: String = "\n===========================%name% %type% begin===========================",
    protected open val footerFormat: String = "===========================%name% %type% end  ===========================",
    open val newLineFormat: String = "=",
    open val maskedFormat: String = "<MASKED>",
    protected open val tooBigBodyFormat: String = "<TOO BIG %contentLength% bytes>"
) {
    protected open val uriMaskers: Collection<HttpUriMasking> = uriMaskersGetFunction.apply(properties.request.mask.maskQueryParameters).distinct()
    protected open val requestBodyMaskers: Collection<HttpRequestBodyMasking> =
        ((bodyKeyMaskersGetFunctions[JsonBodyType]?.apply(properties.request.mask.maskJsonBodyKeys)?.map { it.toHttpRequestBodyMasking() } ?: listOf())
                + (bodyKeyMaskersGetFunctions[FormBodyType]?.apply(properties.request.mask.maskFormBodyKeys)?.map { it.toHttpRequestBodyMasking() } ?: listOf()))
            .distinct()
    protected open val responseBodyMaskers: Collection<HttpResponseBodyMasking> =
        ((bodyKeyMaskersGetFunctions[JsonBodyType]?.apply(properties.response.mask.maskJsonBodyKeys)?.map { it.toHttpResponseBodyMasking() } ?: listOf())
                + (bodyKeyMaskersGetFunctions[FormBodyType]?.apply(properties.response.mask.maskFormBodyKeys)?.map { it.toHttpResponseBodyMasking() } ?: listOf()))
            .distinct()

    /**
     * Represents the logging level for the logger.
     *
     * The logging level determines the verbosity of the log output.
     *
     * @property loggingLevel The logging level for the logger.
     */
    open val loggingLevel: Level = properties.loggingLevel.javaLevel

    /**
     * Specifies the logging level for HTTP request messages.
     *
     * This property determines the verbosity of logging for HTTP requests. The
     * value is derived from the `properties.request.loggingLevel.javaLevel`
     * setting, which is typically configured in the application properties or
     * settings file.
     */
    open val requestLoggingLevel: Level = properties.request.loggingLevel.javaLevel

    /**
     * Represents the logging level to be used for response logs.
     *
     * The value of this property is derived from the response logging level
     * defined in the application properties. It determines the verbosity of
     * response log messages. Typically, it can be set to different levels like
     * DEBUG, INFO, WARN, etc., based on the requirements for diagnosing issues
     * or general monitoring.
     */
    open val responseLoggingLevel: Level = properties.response.loggingLevel.javaLevel


    /**
     * Retrieves the formatted header line based on the given type.
     *
     * This method replaces the placeholders in the header format string with
     * the actual values
     */
    open fun getHeaderLine(type: Type) = headerFormat.replace("%type%", type.stringTemplateType).replace("%name%", name)

    /**
     * Retrieves the formatted footer line based on the given type.
     *
     * This method replaces the placeholders in the footer format string with
     * the actual values.
     *
     * @param type The type of the log message (Request or Response).
     * @return The formatted footer line.
     */
    open fun getFooterLine(type: Type) = footerFormat.replace("%type%", type.stringTemplateType).replace("%name%", name)


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
     * This method determines whether the ID should be included in the log
     * message based on the `logEnabledForRequest` flag and the `idIncluded`
     * property of the given type. If the ID should be included, it formats the
     * ID line using the provided request ID.
     *
     * @param logEnabledForRequest Indicates if logging is enabled for the
     *    request.
     * @param rqId The request ID to be included in the log message.
     * @param type The type of the log message (Request or Response).
     * @return The formatted ID string if logging is enabled and the type
     *    properties indicate the ID should be included, otherwise null.
     */
    open fun getIdString(logEnabledForRequest: Boolean?, rqId: String, type: Type): String? {
        return if (logEnabledForRequest ?: type.properties().idIncluded) formatLine("ID", rqId)
        else null

    }


    /**
     * Retrieves the formatted URI string based on the provided parameters and
     * type.
     *
     * This method determines whether logging for the URI is enabled, applies
     * the appropriate masking to the URI based on the specified parameters,
     * and formats the resulting URI string for logging.
     *
     * @param uriParametersToMask A collection of parameter names in the URI to
     *    mask. If null, the default URI maskers are used.
     * @param logEnabledForRequest Indicates if logging is enabled for the
     *    request. If null, the logging setting is determined from the provided
     *    type's properties.
     * @param uri The original URI to be logged.
     * @param type The type of the log message (Request or Response).
     * @return The formatted and masked URI string if logging is enabled for
     *    the request and URI inclusion is allowed for the specified type.
     *    Returns null otherwise.
     */
    open fun getUriString(uriParametersToMask: Collection<String>?, logEnabledForRequest: Boolean?, uri: String, type: Type): String? {
        val enabled = (logEnabledForRequest ?: type.properties().uriIncluded)
        if (!enabled) return null
        val maskers = uriParametersToMask?.let { p -> uriMaskersGetFunction.apply(p) } ?: uriMaskers
        return formatLine("URI", maskers.fold(uri) { b, it -> it.mask(b) })
    }

    /**
     * Retrieves the result of the "Took" operation as a formatted string.
     *
     * @param logEnabledForRequest Indicates if logging is enabled for the
     *    request.
     * @param startTime The start time of the operation.
     * @param type The type of the log message.
     * @return The formatted "Took" string if it is included in logging,
     *    otherwise null.
     */
    open fun getTookString(logEnabledForRequest: Boolean?, startTime: Long, type: Type): String? {
        val enabled = (logEnabledForRequest ?: type.properties().tookTimeIncluded)
        if (!enabled) return null
        return formatLine("Took", "${System.currentTimeMillis() - startTime} ms")
    }


    /**
     * Retrieves the formatted headers string for logging purposes.
     *
     * This method formats and masks the provided headers for logging, based
     * on the input parameters and the given type's properties. It takes into
     * account whether logging is enabled, whether headers should be included,
     * and applies masking to sensitive headers if necessary.
     *
     * @param headersToMask A collection of header names to mask. If null,
     *    default masked headers are determined based on the type's properties.
     * @param logEnabledForRequest Indicates if logging is enabled for the
     *    request. If null, the logging setting is determined from the provided
     *    type's properties.
     * @param headers A map of headers where the key is the header name and the
     *    value is a list of header values.
     * @param type The type of the log message (Request or Response).
     * @return The formatted and masked headers string if logging is enabled
     *    and headers inclusion is allowed for the given type. Returns null
     *    otherwise.
     */
    open fun getHeadersString(headersToMask: Collection<String>?, logEnabledForRequest: Boolean?, headers: Map<String, List<String>>, type: Type): String? {
        val enabled = (logEnabledForRequest ?: type.properties().headersIncluded)
        if (!enabled) return null
        val maskHeaders = headersToMask ?: getMaskedHeaders(type)
        return formatLine("Headers", headers.getHeadersString(maskHeaders))
    }

    /**
     * Retrieves the masked headers based on the given type.
     *
     * @param type The Type of the log message (Request or Response).
     * @return The masked headers for the given type.
     */
    protected open fun getMaskedHeaders(type: Type) = when (type) {
        Type.REQUEST -> properties.request.mask.maskHeaders
        Type.RESPONSE -> properties.response.mask.maskHeaders
    }

    /**
     * Retrieves the headers string in a formatted way from a map of headers.
     *
     * @param maskingHeaders The list of headers to be masked.
     * @return The formatted headers string.
     */
    protected open fun Map<String, List<String>>.getHeadersString(maskingHeaders: Collection<String>) =
        (this.asSequence()
            .filter { h -> !TECHNICAL_HEADERS.any { th -> th.contentEquals(h.key) } }
            .map { "${it.key}:${if (maskingHeaders.any { m -> m.contentEquals(it.key, true) }) maskedFormat else it.value.joinToString(",")}" }
            .joinToString(";"))


    /**
     * Retrieves the body string for logging purposes, with optional masking applied based on provided parameters.
     *
     * @param bodyKeysToMask A map of `HttpBodyType` to a collection of keys that should be masked in the body.
     *                       If null, masking keys will be determined by the type's properties.
     * @param logEnabledForRequest A flag indicating whether logging is enabled for the request.
     *                              If null, the default behavior will be determined by the type's properties.
     * @param bodySupplier A supplier that provides the body as a string for processing and optional masking.
     * @param type The type of the log message (Request or Response), which determines the context for body masking and inclusion.
     * @return The optionally masked body string if logging is enabled; otherwise, returns null.
     */
    open fun getBodyString(bodyKeysToMask: Map<HttpBodyType,Collection<String>>?,
                           logEnabledForRequest: Boolean?, bodySupplier: Supplier<String?>, type: Type): String? {
        val enabled = (logEnabledForRequest ?: type.properties().bodyIncluded)
        if (!enabled) return null
        val paramMaskers = bodyKeysToMask?.flatMap { e->bodyKeyMaskersGetFunctions[e.key]!!.apply(e.value) }

        val maskers = when (type) {
            Type.REQUEST -> paramMaskers ?: requestBodyMaskers
            Type.RESPONSE -> paramMaskers ?: responseBodyMaskers
        }
        return getBodyString(maskers, logEnabledForRequest, bodySupplier, type)
    }

    /**
     * Retrieves the body string, optionally masking it based on the provided
     * body maskers.
     *
     * @param bodyMaskers A collection of `HttpBodyMasking` instances to apply
     *    for masking the body. If null, default maskers are used depending on
     *    the type.
     * @param logEnabledForRequest A flag indicating whether logging is enabled
     *    for the specific request. If null, the default behavior is determined
     *    by the `type`.
     * @param bodySupplier A supplier that provides the original body as a
     *    string. This function ensures that the body is retrieved and
     *    optionally masked.
     * @param type The type of the body, either REQUEST or RESPONSE,
     *    determining the context for body masking and inclusion.
     * @return The (optionally masked) body string if logging is enabled;
     *    otherwise, returns null.
     */
    open fun getBodyString(bodyMaskers: Collection<HttpBodyMasking>?, logEnabledForRequest: Boolean?, bodySupplier: Supplier<String?>, type: Type): String? {
        val enabled = (logEnabledForRequest ?: type.properties().bodyIncluded)
        if (!enabled) return null

        val maskers = when (type) {
            Type.REQUEST -> (bodyMaskers ?: requestBodyMaskers);
            Type.RESPONSE -> (bodyMaskers ?: responseBodyMaskers);
        }
        return formatLine("Body", maskers.fold(bodySupplier.get()) { b, it -> it.mask(b) })
    }

    /**
     * Constructs a too big message indicating the size of the content.
     *
     * @param contentLength The length of the content in bytes.
     * @return The constructed too big message.
     */
    open fun constructTooBigMsg(contentLength: Long) = tooBigBodyFormat.replace("%contentLength%", contentLength.toString())

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
    protected open fun Type.properties(): HttpLogSettings {
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

    companion object {
        /** A random number generator. */
        protected val random = Random()
    }
}
