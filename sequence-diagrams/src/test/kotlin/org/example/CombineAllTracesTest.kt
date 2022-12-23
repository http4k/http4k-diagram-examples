package org.example

import org.http4k.tracing.TraceRenderPersistence
import org.http4k.tracing.persistence.FileSystem
import org.http4k.tracing.renderer.PumlSequenceDiagram
import org.junit.jupiter.api.Test
import java.io.File

class CombineAllTracesTest {

    @Test
    fun `combine into one list`() {
        FILE_TRACE_PERSISTENCE.load().forEach {
            TraceRenderPersistence.FileSystem(File("build/foo"))(
                PumlSequenceDiagram.render(it.name, it.traces)
            )
        }
    }
}
