package org.http4k.tracing

import org.http4k.events.Event


sealed class TraceActor(private val index: Int) : Comparable<TraceActor> {
    override fun compareTo(other: TraceActor) = index.compareTo(other.index)

    abstract val name: String

    data class Person(override val name: String) : TraceActor(1)
    data class Internal(override val name: String) : TraceActor(2)
    data class Database(override val name: String) : TraceActor(3)
    data class Events(override val name: String) : TraceActor(4)
    data class External(override val name: String) : TraceActor(5)
}

interface Trace {
    val origin: String
    val target: String
    val originActor: TraceActor
    val targetActor: TraceActor
    val request: String
    val response: String
    val children: List<Trace>
}

interface TraceStep

data class StartInteraction(
    val origin: String,
    val interactionName: String
) : TraceStep, Event

object StartRendering : TraceStep, Event
object StopRendering : TraceStep, Event

