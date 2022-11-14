package org.example.oauth

import org.example.proxiedOutbound
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.NoOp
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.routing.reverseProxy
import org.http4k.tracing.AppIncomingHttp
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
            "relying-party" to proxiedOutbound(events, relyingParty, "relying-party"),
            "auth-server" to proxiedOutbound(events, authServer, "auth-server")
        )
    )