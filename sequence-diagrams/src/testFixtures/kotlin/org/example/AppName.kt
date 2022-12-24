package org.example

import org.http4k.events.EventFilter
import org.http4k.events.MetadataEvent
import org.http4k.events.plus
import org.http4k.tracing.TraceActorResolver

object AppName : TraceActorResolver {
    override fun invoke(p1: MetadataEvent) = p1.metadata["app"].toString()
}

fun AddAppName(appName: String) = EventFilter { next ->
    {
        next(it + ("app" to appName))
    }
}
