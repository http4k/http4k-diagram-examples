package org.http4k.tracing.renderer

import org.http4k.tracing.Trace
import org.http4k.tracing.TraceActor

/**
 * Returns all distinct actors in the list of Traces, so that they appear in chronological
 * order as to their position in the overall flow.
 */
fun List<Trace>.chronologicalActors(): List<TraceActor> {
    val origins = map { it.originActor } + flatMap { it.children.flatMap(Trace::origins) }
    val targets = map { it.targetActor } + flatMap { it.children.flatMap(Trace::targets) }
    return (origins + targets).distinct()
}

private fun Trace.origins(): List<TraceActor> = listOf(originActor) + children.flatMap(Trace::origins)
private fun Trace.targets(): List<TraceActor> = listOf(targetActor) + children.flatMap(Trace::targets)

internal fun String.identifier() = filter { it.isLetterOrDigit() }
