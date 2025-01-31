package io.github.breninsul.logging2

open class HttpLoggingProperties(
    var enabled: Boolean = true,
    var loggingLevel: JavaLoggingLevel = JavaLoggingLevel.INFO,
    var request: HttpLogSettings = HttpLogSettings(loggingLevel=loggingLevel,tookTimeIncluded = false),
    var response: HttpLogSettings = HttpLogSettings(loggingLevel=loggingLevel,tookTimeIncluded = true),
    var order: Int = 0,
    var newLineColumnSymbols: Int = 14,
)