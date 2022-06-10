package org.http4k.tracing

fun interface TraceStepRenderer {
    fun render(scenarioName: String, steps: List<TraceStep>): TraceRender
}

data class TraceRender(val title: String, val content: String)
