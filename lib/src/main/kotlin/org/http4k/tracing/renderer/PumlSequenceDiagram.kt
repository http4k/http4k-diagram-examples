package org.http4k.tracing.renderer

import org.http4k.tracing.BiDirectional
import org.http4k.tracing.Event
import org.http4k.tracing.RequestResponse
import org.http4k.tracing.StartInteraction
import org.http4k.tracing.StartRendering
import org.http4k.tracing.StopRendering
import org.http4k.tracing.Trace
import org.http4k.tracing.TraceActor
import org.http4k.tracing.TraceRender
import org.http4k.tracing.TraceRenderer
import org.http4k.tracing.TraceStep

object PumlSequenceDiagram : TraceRenderer {
    override fun render(scenarioName: String, steps: List<TraceStep>): TraceRender {
        val actors = steps.filterIsInstance<Trace>()
            .flatMap { it.actors() }
            .toSet()
            .sorted()

        return TraceRender(
            "$scenarioName - Sequence",
            "PUML",
            """@startuml
            |title $scenarioName
            |${actors.toPumlActor().joinToString("\n")}
            |${
                steps.joinToString("\n") {
                    when (it) {
                        is RequestResponse -> it.asPumlSequenceDiagram()
                        is BiDirectional -> it.asPumlSequenceDiagram()
                        is Event -> it.asPumlSequenceDiagram()
                        is StartInteraction -> it.asPumlSequenceDiagram()
                        is StartRendering, is StopRendering -> ""
                    }
                }
            }
    @enduml""".trimMargin())
    }

    private fun Trace.actors(): Set<TraceActor> =
        (listOf(originActor, targetActor) + children.flatMap { it.actors() }).toSet()

    private fun Iterable<TraceActor>.toPumlActor() =
        fold(emptyList<String>()) { acc, next ->
            val nextVal = when (next) {
                is TraceActor.Database -> "database"
                else -> "participant"
            } + " \"${next.name}\""
            if (acc.contains(nextVal)) acc else acc + nextVal
        }

    private fun Trace.asPumlSequenceDiagram() = when (this) {
        is RequestResponse -> asPumlSequenceDiagram()
        is BiDirectional -> asPumlSequenceDiagram()
        is Event -> asPumlSequenceDiagram()
    }

    private fun RequestResponse.asPumlSequenceDiagram(): String = """
           |"$origin" -> "$target": $request
           |activate "$target"
           |${children.joinToString("\n") { it.asPumlSequenceDiagram() }}
           |"$target" --> "$origin": $response
           |deactivate "$target"
            """.trimMargin()

    private fun BiDirectional.asPumlSequenceDiagram(): String = """
           |"$origin" <-> "$target": $request
            """.trimMargin()

    private fun Event.asPumlSequenceDiagram(): String = """
           |"$origin" -> "$target": $request
            """.trimMargin()

    private fun StartInteraction.asPumlSequenceDiagram(): String = """
        
        note over "$origin" : "$origin" $interactionName
        """.trimIndent()
}

