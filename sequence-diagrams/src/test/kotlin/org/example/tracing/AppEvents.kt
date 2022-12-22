package org.example.tracing

import org.http4k.events.EventFilter
import org.http4k.events.EventFilters
import org.http4k.events.EventFilters.AddEventName
import org.http4k.events.EventFilters.AddTimestamp
import org.http4k.events.plus
import org.http4k.events.then
import org.http4k.tracing.SystemDescriptor
import java.time.Clock

fun AppEvents(name: SystemDescriptor, clock: Clock = Clock.systemUTC()) =
    EventFilters.AddZipkinTraces()
        .then(AddTimestamp(clock))
        .then(AddEventName())
        .then(AddAppName(name))

private fun AddAppName(systemDescriptor: SystemDescriptor) = EventFilter { next ->
    {
        next(it + ("app" to systemDescriptor.name))
    }
}
