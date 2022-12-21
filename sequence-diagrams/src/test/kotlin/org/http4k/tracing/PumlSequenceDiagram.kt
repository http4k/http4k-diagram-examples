package org.http4k.tracing

object PumlSequenceDiagram : TraceStepRenderer {
    override fun render(scenarioName: String, steps: List<TraceStep>): TraceRender {

        val actors = steps.filterIsInstance<CallTree>()
            .flatMap { it.actors() }
            .toSet()
            .sorted()

        return TraceRender(
            "$scenarioName - Sequence",
            """@startuml
            |title $scenarioName
            |${actors.toPumlActor().joinToString("\n")}
            |${
                steps.joinToString("\n") {
                    when (it) {
                        is HttpCallTree -> it.asPumlSequenceDiagram()
                        is DatabaseCallTree -> it.asPumlSequenceDiagram()
                        is StartInteraction -> it.asPumlSequenceDiagram()
                        is StartRendering, is StopRendering -> ""
                        else -> ""
                    }
                }
            }
    @enduml""".trimMargin())
    }

    private fun CallTree.actors(): Set<TraceActor> =
        (listOf(originActor(), targetActor()) + children.flatMap { it.actors() }).toSet()

    private fun Iterable<TraceActor>.toPumlActor() =
        fold(emptyList<String>()) { acc, next ->
            val nextVal = when (next) {
                is TraceActor.Database -> "database"
                is TraceActor.External -> "participant"
                is TraceActor.Internal -> "participant"
                is TraceActor.Person -> "participant"
            } + " ${next.name}"
            if (acc.contains(nextVal)) acc else acc + nextVal
        }

    private fun CallTree.asPumlSequenceDiagram() = when (this) {
        is HttpCallTree -> asPumlSequenceDiagram()
        is DatabaseCallTree -> asPumlSequenceDiagram()
        else -> ""
    }

    private fun HttpCallTree.asPumlSequenceDiagram(): String = """
           |${origin()} -> ${target()}: $method ${uri.path} ${describeHeaders()}
           |activate ${target()}
           |${children.joinToString("\n") { it.asPumlSequenceDiagram() }}
           |${target()} --> ${origin()}: $status
           |deactivate ${target()}
            """.trimMargin()

    private fun HttpCallTree.describeHeaders() = headers
        .filter(InterestingHeadersOnly)
        .takeIf { it.isNotEmpty() }?.joinToString(prefix = "[", postfix = "]") ?: ""

    private fun DatabaseCallTree.asPumlSequenceDiagram(): String = """
           |${origin()} <-> ${target()}: ${describe()}
            """.trimMargin()

    private fun StartInteraction.asPumlSequenceDiagram(): String = """
        
        note over $origin : $origin $interactionName
        """.trimIndent()
}
