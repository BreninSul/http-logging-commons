package io.github.breninsul.logging

/**
 * The RestTemplateConfigHeaders class defines constants for the technical headers used in the RestTemplate configuration.
 * These headers can be set in the HttpRequest headers to control various logging behaviors.
 */
object HttpConfigHeaders {
    const val LOG_REQUEST_URI: String = "LOG_REQUEST_URI_TECHNICAL_HEADER"
    const val LOG_REQUEST_HEADERS: String = "LOG_REQUEST_HEADERS_TECHNICAL_HEADER"
    const val LOG_REQUEST_BODY: String = "LOG_REQUEST_BODY_TECHNICAL_HEADER"
    const val LOG_REQUEST_TOOK_TIME: String = "LOG_REQUEST_TOOK_TIME_TECHNICAL_HEADER"

    const val LOG_RESPONSE_URI: String = "LOG_RESPONSE_URI_TECHNICAL_HEADER"
    const val LOG_RESPONSE_HEADERS: String = "LOG_RESPONSE_HEADERS_TECHNICAL_HEADER"
    const val LOG_RESPONSE_BODY: String = "LOG_RESPONSE_BODY_TECHNICAL_HEADER"
    const val LOG_RESPONSE_TOOK_TIME: String = "LOG_RESPONSE_TOOK_TIME_TECHNICAL_HEADER"
    //You can add your headers here
    val TECHNICAL_HEADERS = mutableListOf(LOG_REQUEST_URI, LOG_REQUEST_HEADERS, LOG_REQUEST_BODY, LOG_REQUEST_TOOK_TIME, LOG_RESPONSE_URI, LOG_RESPONSE_HEADERS, LOG_RESPONSE_BODY, LOG_RESPONSE_TOOK_TIME)
}
