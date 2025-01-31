package io.github.breninsul.logging

/**
 * Defines the settings for HTTP logging, including what details should be included in the logs
 * and how sensitive information should be masked.
 *
 * @property loggingLevel Specifies the logging level to be used when outputting HTTP logs.
 *                        Defaults to `JavaLoggingLevel.INFO`.
 * @property idIncluded Determines if the unique identifier of the HTTP request/response
 *                      should be included in logs. Defaults to `true`.
 * @property uriIncluded Specifies whether the URI of the request should be included in the logs.
 *                       Defaults to `true`.
 * @property tookTimeIncluded Indicates whether the duration of the HTTP transaction (e.g., request/response time)
 *                           should be included in logs. Defaults to `true`.
 * @property headersIncluded Determines whether HTTP headers should be included in the logs. Defaults to `true`.
 * @property bodyIncluded Specifies if the body of HTTP requests/responses should be included in the logs.
 *                        Defaults to `true`.
 * @property maxBodySize Sets the maximum size (in bytes) of the HTTP body to be logged. Body content beyond
 *                       this limit will not be logged. Defaults to `Int.MAX_VALUE`.
 * @property mask Configures settings for masking sensitive data in HTTP requests/responses.
 *                Defaults to an instance of `HttpMaskSettings`.
 */
open class HttpLogSettings(
    var loggingLevel: JavaLoggingLevel = JavaLoggingLevel.INFO,
    var idIncluded: Boolean = true,
    var uriIncluded: Boolean = true,
    var tookTimeIncluded: Boolean = true,
    var headersIncluded: Boolean = true,
    var bodyIncluded: Boolean = true,
    var maxBodySize: Int = Int.MAX_VALUE,
    var mask: HttpMaskSettings = HttpMaskSettings(),
) {
    @Deprecated("Use `loggingLevel` parameter included constructor")
    constructor(idIncluded: Boolean=true,uriIncluded: Boolean=true,tookTimeIncluded: Boolean=true,headersIncluded: Boolean=true,bodyIncluded: Boolean=true,maxBodySize: Int=Int.MAX_VALUE,mask: HttpMaskSettings=HttpMaskSettings()) : this(JavaLoggingLevel.INFO,idIncluded,uriIncluded,tookTimeIncluded,headersIncluded,bodyIncluded,maxBodySize,mask)
}
