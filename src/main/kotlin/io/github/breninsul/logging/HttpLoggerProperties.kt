/*
 * MIT License
 *
 * Copyright (c) 2024 BreninSul
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.breninsul.logging


/**
 * Configuration properties for RestTemplateLogger.
 *
 * This class represents the configuration properties for RestTemplateLogger.
 * It is annotated with @ConfigurationProperties and the prefix "rest-template.logging-interceptor".
 *
 * The properties can be used to configure various aspects of the RestTemplateLogger library.
 * The default values are provided in the constructor.
 * @property enabled Whether the RestTemplateLogger is enabled. Default value is `true`.
 * @property loggingLevel The logging level for RestTemplateLogger. Default value is INFO.
 * @property request The settings for logging various aspects of requests.
 * @property response The settings for logging various aspects of responses.
 * @property maxBodySize The maximum body size to log. Default value is Int.MAX_VALUE.
 * @property order The order of the RestTemplateLogger interceptor. Default value is 0.
 * @property newLineColumnSymbols The number of column symbols for new line. Default value is 14.
 */
open class HttpLoggerProperties(
    var enabled: Boolean = true,
    var loggingLevel: JavaLoggingLevel = JavaLoggingLevel.INFO,
    var request: LogSettings = LogSettings(tookTimeIncluded = false),
    var response: LogSettings = LogSettings(tookTimeIncluded = true),
    var maxBodySize: Int = Int.MAX_VALUE,
    var order: Int = 0,
    var newLineColumnSymbols: Int = 14,
) {
    /**
     * Represents the settings for logging various aspects of requests and responses.
     *
     * @param idIncluded Whether to include the ID in the log output. Default value is `true`.
     * @param uriIncluded Whether to include the URI in the log output. Default value is `true`.
     * @param tookTimeIncluded Whether to include the time taken for the request/response in the log output. Default value is `true`.
     * @param headersIncluded Whether to include the headers in the log output. Default value is `true`.
     * @param bodyIncluded Whether to include the body in the log output. Default value is `true`.
     */
    open class LogSettings(
        var idIncluded: Boolean = true,
        var uriIncluded: Boolean = true,
        var tookTimeIncluded: Boolean = true,
        var headersIncluded: Boolean = true,
        var bodyIncluded: Boolean = true,
        var mask:MaskSettings=MaskSettings()
    )

    /**
     * Represents the mask settings for logging requests and responses.
     *
     * @property maskHeaders The list of headers to be masked in the log output. Default value is ["Authorization"].
     * @property maskJsonBodyKeys The list of keys to be masked in the JSON body of the log output. Default value is ["password", "pass", "code", "token", "secret"].
     * @property maskFormUrlencodedBodyKeys The list of keys to be masked in the form-urlencoded body of the log output. Default value is ["password", "pass", "code", "token", "secret
     * "].
     */
    open class MaskSettings(
        var maskHeaders: List<String> = listOf("Authorization"),
        var maskJsonBodyKeys: List<String> = listOf("password", "pass", "code", "token", "secret"),
        var maskFormUrlencodedBodyKeys: List<String> = listOf("password", "pass", "code", "token", "secret"),
    )
}
