package org.example

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.tracing.AppIncomingHttp

fun pingApp() = AppIncomingHttp()
    .then(routes(
        "/ping" bind GET to {
            Response(OK).body("pong")
        }
    ))

fun indirectPingApp(pingClient: HttpHandler) = AppIncomingHttp()
    .then(routes(
        "/indirect-ping" bind GET to {
            pingClient(Request(GET, "https://a-server/ping"))
        }
    ))

fun main() {
    val printingApp: HttpHandler = PrintRequest().then(pingApp())
    val server = printingApp.asServer(Undertow(9000)).start()
    println("Server started on " + server.port())
}
