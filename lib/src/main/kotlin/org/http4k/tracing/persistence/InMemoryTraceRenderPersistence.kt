package org.http4k.tracing.persistence

import org.http4k.tracing.TraceRender
import org.http4k.tracing.TraceRenderPersistence

class InMemoryTraceRenderPersistence : TraceRenderPersistence, Iterable<TraceRender> {
    private val renders = mutableListOf<TraceRender>()

    override fun invoke(render: TraceRender) {
        renders += render
    }

    override fun iterator() = renders.iterator()
}