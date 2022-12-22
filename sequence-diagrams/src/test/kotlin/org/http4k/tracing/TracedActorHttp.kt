package org.http4k.tracing

import org.example.AppOutgoingHttp
import org.example.tracing.AppEvents
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.events.Events
import org.http4k.events.then
import org.http4k.filter.ClientFilters
import java.time.Clock

fun TracedActorHttp(
    actorName: String,
    rawHttp: HttpHandler,
    events: Events,
    clock: Clock = Clock.systemUTC()
) = object : HttpHandler by ClientFilters.ResetRequestTracing()
    .then(AppOutgoingHttp(AppEvents(AppName(actorName)).then(events), clock))
    .then(rawHttp) {}
