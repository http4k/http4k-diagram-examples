package org.http4k.tracing

import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.events.Event
import org.http4k.tracing.TraceActor.Database
import org.http4k.tracing.TraceActor.Internal
import org.http4k.tracing.TraceActor.Person


sealed class TraceActor(private val index: Int) : Comparable<TraceActor> {
    override fun compareTo(other: TraceActor) = index.compareTo(other.index)

    abstract val name: String

    data class Person(override val name: String) : TraceActor(1)
    data class Database(override val name: String) : TraceActor(3)
    data class Internal(override val name: String) : TraceActor(2)
    data class External(override val name: String) : TraceActor(4)
}

interface CallTree {
    val origin: String
    val target: String
    val originActor: TraceActor
    val targetActor: TraceActor
    val request: String
    val response: String
    val children: List<CallTree>
}

private val interestingHeaders = setOf("Authorization", "User-Agent", "Cookie", "Set-Cookie", "RequesterId")

object InterestingHeadersOnly : (String) -> Boolean by { it in interestingHeaders }

interface TraceStep

class HttpCallTree(
    override val origin: String,
    originating: Boolean,
    uri: Uri,
    method: Method,
    status: Status,
    override val children: List<CallTree>,
    val headers: List<String> = emptyList()
) : CallTree, TraceStep {
    override val target = uri.host
    override val request = method.name + " " + uri.path
    override val response = status.toString()
    override val originActor = if (originating) Person(origin) else Internal(origin)
    override val targetActor = Internal(uri.host)
}

class DatabaseCallTree(
    override val origin: String,
    methodName: String,
) : CallTree, TraceStep {
    override val target = "db"
    override val request = methodName
    override val response = ""
    override val originActor = Internal(origin)
    override val targetActor = Database(target)
    override val children = emptyList<CallTree>()
}


data class StartInteraction(
    val origin: String,
    val interactionName: String
) : TraceStep, Event

object StartRendering : TraceStep, Event
object StopRendering : TraceStep, Event

