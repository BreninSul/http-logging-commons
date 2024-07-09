package io.github.breninsul.logging

/**
 * Represents the mask settings for logging requests and responses.
 *
 * @property maskHeaders The list of headers to be masked in the log output. Default value is ["Authorization"].
 * @property maskJsonBodyKeys The list of keys to be masked in the JSON body of the log output. Default value is ["password", "pass", "code", "token", "secret"].
 * @property maskFormUrlencodedBodyKeys The list of keys to be masked in the form-urlencoded body of the log output. Default value is ["password", "pass", "code", "token", "secret
 * "].
 */
open class HttpMaskSettings(
    var maskHeaders: List<String> = listOf("Authorization"),
    var maskJsonBodyKeys: List<String> = listOf("password", "pass", "code", "token", "secret"),
    var maskFormUrlencodedBodyKeys: List<String> = listOf("password", "pass", "code", "token", "secret"),
) 
