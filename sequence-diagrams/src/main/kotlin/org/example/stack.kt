package org.example

import org.http4k.core.Filter
import org.http4k.core.NoOp
import org.http4k.core.then
import org.http4k.events.Events
import org.http4k.events.HttpEvent
import org.http4k.filter.ClientFilters.RequestTracing
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.http4k.filter.ResponseFilters.ReportHttpTransaction
import org.http4k.filter.ServerFilters
import java.time.Clock

fun AppIncomingHttp() =
    Filter.NoOp
        .then(ServerFilters.RequestTracing())
        .then(PrintRequestAndResponse())

fun AppOutgoingHttp(events: Events, clock: Clock = Clock.systemUTC()) =
    Filter.NoOp
        .then(ReportHttpTransaction(clock) { events(HttpEvent.Outgoing(it)) })
        .then(RequestTracing())
        .then(PrintRequestAndResponse())
