package org.http4k.tracing

import org.http4k.core.Method
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.events.Event
import org.http4k.tracing.TraceActor.Database
import org.http4k.tracing.TraceActor.Internal
import org.http4k.tracing.TraceActor.Person
import java.util.Locale


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
    val describe: String
    val children: List<CallTree>
}

private val interestingHeaders = setOf("Authorization", "User-Agent", "Cookie", "Set-Cookie", "RequesterId")

object InterestingHeadersOnly : (String) -> Boolean by { it in interestingHeaders }

interface TraceStep

data class HttpCallTree(
    private val unfactoredOrigin: String,
    private val originating: Boolean,
    val uri: Uri,
    val method: Method,
    val status: Status,
    override val children: List<CallTree>,
    val headers: List<String> = emptyList(),
) : TraceStep, CallTree {
    override val origin = unfactoredOrigin.toLabel()
    override val target = uri.host.toLabel()
    override val describe = method.name + " " + uri.path
    override val originActor = if (originating) Person(origin) else Internal(origin)
    override val targetActor = Internal(target)
}

data class DatabaseCallTree(
    private val unfactoredOrigin: String,
    private val methodName: String,
) : TraceStep, CallTree {
    override val origin = unfactoredOrigin.toLabel()
    override val target = "db"
    override val describe = methodName
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

private fun String.toLabel() =
    (if (contains(".")) substringBefore('.') else this)
        .replace("-", " ")
        .replace("_", " ")
        .replace(Regex(""" +"""), " ")
        .trim()
        .split(" ")
        .map { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
        .joinToString("")
