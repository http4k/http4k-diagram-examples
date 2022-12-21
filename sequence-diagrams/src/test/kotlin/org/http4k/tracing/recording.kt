package org.http4k.tracing

import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.events.Event
import org.http4k.tracing.TraceActor.Database
import org.http4k.tracing.TraceActor.Internal
import org.http4k.tracing.TraceActor.Person
import org.http4k.tracing.util.capitalize


sealed class TraceActor(private val index: Int) : Comparable<TraceActor> {
    override fun compareTo(other: TraceActor) = index.compareTo(other.index)

    abstract val name: String

    data class Person(override val name: String) : TraceActor(1)
    data class Database(override val name: String) : TraceActor(3)
    data class Internal(override val name: String) : TraceActor(2)
    data class External(override val name: String) : TraceActor(4)
}

abstract class CallTree(
    val origin: String,
    val target: String,
    val originActor: TraceActor,
    val targetActor: TraceActor,
    val describe: String,
    val children: List<CallTree>
)

private val interestingHeaders = setOf("Authorization", "User-Agent", "Cookie", "Set-Cookie", "RequesterId")

object InterestingHeadersOnly : (String) -> Boolean by { it in interestingHeaders }

interface TraceStep

class HttpCallTree(
    origin: String,
    originating: Boolean,
    val uri: Uri,
    val method: Method,
    val status: Status,
    children: List<CallTree>,
    val headers: List<String> = emptyList()
) : CallTree(
    origin = origin.toLabel(),
    target = uri.host.toLabel(),
    describe = method.name + " " + uri.path,
    originActor = if (originating) Person(origin.toLabel()) else Internal(origin.toLabel()),
    targetActor = Internal(uri.host.toLabel()),
    children = children
), TraceStep

class DatabaseCallTree(
    origin: String,
    methodName: String,
) : CallTree(
    origin = origin.toLabel(),
    target = "db",
    describe = methodName,
    originActor = Internal(origin),
    targetActor = Database("db"),
    children = emptyList(),
), TraceStep

data class StartInteraction(
    val origin: String,
    val interactionName: String
) : TraceStep, Event

object StartRendering : TraceStep, Event
object StopRendering : TraceStep, Event

private fun String.toLabel() =
    (if (contains(".")) substringBefore('.') else this)
        .replace("-", " ")
        .replace("_", " ")
        .replace(Regex(" +"), " ")
        .trim()
        .split(" ")
        .joinToString("", transform = String::capitalize)
