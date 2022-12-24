package org.example

import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.events.Events
import org.http4k.events.then
import org.http4k.filter.ClientFilters.ResetRequestTracing

fun TracedActorHttp(rawHttp: HttpHandler, events: Events, actorName: String) =
    ResetRequestTracing()
        .then(AppOutgoingHttp(AppEvents(actorName).then(events)))
        .then(rawHttp)
