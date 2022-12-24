package org.http4k.tracing.persistence

import org.http4k.tracing.NamedTrace
import org.http4k.tracing.TracePersistence
import org.http4k.tracing.persistence.TraceMoshi.asA
import org.http4k.tracing.persistence.TraceMoshi.asFormatString
import org.http4k.tracing.persistence.TraceMoshi.prettify
import java.io.File

fun TracePersistence.Companion.FileSystem(dir: File) = object : TracePersistence {
    override fun store(trace: NamedTrace) {
        File(dir.apply { mkdirs() }, trace.name + TRACE_SUFFIX)
            .writeText(prettify(asFormatString(trace)))
    }

    override fun load() = dir.list()
        ?.filter { it.endsWith(TRACE_SUFFIX) }
        ?.map { asA<NamedTrace>(File(dir, it).readText()) }
        ?: emptyList()

    private val TRACE_SUFFIX = ".trace.json"
}

