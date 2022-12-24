package org.example

import org.http4k.core.then
import org.http4k.events.Events
import org.http4k.events.HttpEvent
import org.http4k.filter.ClientFilters.RequestTracing
import org.http4k.filter.ResponseFilters.ReportHttpTransaction
import org.http4k.filter.ServerFilters
import java.time.Clock

fun AppIncomingHttp() = ServerFilters.RequestTracing()

fun AppOutgoingHttp(events: Events) = ReportHttpTransaction(Clock.systemUTC()) {
    events(HttpEvent.Outgoing(it))
}
    .then(RequestTracing())
