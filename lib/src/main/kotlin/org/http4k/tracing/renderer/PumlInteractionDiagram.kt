package org.http4k.tracing.renderer

import org.http4k.tracing.Trace
import org.http4k.tracing.TraceActor
import org.http4k.tracing.TraceRender
import org.http4k.tracing.TraceRenderer
import org.http4k.tracing.TraceStep

object PumlInteractionDiagram : TraceRenderer {
    override fun render(scenarioName: String, steps: List<TraceStep>): TraceRender {
        val calls = steps.filterIsInstance<Trace>()
        val actors = calls.map { it.originActor }.toSet().sorted()
        val systems = (calls.flatMap { it.parties() } - actors.toSet()).toSet().sorted()

        val all = (actors + systems).toSet().sorted()

        val relations = calls.flatMap { it.relations() }.toSet()

        return TraceRender(
            "$scenarioName - Interactions",
            "PUML",
            """@startuml
title $scenarioName

!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml


${all.toPumlActor().joinToString("\n")}    
${relations.joinToString("\n") { "Rel_D(${it.origin.identifier()}, ${it.target.identifier()}, \" \") " }}    
@enduml""".trimMargin()
        )
    }

    private fun Iterable<TraceActor>.toPumlActor() =
        fold(emptyList<String>()) { acc, it ->
            val nextVal = when (it) {
                is TraceActor.Database -> "ContainerDb"
                is TraceActor.Person -> "Person"
                else -> "System"
            } + "(${it.name.identifier()}, \"${it.name}\")"

            if (acc.contains(nextVal)) acc else acc + nextVal
        }

    private fun Trace.parties(): List<TraceActor> =
        listOf(originActor, targetActor) + children.flatMap { it.parties() }

    private fun Trace.relations(): List<Call> =
        listOf(Call(origin, target)) + children.flatMap { it.relations() }

    private data class Call(val origin: String, val target: String)
}