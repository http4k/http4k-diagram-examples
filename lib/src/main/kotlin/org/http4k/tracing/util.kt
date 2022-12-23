package org.http4k.tracing

import org.http4k.events.MetadataEvent
import org.http4k.filter.ZipkinTraces
import java.util.Locale.getDefault

fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }

internal fun MetadataEvent.traces() = (metadata["traces"] as? ZipkinTraces)
