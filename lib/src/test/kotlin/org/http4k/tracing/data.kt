package org.http4k.tracing

import org.http4k.tracing.ActorType.Database
import org.http4k.tracing.ActorType.Human
import org.http4k.tracing.ActorType.System

val c_to_external = RequestResponse(
    Actor("c", System),
    Actor("external", System),
    "c-to-external req",
    "c-to-external resp",
    listOf()
)

val bidi_b = BiDirectional(
    Actor("b", System),
    Actor("db", Database),
    "bidi-b req-resp",
    listOf()
)

val b_to_c = RequestResponse(
    Actor("b", System),
    Actor("c", System),
    "b-to-c req",
    "b-to-c resp",
    listOf(bidi_b, c_to_external)
)

val fireAndForget_user1 = FireAndForget(
    Actor("user1", Human),
    Actor("events", System),
    "event a",
    listOf()
)

val entire_trace_1 = RequestResponse(
    Actor("user1", Human),
    Actor("b", System),
    "init 1 req",
    "init 2 resp",
    listOf(fireAndForget_user1, b_to_c)
)

val fireAndForget_d = FireAndForget(
    Actor("d", System),
    Actor("events", System),
    "event d",
    listOf()
)

val entire_trace_2 = RequestResponse(
    Actor("user2", Human),
    Actor("d", System),
    "init 2 req",
    "init 2 resp",
    listOf(fireAndForget_d)
)
