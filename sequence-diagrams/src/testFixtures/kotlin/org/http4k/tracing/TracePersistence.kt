package org.http4k.tracing

import org.http4k.tracing.renderer.TraceRender
import java.io.File
import java.util.Locale.getDefault

fun interface TracePersistence {
    operator fun invoke(render: TraceRender)

    companion object {
        fun FileSystem(dir: File = File(".generated/diagrams")) = TracePersistence {
            File(dir.apply { mkdirs() }, "${it.title}.${it.identifier.lowercase(getDefault())}").writeText(it.content)
        }
    }
}