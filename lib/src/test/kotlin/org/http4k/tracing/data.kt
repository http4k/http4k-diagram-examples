package org.http4k.tracing

import org.http4k.tracing.ActorType.Database
import org.http4k.tracing.ActorType.Person
import org.http4k.tracing.ActorType.System

val c_to_external = RequestResponse(
    "c",
    "external",
    TraceActor("c", System),
    TraceActor("external", System),
    "c-to-external req",
    "c-to-external resp",
    listOf()
)

val bidi_b = BiDirectional(
    "b",
    "db",
    TraceActor("b", System),
    TraceActor("db", Database),
    "bidi-b req-resp",
    listOf()
)

val b_to_c = RequestResponse(
    "b",
    "c",
    TraceActor("b", System),
    TraceActor("c", System),
    "b-to-c req",
    "b-to-c resp",
    listOf(bidi_b, c_to_external)
)

val fireAndForget_user1 = FireAndForget(
    "user1",
    "events",
    TraceActor("user1", Person),
    TraceActor("events", System),
    "event a",
    listOf()
)

val entire_trace_1 = RequestResponse(
    "user1",
    "b",
    TraceActor("user1", Person),
    TraceActor("b", System),
    "init 1 req",
    "init 2 resp",
    listOf(fireAndForget_user1, b_to_c)
)

val fireAndForget_d = FireAndForget(
    "d",
    "events",
    TraceActor("d", System),
    TraceActor("events", System),
    "event d",
    listOf()
)

val entire_trace_2 = RequestResponse(
    "user2",
    "d",
    TraceActor("user2", Person),
    TraceActor("d", System),
    "init 2 req",
    "init 2 resp",
    listOf(fireAndForget_d)
)
