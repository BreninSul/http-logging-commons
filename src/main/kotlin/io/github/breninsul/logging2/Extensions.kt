package io.github.breninsul.logging2

import com.google.re2j.Pattern

fun String.toRE2Pattern(): Pattern {
    return Pattern.compile(this)
}