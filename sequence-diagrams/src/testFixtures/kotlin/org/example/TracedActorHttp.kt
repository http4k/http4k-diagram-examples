package org.example

import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.events.Events
import org.http4k.events.then
import org.http4k.filter.ClientFilters.ResetRequestTracing
import java.time.Clock

fun TracedActorHttp(
    actorName: String,
    rawHttp: HttpHandler,
    events: Events,
    clock: Clock = Clock.systemUTC()
) = object : HttpHandler by ResetRequestTracing()
    .then(AppOutgoingHttp(AppEvents(actorName).then(events), clock))
    .then(rawHttp) {}
