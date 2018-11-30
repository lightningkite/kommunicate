package com.lightningkite.kommunicate

fun String.urlEncode() = buildString(this.length*2) {
    for (c in this) {
        //ALPHA  DIGIT  "-" / "." / "_" / "~"
        when (c) {
            ' ' -> append('+')
            '-', '.', '_', '~',
            in '0'..'9',
            in 'a'..'z',
            in 'A'..'Z' -> append(c)
            else -> {
                append('%')
                append(c.toInt().toString(16))
            }
        }
    }
}