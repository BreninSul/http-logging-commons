package io.github.breninsul.logging

/**
 * Represents the settings for logging various aspects of requests and responses.
 *
 * @param idIncluded Whether to include the ID in the log output. Default value is `true`.
 * @param uriIncluded Whether to include the URI in the log output. Default value is `true`.
 * @param tookTimeIncluded Whether to include the time taken for the request/response in the log output. Default value is `true`.
 * @param headersIncluded Whether to include the headers in the log output. Default value is `true`.
 * @param bodyIncluded Whether to include the body in the log output. Default value is `true`.
 */
open class HttpLogSettings(
    var idIncluded: Boolean = true,
    var uriIncluded: Boolean = true,
    var tookTimeIncluded: Boolean = true,
    var headersIncluded: Boolean = true,
    var bodyIncluded: Boolean = true,
    var maxBodySize: Int = Int.MAX_VALUE,
    var mask: HttpMaskSettings = HttpMaskSettings(),
) 
