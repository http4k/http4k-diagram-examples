package org.http4k.tracing

sealed interface TraceActor {
    val name: String

    data class Person(override val name: String) : TraceActor
    data class Internal(override val name: String) : TraceActor
    data class Database(override val name: String) : TraceActor
    data class Events(override val name: String) : TraceActor
    data class External(override val name: String) : TraceActor
}