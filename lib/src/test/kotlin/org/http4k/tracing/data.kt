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

val fireAndForget_user1 = FireAndForget(
    "user1",
    "events",
    Internal("user1"),
    TraceActor.Events("events"),
    "event a",
    listOf()
)

val entire_trace_1 = RequestResponse(
    "user1",
    "b",
    Internal("user1"),
    Internal("b"),
    "init 1 req",
    "init 2 resp",
    listOf(fireAndForget_user1, b_to_c)
)

val fireAndForget_d = FireAndForget(
    "d",
    "events",
    Internal("d"),
    TraceActor.Events("events"),
    "event d",
    listOf()
)

val entire_trace_2 = RequestResponse(
    "user2",
    "d",
    Internal("user2"),
    Internal("d"),
    "init 2 req",
    "init 2 resp",
    listOf(fireAndForget_d)
)
