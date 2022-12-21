package org.http4k.tracing.util

import java.util.Locale.getDefault

fun String.capitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }
