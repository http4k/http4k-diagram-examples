package org.example

import org.http4k.tracing.renderer.PumlInteractionDiagram
import org.junit.jupiter.api.Test

class CombineAllTracesTest {

    @Test
    fun `combine into one list`() {
        FILE_TRACE_PERSISTENCE.load().forEach {
            FILE_TRACE_RENDER_PERSISTENCE(PumlInteractionDiagram.render(it.name, it.traces))
        }
    }
}
