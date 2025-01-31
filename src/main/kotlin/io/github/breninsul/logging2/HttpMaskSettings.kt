package io.github.breninsul.logging2

/**
 * Defines the settings for masking sensitive information in HTTP headers, query parameters, and body data.
 *
 * @property maskHeaders A collection of header names whose values will be masked in logs.
 *                       Defaults to masking the "Authorization" header.
 * @property maskQueryParameters A collection of query parameter names whose values will be masked in logs.
 *                               Defaults to masking commonly sensitive parameters like "Authorization",
 *                               "authorization", "token", "secret", "password", and "code".
 * @property maskBodyKeys A map defining the body types (e.g., JSON, form-encoded) and the keys within these body types
 *                        whose values will be masked in logs.
 *                        Defaults to masking sensitive keys like "password", "pass", "code", "token", and "secret"
 *                        for supported body types such as JSON and form-encoded.
 */
open class HttpMaskSettings(
    var maskHeaders: Collection<String> = listOf("Authorization"),
    var maskQueryParameters: Collection<String> = listOf("Authorization", "authorization", "token", "secret", "password", "code"),
    var maskBodyKeys: Map<HttpBodyType, Collection<String>> =
        mapOf(
            JsonBodyType to listOf("password", "pass", "code", "token", "secret"),
            FormBodyType to listOf("password", "pass", "code", "token", "secret")
        )
)

open class HttpBodyType(val type: String) {
    override fun hashCode() = type.hashCode()
    override fun equals(other: Any?) = type == other
    override fun toString() = type
}

object JsonBodyType : HttpBodyType("json")
object FormBodyType : HttpBodyType("form")