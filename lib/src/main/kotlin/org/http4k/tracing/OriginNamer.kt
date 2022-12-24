package org.http4k.tracing

import org.http4k.events.MetadataEvent

fun interface OriginNamer : (MetadataEvent) -> String