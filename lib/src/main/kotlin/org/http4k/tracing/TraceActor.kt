package org.http4k.tracing

sealed class TraceActor(private val index: Int) : Comparable<TraceActor> {
    override fun compareTo(other: TraceActor) = index.compareTo(other.index)

    abstract val name: String

    data class Person(override val name: String) : TraceActor(1)
    data class Internal(override val name: String) : TraceActor(2)
    data class Database(override val name: String) : TraceActor(3)
    data class Events(override val name: String) : TraceActor(4)
    data class External(override val name: String) : TraceActor(5)
}