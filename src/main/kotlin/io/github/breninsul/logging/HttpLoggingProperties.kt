package io.github.breninsul.logging

open class HttpLoggingProperties(
    var enabled: Boolean = true,
    var loggingLevel: JavaLoggingLevel = JavaLoggingLevel.INFO,
    var request: HttpLogSettings = HttpLogSettings(tookTimeIncluded = false),
    var response: HttpLogSettings = HttpLogSettings(tookTimeIncluded = true),
    var maxBodySize: Int = Int.MAX_VALUE,
    var order: Int = 0,
    var newLineColumnSymbols: Int = 14,
)