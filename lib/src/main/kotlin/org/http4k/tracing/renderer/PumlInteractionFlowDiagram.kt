package org.http4k.tracing.renderer

import org.http4k.tracing.Trace
import org.http4k.tracing.TraceActor
import org.http4k.tracing.TraceRender
import org.http4k.tracing.TraceRenderer
import org.http4k.tracing.TraceStep
import org.http4k.tracing.chronologicalActors

object PumlInteractionFlowDiagram : TraceRenderer {
    override fun render(scenarioName: String, steps: List<TraceStep>): TraceRender {

        val traces = steps.filterIsInstance<Trace>()

        val relations = traces
            .flatMapIndexed { i, it -> it.relations(i + 1) }
            .toSet()

        return TraceRender(
            "$scenarioName - Flow",
            "PUML",
            """@startuml
title $scenarioName

!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml


${traces.chronologicalActors().toPumlActor().joinToString("\n")}    
${relations.joinToString("\n") { "Rel_D(${it.origin.identifier()}, ${it.target.identifier()}, \"${it.interaction}\")" }}    
@enduml""".trimMargin()

        )
    }

    private fun Iterable<TraceActor>.toPumlActor() =
        fold(emptyList<String>()) { acc, it ->
            val nextVal = when (it) {
                is TraceActor.Database -> "ContainerDb"
                is TraceActor.Person -> "Person"
                else -> "Container"
            } + "(${it.name.identifier()}, \"${it.name}\")"

            if (acc.contains(nextVal)) acc else acc + nextVal
        }

    private fun Trace.relations(baseCounter: Int): List<Call> =
        listOf(
            Call(
                origin,
                target,
                "$baseCounter. $request"
            )
        ) + children.flatMapIndexed { i, it -> it.relations(baseCounter + i + 1) }

    private data class Call(val origin: String, val target: String, val interaction: String)
}