package org.http4k.tracing.renderer

import org.http4k.tracing.TraceStep

fun interface TraceRenderer {
    fun render(scenarioName: String, steps: List<TraceStep>): TraceRender
}

data class TraceRender(
    val title: String,
    val content: String,
    val identifier: String
)
