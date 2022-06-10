package org.http4k.tracing

import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.events.*
import org.http4k.filter.ClientFilters
import java.time.Clock

class TracedActorHttp(
    actorName: String,
    rawHttp: HttpHandler,
    events: Events,
    clock: Clock = Clock.systemUTC()
) : HttpHandler by ClientFilters.ResetRequestTracing()
    .then(
        AppOutgoingHttp(
            AppEvents(AppName(actorName)).then(events),
            clock,
        )
    ).then(rawHttp)

fun AppEvents(name: SystemDescriptor, clock: Clock = Clock.systemUTC()) =
    EventFilters.AddZipkinTraces()
        .then(EventFilters.AddTimestamp(clock))
        .then(EventFilters.AddEventName())
        .then(AddAppName(name))

private fun AddAppName(systemDescriptor: SystemDescriptor) = EventFilter { next ->
    {
        next(it + ("app" to systemDescriptor.name))
    }
}
