package org.http4k.tracing.renderer

internal fun String.identifier() = filter { it.isLetterOrDigit() }