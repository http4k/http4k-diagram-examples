package org.http4k.tracing.persistence

import org.http4k.tracing.TraceRenderPersistence
import java.io.File
import java.util.Locale

fun TraceRenderPersistence.Companion.FileSystem(dir: File) = TraceRenderPersistence {
    File(dir.apply { mkdirs() }, "${it.title}.${it.format.lowercase(Locale.getDefault())}").writeText(it.content)
}