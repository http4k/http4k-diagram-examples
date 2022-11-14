package org.example.oauth

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.NoOp
import org.http4k.core.then
import org.http4k.events.Events
import org.http4k.events.HttpEvent
import org.http4k.events.then
import org.http4k.filter.ClientFilters
import org.http4k.filter.ResponseFilters
import org.http4k.routing.reverseProxy
import org.http4k.tracing.AppEvents
import org.http4k.tracing.AppIncomingHttp
import org.http4k.tracing.AppName
import org.http4k.tracing.TraceReportingEvents

fun Browser(relyingParty: HttpHandler, authServer: HttpHandler, events: TraceReportingEvents) = Filter.NoOp
    .then(Filter { next ->
        {
            next(it.uri(it.uri.host(it.header("host")!!)).removeHeader("host"))
        }
    })
    .then(AppIncomingHttp())
    .then(ClientFilters.FollowRedirects())
    .then(ClientFilters.Cookies())
    .then(Filter { next ->
        {
            next(it.removeHeader("host"))
        }
    })
    .then(
        reverseProxy(
            "relying-party" to proxiedOutbound(events, relyingParty),
            "auth-server" to proxiedOutbound(events, authServer)
        )
    )

private fun proxiedOutbound(events: Events, client: HttpHandler) =
    Filter.NoOp
        .then(ResponseFilters.ReportHttpTransaction { AppEvents(AppName("browser")).then(events)(HttpEvent.Outgoing(it)) })
        .then(ClientFilters.RequestTracing())
        .then(client)