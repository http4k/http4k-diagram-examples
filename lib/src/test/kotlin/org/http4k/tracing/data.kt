package org.http4k.tracing

import org.http4k.tracing.TraceActor.Internal

val c_to_external = RequestResponse(
    "c",
    "external",
    Internal("c"),
    TraceActor.External("external"),
    "c-to-external req",
    "c-to-external resp",
    listOf()
)

val bidi_b = BiDirectional(
    "b",
    "db",
    Internal("b"),
    TraceActor.Database("db"),
    "bidi-b req-resp",
    listOf()
)

val b_to_c = RequestResponse(
    "b",
    "c",
    Internal("b"),
    Internal("c"),
    "b-to-c req",
    "b-to-c resp",
    listOf(bidi_b, c_to_external)
)

val event_a = Event(
    "a",
    "events",
    Internal("a"),
    TraceActor.Events("events"),
    "event a",
    listOf()
)

val entire_trace = RequestResponse(
    "a",
    "b",
    Internal("a"),
    Internal("b"),
    "req",
    "resp",
    listOf(event_a, b_to_c)
)
