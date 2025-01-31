package io.github.breninsul.logging

/**
 * Represents the settings for masking sensitive information in HTTP requests and responses.
 *
 * @param maskHeaders A list of header names whose values should be masked. Default value is `listOf("Authorization")`.
 * @param maskQueryParameters A list of query parameter names whose values should be masked. Default value is `listOf("Authorization", "authorization", "token", "secret", "password
 * ", "code")`. Only for request
 * @param maskJsonBodyKeys A list of JSON body keys whose values should be masked. Default value is `listOf("password", "pass", "code", "token", "secret")`.
 * @param maskFormBodyKeys A list of form-urlencoded body keys whose values should be masked. Default value is `listOf("password", "pass", "code", "token", "secret")`.  Renamed from `maskFormUrlencodedBodyKeys` @since `1.3.0`
 */
open class HttpMaskSettings(
    var maskHeaders: List<String> = listOf("Authorization"),
    var maskQueryParameters: List<String> = listOf("Authorization","authorization","token", "secret","password","code"),
    var maskJsonBodyKeys: List<String> = listOf("password", "pass", "code", "token", "secret"),
    var maskFormBodyKeys: List<String> = listOf("password", "pass", "code", "token", "secret"),
) {
    @Deprecated("Renamed to maskFormBodyKeys", ReplaceWith("maskFormBodyKeys"))
    var maskFormUrlencodedBodyKeys: List<String>
        get() = maskFormBodyKeys
        set(value) {
            maskJsonBodyKeys = value
        };
}
