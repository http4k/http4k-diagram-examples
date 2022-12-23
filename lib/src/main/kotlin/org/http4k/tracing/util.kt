package org.http4k.tracing

import java.util.Locale.getDefault

fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }
